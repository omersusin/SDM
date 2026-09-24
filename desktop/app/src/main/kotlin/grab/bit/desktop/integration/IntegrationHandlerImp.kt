package grab.bit.desktop.integration

import grab.bit.desktop.AppComponent
import grab.bit.desktop.repository.AppRepository
import grab.bit.integration.IntegrationHandler
import grab.bit.integration.model.*
import grab.bit.shared.downloaderinui.BasicDownloadItem
import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.pages.adddownload.ImportOptions
import grab.bit.shared.pages.adddownload.SilentImportOptions
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.NewDownloadItemProps
import grab.bit.downloader.downloaditem.EmptyContext
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.downloader.queue.QueueManager
import grab.bit.downloader.utils.OnDuplicateStrategy
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IntegrationHandlerImp : IntegrationHandler, KoinComponent {
    val appComponent by inject<AppComponent>()
    val downloadSystem by inject<DownloadSystem>()
    val queueManager by inject<QueueManager>()
    val appSettings by inject<AppRepository>()
    private val downloaderInUiRegistry by inject<DownloaderInUiRegistry>()

    override suspend fun addDownloadByGui(
        request: AddDownloadsFromIntegration
    ) {
        val list = request.items
        val options = request.options
        appComponent.externalCredentialComingIntoApp(
            list.map {
                convertToDownloadSystemCredentials(it)
            },
            options = ImportOptions(
                silentImport = if (options.silentAdd) {
                    SilentImportOptions(
                        silentDownload = options.silentStart
                    )
                } else null
            )
        )
    }

    override fun listQueues(): List<ApiQueueModel> {
        return queueManager.getAll().map { downloadQueue ->
            val queueModel = downloadQueue.getQueueModel()
            ApiQueueModel(id = queueModel.id, name = queueModel.name)
        }
    }

    override suspend fun addDownload(task: NewDownloadTask): Long {
        val addDownloaderInUiProps = convertToDownloadSystemCredentials(task.downloadSource)
        val downloaderInUi = downloaderInUiRegistry.getDownloaderOf(
            addDownloaderInUiProps.credentials
        ) ?: error("Downloader for ${addDownloaderInUiProps.credentials::class.qualifiedName} not found")
        val downloadItem = downloaderInUi.createBareDownloadItem(
            addDownloaderInUiProps.credentials,
            basicDownloadItem = BasicDownloadItem(
                folder = task.folder ?: appSettings.saveLocation.value,
                name = task.name ?: addDownloaderInUiProps.extraConfig.suggestedName
                ?: task.downloadSource.link.substringAfterLast("/"),
            ),
        )
        val id = downloadSystem.addDownload(
            newDownload = NewDownloadItemProps(
                downloadItem = downloadItem,
                onDuplicateStrategy = OnDuplicateStrategy.default(),
                extraConfig = null,
                context = EmptyContext,
            ),
            queueId = task.queueId,
            categoryId = task.categoryId,
        )
        val queueId = task.queueId
        // either start the queue or manually start the download
        // both of them at the same time is not good idea
        if (task.startQueue && queueId != null) {
            val queue = queueManager.getQueue(queueId)
            queue.start()
        } else {
            if (task.startDownload) {
                downloadSystem.userManualResume(id)
            }
        }
        return id
    }

    companion object {
        private fun convertToDownloadSystemCredentials(it: IDownloadCredentialsFromIntegration): AddDownloadCredentialsInUiProps {
            val credentials = when (it) {
                is HttpDownloadCredentialsFromIntegration -> {
                    HttpDownloadCredentials(
                        link = it.link,
                        headers = it.headers,
                        downloadPage = it.downloadPage,
                    )
                }

                is HLSDownloadCredentialsFromIntegration -> {
                    HLSDownloadCredentials(
                        link = it.link,
                        headers = it.headers,
                        downloadPage = it.downloadPage,
                    )
                }
            }
            return AddDownloadCredentialsInUiProps(
                credentials = credentials,
                extraConfig = AddDownloadCredentialsInUiProps.Configs(
                    suggestedName = it.suggestedName,
                )
            )
        }
    }
}
