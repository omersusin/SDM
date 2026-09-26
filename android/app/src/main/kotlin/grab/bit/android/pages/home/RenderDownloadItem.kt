package grab.bit.android.pages.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Unbounded
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import grab.bit.shared.singledownloadpage.createStatusStringWithReason
import grab.bit.shared.ui.widget.CheckBox
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.*
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.shared.util.ui.*
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.myShapes
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.downloader.downloaditem.DownloadJobStatus
import grab.bit.downloader.monitor.CompletedDownloadItemState
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import grab.bit.downloader.monitor.statusOrFinished
import grab.bit.downloader.utils.ExceptionUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val PROGRESS_HEIGHT = 6

@Composable
fun RenderDownloadItem(
    checked: Boolean?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    downloadItem: IDownloadItemState,
    errorReason: DownloadErrorReason?,
    fileIconProvider: FileIconProvider,
    modifier: Modifier,
) {
    Row(
        modifier
    ) {
        WithContentColor(
            myColors.onSurface,
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .let {
                        if (checked == true) {
                            val selectionColor = myColors.onBackground
                            it.background(myColors.selectionGradient(0.15f, 0.03f, selectionColor))
                        } else {
                            it.border(1.dp, Color.Transparent)
                        }
                    }
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedVisibility(
                        checked != null
                    ) {
                        Row {
                            val isChecked = checked ?: false
                            CheckBox(
                                value = isChecked,
                                onValueChange = { onLongClick() },
                                size = 18.dp,
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                    RenderFileIcon(
                        downloadItem = downloadItem,
                        fileIconProvider = fileIconProvider,
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            downloadItem.name,
                            maxLines = 1,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderProgressBar(
                                downloadItem, Modifier
                                    .weight(1f)
                                    .height(PROGRESS_HEIGHT.dp)
                            )
                            if (downloadItem is ProcessingDownloadItemState) {
                                Spacer(Modifier.width(2.dp))
                                RenderProgressLight(downloadItem)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                RenderSubTexts(downloadItem, errorReason)
            }
        }
    }
}

@Composable
fun RenderProgressLight(itemState: IDownloadItemState) {
    val color = when (val status = itemState.statusOrFinished()) {
        is DownloadJobStatus.IsActive -> {
            myColors.primaryGradient
        }

        is DownloadJobStatus.CanBeResumed -> {
            if (status is DownloadJobStatus.Canceled && !ExceptionUtils.isNormalCancellation(status.e)) {
                myColors.errorGradient
            } else {
                myColors.warningGradient
            }
        }

        DownloadJobStatus.Finished -> {
            myColors.successGradient
        }
    }
    Box(
        modifier = Modifier
            .size((PROGRESS_HEIGHT).dp)
            .background(color, CircleShape),
    )
}

@Composable
fun RenderSubTexts(itemState: IDownloadItemState, errorReason: DownloadErrorReason?) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(fontSize = myTextSizes.xs),
        LocalContentAlpha provides 0.8f
    ) {
        Box(
            Modifier.fillMaxWidth()
        ) {
            RenderLeftSubText(itemState, Modifier.align(Alignment.CenterStart))
            RenderCenterSubText(itemState, errorReason, Modifier.align(Alignment.Center))
            RenderRightSubText(itemState, Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
private fun RenderEta(itemState: ProcessingDownloadItemState, modifier: Modifier) {
    val eta = remember(itemState.remainingTime) {
        itemState.remainingTime?.let {
            convertTimeRemainingToHumanReadable(
                it,
                TimeNames.ShortNames
            )
        }.orEmpty()
    }
    Text(eta, modifier)
}

@OptIn(ExperimentalTime::class)
@Composable
private fun RenderAddedTime(itemState: IDownloadItemState, modifier: Modifier) {
    var dateAddedString by remember { mutableStateOf("") }
    val useRelativeDateTime = LocalUseRelativeDateTime.current

    LaunchedEffect(
        itemState.dateAdded,
        useRelativeDateTime,
    ) {
        val instant = Instant.fromEpochMilliseconds(itemState.dateAdded)
        if (useRelativeDateTime) {
            while (isActive) {
                val now = Clock.System.now()
                val period = now.periodUntil(instant, TimeZone.UTC)
                val relativeTime = prettifyRelativeTime(period)
                dateAddedString = relativeTime
                // ponytail: 30s tick; minute-level labels don't need 1s refresh x N rows
                delay(30.seconds)
            }
        } else {
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            dateAddedString = dateTime.format(MyDateAndTimeFormats.fullDateTime)
        }
    }
    Text(dateAddedString, modifier)
}

@Composable
fun RenderRightSubText(itemState: IDownloadItemState, modifier: Modifier) {
    if (itemState is ProcessingDownloadItemState && itemState.status is DownloadJobStatus.IsActive) {
        RenderEta(itemState, modifier)
    } else {
        RenderAddedTime(itemState, modifier)
    }
}

@Composable
fun RenderCenterSubText(itemState: IDownloadItemState, errorReason: DownloadErrorReason?, modifier: Modifier) {
    if (itemState is ProcessingDownloadItemState) {
        if (itemState.status is DownloadJobStatus.IsActive) {
            RenderSpeed(itemState.speed, modifier)
        } else {
            RenderTextStatus(itemState, errorReason, modifier)
        }
    }
}

@Composable
fun RenderTextStatus(
    itemState: IDownloadItemState,
    errorReason: DownloadErrorReason?,
    modifier: Modifier,
) {
    val status = createStatusStringWithReason(itemState, errorReason)
    Text(
        status.rememberString(),
        color = if (errorReason != null) {
            myColors.error
        } else {
            LocalContentColor.current
        },
        modifier = modifier,
    )
}

@Composable
fun RenderSpeed(speed: Long, modifier: Modifier) {
    val target = LocalSpeedUnit.current
    val speedString = remember(speed) {
        convertPositiveSpeedToHumanReadable(speed, target)
    }
    Text(speedString, modifier)
}

@Composable
fun RenderLeftSubText(itemState: IDownloadItemState, modifier: Modifier) {
    val totalSize = itemState.contentLength
    val sizeUnit = LocalSizeUnit.current
    val totalSizeString = remember(totalSize, sizeUnit) {
        convertPositiveSizeToHumanReadable(totalSize, sizeUnit, true)
    }
    val progress = (itemState as? ProcessingDownloadItemState)?.progress
    val progressStringOrNull = remember(progress, sizeUnit) {
        progress?.let {
            convertPositiveSizeToHumanReadable(progress, sizeUnit, true)
        }
    }
    val text = when {
        else -> {
            buildString {
                progressStringOrNull?.let {
                    append(it.rememberString())
                    append("/")
                }
                append(totalSizeString.rememberString())
            }
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (itemState is ProcessingDownloadItemState && itemState.supportResume == false) {
            MyIcon(
                MyIcons.pause,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = myColors.error,
            )
        }
        Text(text)
    }
}


@Composable
private fun RenderFileIcon(
    downloadItem: IDownloadItemState,
    fileIconProvider: FileIconProvider,
) {
    MyIcon(
        icon = fileIconProvider.rememberIcon(downloadItem.name),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
    )
}

@Composable
private fun RenderProgressBar(
    itemState: IDownloadItemState,
    modifier: Modifier,
) {
    val progress = when (itemState) {
        is CompletedDownloadItemState -> 100
        is ProcessingDownloadItemState -> when (val status = itemState.status) {
            is DownloadJobStatus.PreparingFile -> status.percent
            else -> itemState.percent
        }
    }?.let {
        it / 100f
    }

    val status = itemState.statusOrFinished()
    val background = when (status) {
        is DownloadJobStatus.Finished -> myColors.successGradient
        is DownloadJobStatus.Canceled -> if (ExceptionUtils.isNormalCancellation(status.e)) {
            myColors.warningGradient
        } else {
            myColors.errorGradient
        }

        DownloadJobStatus.IDLE -> myColors.warningGradient
        is DownloadJobStatus.Retrying -> myColors.errorGradient
        DownloadJobStatus.Finished -> myColors.successGradient
        is DownloadJobStatus.PreparingFile -> myColors.infoGradient
        DownloadJobStatus.Resuming,
        DownloadJobStatus.Downloading,
            -> myColors.primaryGradient
    }

    Box(
        modifier
            .fillMaxSize()
            .clip(myShapes.defaultRounded)
            .background(myColors.onBackground / 15)
    ) {
        progress?.let { progress ->
            val animatedProgress by animateFloatAsState(
                progress,
                tween(300, easing = LinearEasing),
                label = "downloadProgress",
            )
            Box(
                Modifier
                    .clip(myShapes.defaultRounded)
                    .background(background)
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
            ) {
            }
        }
        if (progress == null && status is DownloadJobStatus.IsActive) {
            val anim = rememberInfiniteTransition()
            val l = 2000
            val endPos by anim.animateFloat(
                0f,
                1f,
                infiniteRepeatable(tween(l), RepeatMode.Restart)
            )
            val width by anim.animateFloat(
                6f, 16f, infiniteRepeatable(
                    keyframes {
                        durationMillis = l
                        0f atFraction 0f
                        0.75f atFraction 0.25f
                        0f atFraction 1f
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(endPos)
            ) {
                Box(
                    Modifier
                        .background(background)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(width)
                )
            }
        }
    }
}


