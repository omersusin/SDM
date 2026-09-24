package grab.bit.shared.downloaderror

import grab.bit.shared.util.BaseComponent
import grab.bit.shared.util.ClipboardUtil
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DownloadErrorComponent(
    ctx: ComponentContext,
    config: DownloadErrorConfig,
    val onClose: () -> Unit,
) : BaseComponent(ctx), KoinComponent {
    val json: Json by inject()
    val downloadItem = config.downloadItem
    val reason = config.errorReason

    @Serializable
    data class DownloadErrorConfig(
        val downloadItem: IDownloadItem,
        val errorReason: DownloadErrorReason,
    )

    fun onRequestCopyToClipboard() {
        ClipboardUtil.copy(
            json.encodeToString(
                ClipboardData(
                    link = downloadItem.link,
                    reason = reason,
                )
            )
        )
    }

    @Serializable
    private data class ClipboardData(
        val link: String,
        val reason: DownloadErrorReason,
    )
}
