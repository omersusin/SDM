package grab.bit.desktop.utils.singleInstance.service

import grab.bit.integration.model.AddDownloadsFromIntegration
import grab.bit.integration.model.ApiQueueModel
import grab.bit.integration.model.NewDownloadTask
import grab.bit.downloader.downloaditem.DownloadJobStatus
import grab.bit.downloader.utils.ExceptionUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import grab.bit.downloader.downloaditem.DownloadStatus as DownloadItemStatus

@Rpc
interface IDefaultAppIPCService {
    // download
    suspend fun addDownloadByGui(request: AddDownloadsFromIntegration)
    suspend fun addDownload(request: NewDownloadTask): Long

    suspend fun pauseDownload(ids: List<Long>)
    suspend fun resumeDownload(ids: List<Long>)

    suspend fun removeDownload(ids: List<Long>, alsoRemoveFile: Boolean)

    suspend fun showDownload(ids: List<Long>): List<ShowDownloadIPC>
    fun watchDownload(ids: List<Long>): Flow<List<ShowDownloadIPC>>

    suspend fun listQueues(): List<ApiQueueModel>
}

@Serializable
data class ShowDownloadIPC(
    val name: String,
    val folder: String,
    val id: Long,
    val percent: Int?,
    val status: DownloadStatus
) {
    @Serializable
    enum class DownloadStatus {
        Paused,
        Error,
        Downloading,
        Finished,
        PreparingFile,
        Resuming,
        Retrying;

        companion object {
            fun fromDownloadStatus(status: DownloadJobStatus): DownloadStatus {
                return when (status) {
                    is DownloadJobStatus.Canceled -> {
                        if (ExceptionUtils.isNormalCancellation(status.e)) {
                            Paused
                        } else {
                            Error
                        }
                    }

                    DownloadJobStatus.Downloading -> Downloading
                    DownloadJobStatus.Finished -> Finished
                    DownloadJobStatus.IDLE -> Paused
                    is DownloadJobStatus.PreparingFile -> PreparingFile
                    DownloadJobStatus.Resuming -> Resuming
                    is DownloadJobStatus.Retrying -> Retrying
                }
            }

            fun fromDownloadStatus(status: DownloadItemStatus): ShowDownloadIPC.DownloadStatus {
                return when (status) {
                    DownloadItemStatus.Paused -> Paused
                    DownloadItemStatus.Error -> Error
                    DownloadItemStatus.Downloading -> Downloading
                    DownloadItemStatus.Completed -> Finished
                    DownloadItemStatus.Added -> Paused
                }
            }
        }
    }
}
