@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package ru.mephi.voip.ui.home.screens.profile.common.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import org.koin.androidx.compose.get
import ru.mephi.shared.data.model.Account
import ru.mephi.voip.R
import ru.mephi.voip.data.PhoneManager
import ru.mephi.voip.ui.MasterActivity
import ru.mephi.voip.utils.getImageUrl

@Composable
internal fun ManageAccountsDialog(
    onDismiss: () -> Unit,
    openAddAccountDialog: () -> Unit,
    phoneManager: PhoneManager = get()
) {
    val activity = LocalContext.current as MasterActivity
    val accountsList = phoneManager.accountsList.collectAsState()
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
        ) {
            Scaffold(
                modifier = Modifier
                    .padding(top = 34.dp, bottom = 42.dp, start = 10.dp, end = 10.dp)
                    .background(color = Color.Transparent)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = false) { },
                topBar = {
                    SmallTopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = { onDismiss() }) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                            }
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text("Добавить аккаунт") },
                        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                        onClick = { if (activity.checkNonGrantedPermissions()) openAddAccountDialog() })
                }
            ) {
                Surface(
                    modifier = Modifier.padding(it)
                ) {
                    AccountsList(accountsList.value, onDismiss)
                }
            }
        }
    }
}

@Composable
private fun AccountsList(
    accountsList: List<Account>,
    onDismiss: () -> Unit
) {
    if (accountsList.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(items = accountsList) { _, item ->
                SavedAccountItem(item, onDismiss)
            }
        }
    } else {
        AccountsListEmpty()
    }
}

@Composable
private fun AccountsListEmpty() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(128.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ).toSpanStyle()
                    ) {
                        append("У вас нет сохраннёных аккаунтов!")
                    }
                },
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
        }
    }
}

@Composable
private fun SavedAccountItem(
    account: Account,
    onDismiss: () -> Unit,
    accountRepo: PhoneManager = get(),
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(top = 4.dp, start = 4.dp, end = 4.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { accountRepo.setActiveAccount(account); onDismiss() },
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getImageUrl(account.login))
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 12.dp)
                    .size(50.dp)
                    .clip(CircleShape),
                error = painterResource(id = R.drawable.ic_dummy_avatar),
                placeholder = painterResource(id = R.drawable.ic_dummy_avatar),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width((LocalConfiguration.current.screenWidthDp - 156).dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = account.displayedName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Номер: ${account.login}",
                    maxLines = 1,
                    overflow  = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
                val current = accountRepo.currentAccount.collectAsState()
                Text(
                    text = if (current.value.login == account.login) "Вход выполнен" else "Без входа",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.secondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                onClick = { onDismiss(); accountRepo.removeAccount(account) }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }

        }
    }
}
