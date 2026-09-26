package grab.bit.shared.util.ondownloadcompletion

import grab.bit.downloader.DownloadManager
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.downloader.exception.ChecksumMismatchException
import grab.bit.shared.util.FileChecksum
import grab.bit.shared.util.HashUtil

class HashVerifyOnCompletionAction(
    private val downloadManager: () -> DownloadManager,
) : OnDownloadCompletionAction {
    override suspend fun onDownloadCompleted(downloadItem: IDownloadItem) {
        val expected = FileChecksum.fromNullableString(downloadItem.fileChecksum) ?: return
        val file = runCatching { downloadManager().calculateOutputFile(downloadItem) }.getOrNull()
            ?: return
        if (!file.exists()) {
            return
        }
        val actual = runCatching {
            HashUtil.fileHash(expected.algorithm, file) {}
        }.getOrNull() ?: return
        if (expected != FileChecksum(expected.algorithm, actual)) {
            downloadManager().onDownloadCanceled(
                downloadItem,
                ChecksumMismatchException(expected.toString(), "${expected.algorithm}:$actual"),
            )
        }
    }
}

class HashVerifyOnCompletionProvider(
    private val downloadManager: () -> DownloadManager,
) : OnDownloadCompletionActionProvider {
    private val action by lazy { HashVerifyOnCompletionAction(downloadManager) }

    override suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction> {
        if (FileChecksum.fromNullableString(downloadItem.fileChecksum) == null) {
            return emptyList()
        }
        return listOf(action)
    }
}
