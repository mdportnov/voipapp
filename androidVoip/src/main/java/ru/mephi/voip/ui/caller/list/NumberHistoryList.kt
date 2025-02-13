package ru.mephi.voip.ui.caller.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import org.koin.androidx.compose.inject
import ru.mephi.shared.data.model.CallRecord
import ru.mephi.shared.data.model.CallStatus
import ru.mephi.shared.data.network.GET_PROFILE_PIC_URL_BY_SIP
import ru.mephi.shared.vm.CallerViewModel
import ru.mephi.voip.R
import ru.mephi.voip.utils.*


@Composable
fun NumberHistoryList(
    selectedRecord: CallRecord?,
    setSelectedRecord: (CallRecord?) -> Unit
) {
    val viewModel: CallerViewModel by inject()
    selectedRecord?.let {
        val callsHistory: List<CallRecord> =
            viewModel.getAllCallsBySipNumber(selectedRecord.sipNumber).executeAsList()

        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(data = GET_PROFILE_PIC_URL_BY_SIP + selectedRecord.sipNumber)
                .apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    diskCachePolicy(CachePolicy.ENABLED)
                }).build()
        )
        Card(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(5.dp), elevation = 10.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Spacer(modifier = Modifier.width(50.dp))
                    Image(
                        painter = painter,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                    )

                    IconButton(onClick = {
                        setSelectedRecord(null)
                    }, modifier = Modifier.padding(end = 10.dp)) {
                        Icon(
                            Icons.Sharp.Close,
                            tint = Color.LightGray,
                            contentDescription = "Закрыть"
                        )
                    }
                }

                Text(
                    text = selectedRecord.sipNumber,
                    style = TextStyle(color = Color.Black, fontSize = 30.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                selectedRecord.sipName?.let {
                    if (it != selectedRecord.sipNumber)
                        Text(
                            text = it, style = TextStyle(color = Color.Black, fontSize = 30.sp),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items = callsHistory) { callItem ->
                        CallItem(callRecord = callItem)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun CallItemPreview() =
    CallItem(
        callRecord = CallRecord(
            1,
            "09024",
            "Портнов М.Д.",
            CallStatus.INCOMING,
            1648133547,
            10000
        )
    )

@Composable
fun ImageCallStatus(status: CallStatus) {
    Icon(
        imageVector = when (status) {
            CallStatus.INCOMING -> Icons.Default.CallReceived
            CallStatus.OUTCOMING -> Icons.Default.CallMade
            CallStatus.MISSED -> Icons.Default.CallMissed
            CallStatus.DECLINED_FROM_SIDE -> Icons.Default.CallEnd
            CallStatus.DECLINED_FROM_YOU -> Icons.Default.CallMissedOutgoing
            CallStatus.NONE -> Icons.Default.Info
        },
        tint = when (status) {
            CallStatus.INCOMING -> Color.Blue
            CallStatus.OUTCOMING -> ColorGreen
            CallStatus.MISSED -> ColorAccent
            CallStatus.DECLINED_FROM_SIDE -> ColorAccent
            CallStatus.DECLINED_FROM_YOU -> ColorGray
            CallStatus.NONE -> ColorGray
        },
        contentDescription = "",
        modifier = Modifier.padding(end = 10.dp)
    )
}

@Composable
fun CallItem(callRecord: CallRecord) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ImageCallStatus(callRecord.status)

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = callRecord.time.stringFromDate(),
                style = TextStyle(color = colorResource(id = R.color.colorAccent))
            )
            Text(
                text = callRecord.status.text + ", " + callRecord.duration.durationStringFromMillis(),
                style = TextStyle(color = Color.Gray)
            )
        }
    }
}