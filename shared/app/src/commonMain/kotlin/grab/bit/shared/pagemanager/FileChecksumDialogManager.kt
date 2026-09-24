package grab.bit.shared.pagemanager

interface FileChecksumDialogManager {
    fun openFileChecksumPage(ids: List<Long>)

    fun closeFileChecksumPage(dialogId: String)
}
