package ru.mephi.voip.ui.caller

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import ru.mephi.shared.data.model.CallRecord
import ru.mephi.shared.data.sip.AccountStatus
import ru.mephi.shared.vm.CallerViewModel
import ru.mephi.voip.R
import ru.mephi.voip.data.AccountStatusRepository
import ru.mephi.voip.ui.call.CallActivity
import ru.mephi.voip.ui.caller.list.CallRecordsList
import ru.mephi.voip.ui.caller.list.NumberHistoryList
import ru.mephi.voip.ui.caller.numpad.NumPad
import timber.log.Timber

@Composable
fun CallerScreen(
    isPermissionGranted: Boolean = false,
    navController: NavController,
    callerNumberArg: String? = null,
    callerNameArg: String? = null,
) {
    val accountStatusRepository: AccountStatusRepository by inject()
    val viewModel: CallerViewModel by inject()

    var inputState by remember { mutableStateOf("") }
    var isNumPadStateUp by remember { mutableStateOf(false) }
    var isBackArrowVisible by remember { mutableStateOf(false) }
    val callerNumber by remember { mutableStateOf(callerNumberArg) }
    val callerName by remember { mutableStateOf(callerNameArg) }
    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    BackHandler {
        navController.popBackStack()
    }

    if (callerNumber.isNullOrEmpty()) {
        isBackArrowVisible = false
    } else {
        isBackArrowVisible = true
        isNumPadStateUp = true

        if (inputState.isEmpty()) {
            if (!inputState.contains(callerNumber!!))
                inputState = callerNumber!!
        }
    }

    Column {
        TopAppBar(
            backgroundColor = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isBackArrowVisible)
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    } else
                    Image(
                        painter = painterResource(id = R.drawable.logo_mephi),
                        contentDescription = "лого",
                    )

                Text(
                    text = "Звонки", style = TextStyle(color = Color.Black, fontSize = 20.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
                AccountStatusWidget(
                    accountStatusRepository,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        val scope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {
            val (selectedRecord, setSelectedRecord) = remember {
                mutableStateOf<CallRecord?>(null)
            }
            Column(modifier = Modifier.fillMaxSize()) {
                StatusBar()
                CallRecordsList(setSelectedRecord) { deletedRecord ->
                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        val snackBarResult = snackBarHostState.showSnackbar(
                            "Запись ${deletedRecord.sipNumber} удалена",
                            actionLabel = "Вернуть"
                        )
                        when (snackBarResult) {
                            SnackbarResult.Dismissed -> Timber.d("SnackBar dismissed")
                            SnackbarResult.ActionPerformed -> viewModel.addRecord(
                                deletedRecord
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                callerName?.let {
                    AnimatedVisibility(
                        visible = inputState == callerNumber && inputState.isNotEmpty(),
                        enter = slideInVertically() + expandVertically()
                                + fadeIn(initialAlpha = 0.3f),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedButton(
                            onClick = {}, modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(id = R.color.colorGreen)
                            ),
                            elevation = ButtonDefaults.elevation()
                        ) {
                            Text(text = it, color = Color.White)
                        }
                    }
                }
                NumPad(
                    11, inputState, isNumPadStateUp,
                    onLimitExceeded = {
                        scope.launch {
                            snackBarHostState.showSnackbar("Превышен размер номера")
                        }
                    },
                    onNumPadStateChange = {
                        if (callerNumber.isNullOrEmpty())
                            isNumPadStateUp = !isNumPadStateUp
                        else
                            navController.popBackStack()
                    },
                    onInputStateChanged = {
                        inputState = it
                    }
                )
                FloatingActionButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.End), onClick = {
                        if (isPermissionGranted)
                            if (isNumPadStateUp) {
                                if (inputState.length <= 3) {
                                    scope.launch {
                                        snackBarHostState.showSnackbar("Слишком короткий номер")
                                    }
                                    return@FloatingActionButton
                                }
                                if (accountStatusRepository.status.value == AccountStatus.REGISTERED) {
                                    CallActivity.create(
                                        context,
                                        inputState,
                                        false
                                    )
                                } else {
                                    scope.launch {
                                        snackBarHostState.showSnackbar("Нет активного аккаунта для совершения звонка")
                                    }
                                }
                            } else {
                                isNumPadStateUp = !isNumPadStateUp
                            }
                    }, backgroundColor = colorResource(id = R.color.colorGreen)
                ) {
                    if (isNumPadStateUp)
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "",
                            tint = colorResource(
                                id = if (isPermissionGranted)
                                    R.color.colorPrimary else R.color.colorGray
                            )
                        ) else
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_dialpad_24),
                            contentDescription = "",
                            tint = colorResource(
                                id = if (isPermissionGranted)
                                    R.color.colorPrimary else R.color.colorGray
                            )
                        )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visibleState = MutableTransitionState(selectedRecord != null),
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                NumberHistoryList(
                    callRecord = selectedRecord!!,
                    callsHistory = viewModel.getAllCallsBySipNumber(selectedRecord.sipNumber)
                        .executeAsList(),
                    setSelectedRecord
                )
            }
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}