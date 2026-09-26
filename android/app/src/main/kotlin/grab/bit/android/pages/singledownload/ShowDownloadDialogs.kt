package grab.bit.android.pages.singledownload

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import grab.bit.android.ui.SheetHeader
import grab.bit.android.ui.SheetTitle
import grab.bit.android.ui.SheetUI
import grab.bit.resources.Res
import grab.bit.shared.singledownloadpage.createStatusString
import grab.bit.shared.ui.widget.TransparentIconActionButton
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.downloader.monitor.CompletedDownloadItemState
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import grab.bit.util.compose.asStringSource
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun getDownloadTitle(itemState: IDownloadItemState): String {
    return buildString {
        if (itemState is ProcessingDownloadItemState && itemState.percent != null) {
            append("${itemState.percent}%")
            append(" ")
        }
        append(createStatusString(itemState).rememberString())
    }
}


@Composable
fun ShowDownloadDialog(
    singleDownloadComponent: AndroidSingleDownloadComponent,
    onRequestShowInDownloads: () -> Unit,
) {
    val itemState by singleDownloadComponent.itemStateFlow.collectAsState()
    val dialogState = rememberResponsiveDialogState(false)
    dialogState.OnFullyDismissed {
        singleDownloadComponent.close()
    }
    LaunchedEffect(Unit) {
        // animate open after activity becomes fully open
        // is there a better way?
        delay(10.milliseconds)
        dialogState.show()
    }
    val closeDialog = dialogState::hide
    ResponsiveDialog(
        dialogState, closeDialog
    ) {
        itemState?.let { downloadItemState ->
            SheetUI(header = {
                SheetHeader(
                    headerTitle = {
                        SheetTitle(getDownloadTitle(downloadItemState))
                    },
                    headerActions = {
                        if (singleDownloadComponent.comesFromExternalApplication) {
                            TransparentIconActionButton(
                                MyIcons.externalLink,
                                contentDescription = Res.string.show_downloads.asStringSource(),
                                onClick = onRequestShowInDownloads,
                            )
                        }
                        TransparentIconActionButton(
                            MyIcons.close,
                            contentDescription = Res.string.close.asStringSource(),
                            onClick = closeDialog
                        )
                    }
                )
            }) {
                AnimatedContent(
                    targetState = downloadItemState,
                    transitionSpec = {
                        fadeIn() + scaleIn(.98f) togetherWith fadeOut() + scaleOut()
                    },
                    label = "dlSwap",
                    contentKey = {
                        when (it) {
                            is CompletedDownloadItemState -> 0
                            is ProcessingDownloadItemState -> 1
                        }
                    }
                ) { downloadItemState ->
                    when (downloadItemState) {
                        is CompletedDownloadItemState -> {
                            CompletedDownloadPage(
                                singleDownloadComponent,
                                downloadItemState,
                            )
                        }

                        is ProcessingDownloadItemState -> {
                            ProgressDownloadPage(
                                singleDownloadComponent,
                                downloadItemState,
                            )
                        }
                    }
                }

            }
        }
    }

}



