package grab.bit.shared.pagemanager

import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.pages.adddownload.ImportOptions

interface AddDownloadDialogManager {
    fun closeAddDownloadDialog()
    fun openAddDownloadDialog(
        links: List<AddDownloadCredentialsInUiProps>,
        importOptions: ImportOptions = ImportOptions(),
    )
}
