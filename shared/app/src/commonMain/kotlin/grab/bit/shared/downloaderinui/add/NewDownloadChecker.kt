package grab.bit.shared.downloaderinui.add

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.utils.DuplicateFilterByPath
import grab.bit.util.FileNameValidator
import grab.bit.util.osfileutil.FileUtils
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import java.io.File

class NewDownloadChecker<
        Credentials : IDownloadCredentials,
        ResponseInfo : IResponseInfo,
        TDownloadSize : DownloadSize,
        >(
    initialName: String,
    initialFolder: String,
    private val linkChecker: LinkChecker<Credentials, ResponseInfo, TDownloadSize>,
    private val downloadSystem: DownloadSystem,
    private val parentScope: CoroutineScope,
) {

    val canAddResult = MutableStateFlow(null as CanAddResult?)
    val canAdd = canAddResult.mapStateFlow() {
        it is CanAddResult.CanAdd
    }
    val isDuplicate = canAddResult.mapStateFlow() {
        it is CanAddResult.DownloadAlreadyExists
    }

    val name = MutableStateFlow(initialName)
    val folder = MutableStateFlow(initialFolder)

    init {
        combine(
            this.name,
            this.folder,
            transform = { _ ->
                canAddResult.update { null }
            }
        ).launchIn(parentScope)
    }

    suspend fun check() {
        canAddResult.update { null }
        val newResult = validate()
        canAddResult.update { newResult }
    }

    private suspend fun validate(): CanAddResult {
        if (!linkChecker.isValidCredentials(linkChecker.credentials.value)) {
            return CanAddResult.InvalidUrl
        }
        if (!fileNameValid()) {
            return CanAddResult.InvalidFileName
        }
        val name = name.value
        val folder = folder.value
        val file = File(folder, name)
        val duplicateFilterByPath = DuplicateFilterByPath(file)
        val items = downloadSystem
            .getDownloadItemsBy(duplicateFilterByPath::isDuplicate)

        if (items.isNotEmpty()) {
            return CanAddResult.DownloadAlreadyExists(items.first().id)
        }
        if (!FileUtils.canWriteInThisFolder(folder)) {
            return CanAddResult.CantWriteInThisFolder
        }
        return CanAddResult.CanAdd
    }

    private fun fileNameValid(): Boolean {
        return FileNameValidator.isValidFileName(name.value)
    }


}
