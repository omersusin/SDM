package grab.bit.shared.downloaderinui.add

import grab.bit.resources.Res
import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.shared.util.perhostsettings.PerHostSettingsItem
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class NewDownloadInputs<
        TDownloadItem : IDownloadItem,
        TCredentials : IDownloadCredentials,
        TResponseInfoType : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfoType, TDownloadSize>,
        >(
    val newDownloadUiChecker: NewDownloadUiChecker<TCredentials, TResponseInfoType, TDownloadSize, TLinkChecker>
) {
    val openedTime = System.currentTimeMillis()

    val name = newDownloadUiChecker.name
    val folder = newDownloadUiChecker.folder
    val credentials = newDownloadUiChecker.credentials
    val downloadSize = newDownloadUiChecker.downloadSize
    abstract val downloadItem: StateFlow<TDownloadItem>
    abstract val downloadJobConfig: StateFlow<DownloadJobExtraConfig?>
    abstract val configurableList: List<Configurable<*>>

    abstract fun applyHostSettingsToExtraConfig(extraConfig: PerHostSettingsItem)

    fun setCredentials(credentials: TCredentials) {
        newDownloadUiChecker.credentials.update { credentials }
    }

    abstract fun downloadSizeToStringSource(downloadSize: TDownloadSize): StringSource?

    val lengthStringFlow: StateFlow<StringSource> = downloadSize.mapStateFlow {
        it
            ?.let(::downloadSizeToStringSource)
            ?: Res.string.unknown.asStringSource()
    }

    fun getLengthString(): StringSource {
        return lengthStringFlow.value
    }

    fun getUniqueId(): NewDownloadInputsUniqueIdType = hashCode()
}
typealias TANewDownloadInputs = NewDownloadInputs<IDownloadItem, IDownloadCredentials, IResponseInfo, DownloadSize, LinkChecker<IDownloadCredentials, IResponseInfo, DownloadSize>>
typealias NewDownloadInputsUniqueIdType = Int
