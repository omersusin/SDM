package grab.bit.android.di

import AndroidDirectLinkUpdateApplier
import android.app.Application
import android.content.Context
import grab.bit.github.GithubApi
import grab.bit.UpdateDownloadLocationProvider
import grab.bit.UpdateManager
import grab.bit.android.ABDMApp
import grab.bit.android.pages.home.HomePageStateToPersist
import grab.bit.android.pages.onboarding.permissions.ABDMPermissions
import grab.bit.android.pages.onboarding.permissions.PermissionManager
import grab.bit.android.receiver.StartOnBootBroadcastReceiver
import grab.bit.android.repository.AppRepository
import grab.bit.android.storage.AndroidExtraDownloadItemSettings
import grab.bit.android.storage.AndroidExtraQueueSettings
import grab.bit.android.storage.AndroidOnBoardingStorage
import grab.bit.android.storage.AppSettingsStorage
import grab.bit.android.storage.BrowserBookmarksStorage
import grab.bit.android.storage.HomePageStorage
import grab.bit.android.storage.OnBoardingData
import grab.bit.android.util.ABDMAppManager
import grab.bit.android.util.ABDMServiceNotificationManager
import grab.bit.android.util.AndroidDefinedPaths
import grab.bit.android.util.AndroidDownloadItemOpener
import grab.bit.android.util.AppInfo
import grab.bit.shared.util.SharedConstants
import grab.bit.shared.ui.theme.ThemeManager
import grab.bit.downloader.queue.QueueManager
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.ISystemThemeDetector
import grab.bit.downloader.DownloadManagerMinimalControl
import grab.bit.downloader.DownloadSettings
import grab.bit.downloader.connection.HttpDownloaderClient
import grab.bit.downloader.connection.OkHttpHttpDownloaderClient
import grab.bit.downloader.db.*
import grab.bit.downloader.monitor.DownloadMonitor
import grab.bit.downloader.utils.IDiskStat
import grab.bit.resources.ABDMLanguageResources
import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.downloaderinui.hls.HLSDownloaderInUi
import grab.bit.shared.downloaderinui.http.HttpDownloaderInUi
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.DnsSettings
import grab.bit.shared.storage.ExtraDownloadSettingsStorage
import grab.bit.shared.storage.ExtraQueueSettingsStorage
import grab.bit.shared.storage.IDNSSettingsStorage
import grab.bit.shared.storage.IExtraDownloadSettingsStorage
import grab.bit.shared.storage.IExtraQueueSettingsStorage
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.storage.PerHostSettingsDatastoreStorage
import grab.bit.shared.storage.ProxyDatastoreStorage
import grab.bit.shared.storage.SelectQueueSettings
import grab.bit.shared.storage.appsettings.PlatformAppSettingsSchema
import grab.bit.shared.storage.impl.DNSStorage
import grab.bit.shared.storage.impl.LastSavedLocationStorage
import grab.bit.shared.storage.impl.SelectQueueStorage
import grab.bit.shared.ui.theme.ThemeSettingsStorage
import grab.bit.shared.ui.widget.NotificationManager
import grab.bit.shared.updater.UpdateDownloaderViaDownloadSystem
import grab.bit.shared.util.AndroidDiskStat
import grab.bit.shared.util.AndroidSystemThemeDetector
import grab.bit.shared.util.AppVersion
import grab.bit.shared.util.DefinedPaths
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.UserAgentProviderFromSettings
import grab.bit.shared.util.*
import grab.bit.updateapplier.UpdateApplier
import grab.bit.downloader.DownloadManager
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import grab.bit.updatechecker.GithubUpdateChecker
import grab.bit.updatechecker.UpdateChecker
import grab.bit.util.AppVersionTracker
import grab.bit.shared.util.appinfo.PreviousVersion
import grab.bit.shared.util.autoremove.RemovedDownloadsFromDiskTracker
import grab.bit.shared.util.category.*
import grab.bit.shared.util.di.BaseOKHttpClientQualifier
import grab.bit.shared.util.dns.AppDns
import grab.bit.shared.util.dns.DnsOptionProvider
import grab.bit.shared.util.downloaderror.DownloadErrorMapperRegistryFactory
import grab.bit.shared.util.downloaderror.faileddownloads.FailedDownloadErrorStorageInMemory
import grab.bit.shared.util.downloaderror.faileddownloads.FailedDownloads
import grab.bit.shared.util.downloaderror.faileddownloads.IFailedDownloadErrorStorage
import grab.bit.shared.util.keepawake.KeepAwakeManager
import grab.bit.shared.util.keepawake.platformKeepAwake
import grab.bit.shared.util.notification.INotificationSettingsStorage
import grab.bit.shared.util.ondownloadcompletion.NoOpOnDownloadCompletionActionProvider
import grab.bit.shared.util.ondownloadcompletion.OnDownloadCompletionActionProvider
import grab.bit.shared.util.ondownloadcompletion.OnDownloadCompletionActionRunner
import grab.bit.shared.util.onqueuecompletion.NoopOnQueueCompletionActionProvider
import grab.bit.shared.util.onqueuecompletion.OnQueueEventActionRunner
import grab.bit.shared.util.onqueuecompletion.OnQueueCompletionActionProvider
import grab.bit.shared.util.perhostsettings.IPerHostSettingsStorage
import grab.bit.shared.util.perhostsettings.PerHostSettingsItem
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import grab.bit.shared.util.ui.IMyIcons
import grab.bit.shared.util.proxy.IProxyStorage
import grab.bit.shared.util.proxy.ProxyData
import grab.bit.shared.util.proxy.ProxyManager
import grab.bit.downloader.DownloaderRegistry
import grab.bit.downloader.connection.UserAgentProvider
import grab.bit.downloader.connection.proxy.AutoConfigurableProxyProvider
import grab.bit.downloader.connection.proxy.NoopSystemProxySelectorProvider
import grab.bit.downloader.connection.proxy.ProxyStrategyProvider
import grab.bit.downloader.connection.proxy.SystemProxySelectorProvider
import grab.bit.downloader.downloaditem.DownloadJob
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.downloader.downloaditem.hls.HLSDownloader
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.downloader.downloaditem.http.HttpDownloadItem
import grab.bit.downloader.downloaditem.http.HttpDownloader
import grab.bit.downloader.monitor.DownloadItemStateFactory
import grab.bit.downloader.monitor.IDownloadMonitor
import grab.bit.downloader.queue.ManualDownloadQueue
import grab.bit.downloader.utils.EmptyFileCreator
import grab.bit.util.compose.IIconResolver
import grab.bit.util.compose.localizationmanager.LanguageManager
import grab.bit.util.compose.localizationmanager.LanguageSourceProvider
import grab.bit.util.compose.localizationmanager.LanguageStorage
import grab.bit.util.config.datastore.createSchemaBasedDatastore
import grab.bit.util.config.datastore.kotlinxSerializationDataStore
import grab.bit.util.startup.AbstractStartupManager
import grab.bit.util.startup.Startup
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okhttp3.Protocol
import okhttp3.internal.tls.OkHostnameVerifier

val downloaderModule = module {
    single<IDownloadQueueDatabase> {
        val definedPaths = get<DefinedPaths>()

        DownloadQueueFileStorageDatabase(
            queueFolder = get<DownloadFoldersRegistry>().registerAndGet(
                definedPaths.queuesDir
            ),
            fileSaver = get(),
        )
    }
    single<IDownloadListDb> {
        val definedPaths = get<DefinedPaths>()
        DownloadListFileStorage(
            downloadListFolder = get<DownloadFoldersRegistry>().registerAndGet(
                definedPaths.downloadListDir
            ),
            fileSaver = get(),
        )
    }
    single {
        TransactionalFileSaver(get())
    }
    single<IDownloadPartListDb> {
        val definedPaths = get<DefinedPaths>()
        PartListFileStorage(
            get<DownloadFoldersRegistry>().registerAndGet(
                definedPaths.partsDir
            ),
            get()
        )
    }
    single<IDiskStat> {
        AndroidDiskStat()
    }
    single<ISystemThemeDetector> {
        AndroidSystemThemeDetector(get())
    }
    single {
        QueueManager(get(), get())
    }
    single {
        DownloadFoldersRegistry()
    }
    single {
        DownloadSettings(
            8,
        )
    }
    single {
        ProxyManager(
            get()
        )
    }.bind<ProxyStrategyProvider>()
    single<SystemProxySelectorProvider> {
        NoopSystemProxySelectorProvider()
    }
    single<AutoConfigurableProxyProvider> {
        AutoConfigurableProxyProvider.NoOp()
    }
    single<UserAgentProvider> {
        UserAgentProviderFromSettings(get())
    }
    single<HttpDownloaderClient> {
        OkHttpHttpDownloaderClient(
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single {
        val downloadSettings: DownloadSettings = get()
        EmptyFileCreator(
            diskStat = get(),
            useSparseFile = { downloadSettings.useSparseFileAllocation }
        )
    }
    single {
        HLSDownloader(inject())
    }
    single {
        HLSDownloaderInUi(get(), get())
    }
    single {
        HttpDownloader(inject())
    }
    single {
        HttpDownloaderInUi(get(), get())
    }
    single {
        DownloaderInUiRegistry().apply {
            add(get<HttpDownloaderInUi>())
            add(get<HLSDownloaderInUi>())
        }
    }.bind<DownloadItemStateFactory<IDownloadItem, DownloadJob>>()
    single {
        DownloaderRegistry().apply {
            add(get<HttpDownloader>())
            add(get<HLSDownloader>())
        }
    }
    single {
        val definedPaths = get<DefinedPaths>()
        DownloadManager(
            get(),
            get(),
            get(),
            get(),
            get(),
            get<DownloadFoldersRegistry>().registerAndGet(
                definedPaths.downloadDataDir
            )
        )
    }.bind(DownloadManagerMinimalControl::class)
    single {
        ManualDownloadQueue(get(), get())
    }
    single<IDownloadMonitor> {
        DownloadMonitor(
            downloadManager = get(),
            manualDownloadQueue = get(),
            downloadItemStateFactory = inject(),
        )
    }
}
val downloadSystemModule = module {
    single {
        val definedPaths = get<DefinedPaths>()
        get<DownloadFoldersRegistry>().registerAndGet(definedPaths.categoriesDir)
        CategoryFileStorage(
            file = definedPaths.categoriesFile.toFile(),
            fileSaver = get()
        )
    }.bind<CategoryStorage>()
    single {
        FileIconProviderUsingCategoryIcons(
            get(),
            get(),
            get(),
            get(),
        )
    }.bind<FileIconProvider>()
    single {
        DefaultCategories(
            icons = get(),
            getDefaultDownloadFolder = {
                get<BaseAppSettingsStorage>().defaultDownloadFolder.value
            }
        )
    }
    single {
        DownloadManagerCategoryItemProvider(get())
    }.bind<ICategoryItemProvider>()
    single {
        CategoryManager(
            categoryStorage = get(),
            scope = get(),
            defaultCategoriesFactory = get(),
            categoryItemProvider = get(),
        )
    }

    single {
        DownloadSystem(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single {
        val definedPaths = get<DefinedPaths>()
        val extraDownloadSettingsStorageFolder = get<DownloadFoldersRegistry>().registerAndGet(
            definedPaths.extraDownloadSettings
        )
        ExtraDownloadSettingsStorage(
            extraDownloadSettingsStorageFolder,
            get(),
            AndroidExtraDownloadItemSettings
        )
    }.bind<IExtraDownloadSettingsStorage<*>>()
    single {
        val definedPaths = get<DefinedPaths>()
        val extraQueueSettingsStorageFolder = get<DownloadFoldersRegistry>().registerAndGet(
            definedPaths.extraQueueSettings
        )
        ExtraQueueSettingsStorage(
            extraQueueSettingsStorageFolder,
            get(),
            AndroidExtraQueueSettings
        )
    }.apply {
        bind<IExtraQueueSettingsStorage<*>>()
    }
    single<OnDownloadCompletionActionProvider> {
        NoOpOnDownloadCompletionActionProvider()
    }
    single<OnQueueCompletionActionProvider> {
        NoopOnQueueCompletionActionProvider()
    }
    single {
        OnDownloadCompletionActionRunner(
            downloadManagerMinimalControl = get(),
            scope = get(),
            onDownloadCompletionActionProvider = get(),
        )
    }
    single {
        OnQueueEventActionRunner(
            queueManager = get(),
            scope = get(),
            onQueueCompletionActionProvider = get(),
        )
    }
    single {
        PermissionManager(
            ABDMPermissions.importantPermissions,
            get(),
        )
    }
}
val coroutineModule = module {
    single {
        CoroutineScope(SupervisorJob())
    }
}
val jsonModule = module {
    single {
        val downloaderRegistry: DownloaderRegistry by inject()
        Json {
            this.encodeDefaults = true
            this.prettyPrint = true
            this.ignoreUnknownKeys = true
            this.serializersModule = SerializersModule {
                polymorphic(IDownloadItem::class) {
                    downloaderRegistry.getAll().forEach {
                        subclass(it.downloadItemClass, it.downloadItemSerializer)
                    }
                    defaultDeserializer {
                        HttpDownloadItem.serializer()
                    }
                }
                polymorphic(IDownloadCredentials::class) {
                    downloaderRegistry.getAll().forEach {
                        subclass(it.downloadCredentialsClass, it.downloadCredentialsSerializer)
                    }
                    defaultDeserializer {
                        HttpDownloadCredentials.serializer()
                    }
                }
            }
        }
    }
}
val updaterModule = module {
    single {
        val definedPaths = get<DefinedPaths>()
        UpdateDownloadLocationProvider {
            definedPaths.updateDownloadLocation.toFile()
        }
    }
    single<UpdateApplier> {
        val definedPaths = get<DefinedPaths>()
        definedPaths.updateDownloadLocation
        AndroidDirectLinkUpdateApplier(
            updateDownloader = UpdateDownloaderViaDownloadSystem(
                get(),
                get(),
            ),
        )
    }
    single<UpdateChecker> {
        GithubUpdateChecker(
            AppVersion.get(),
            githubApi = GithubApi(
                owner = SharedConstants.projectGithubOwner,
                repo = SharedConstants.projectGithubRepo,
                client = OkHttpClient
                    .Builder()
                    .build()
            )
        )
    }
    single {
        UpdateManager(
            updateChecker = get(),
            updateApplier = get(),
            appVersionTracker = get(),
        )
    }
}
val startUpModule = module {
    single {
        Startup.getStartUpManager(get(), StartOnBootBroadcastReceiver::class.java)
    }.apply {
        bind<AbstractStartupManager>()
    }
}

fun getAppModule(context: ABDMApp) = module {
    includes(downloaderModule)
    includes(downloadSystemModule)
    includes(coroutineModule)
    includes(jsonModule)
    includes(updaterModule)
    includes(startUpModule)
    single {
        AppInfo.definedPaths
    }.apply {
        bind<DefinedPaths>()
        bind<AndroidDefinedPaths>()
    }
    single {
        AppRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }.apply {
        bind<BaseAppRepository>()
        bind<SizeAndSpeedUnitProvider>()
    }
    single {
        ThemeManager(get(), get(), get())
    }
    single {
        LanguageManager(
            get(),
            LanguageSourceProvider(
                ABDMLanguageResources.defaultLanguageResource,
                ABDMLanguageResources.languages,
            )
        )
    }
    single {
        MyIcons
    }.apply {
        bind<IMyIcons>()
        bind<IIconResolver>()
    }
    single {
        val definedPaths = get<DefinedPaths>()
        ProxyDatastoreStorage(
            kotlinxSerializationDataStore(
                definedPaths.proxySettingsFile.toFile(),
                get(),
                ProxyData::default,
            )
        )
    }.bind<IProxyStorage>()
    single {
        val definedPaths = get<DefinedPaths>()
        AppSettingsStorage(
            createSchemaBasedDatastore(
                definedPaths.appSettingsFile.toFile(),
                get(),
                PlatformAppSettingsSchema,
            )
        )
    }.apply {
        bind<BaseAppSettingsStorage>()
        bind<LanguageStorage>()
        bind<ThemeSettingsStorage>()
        bind<INotificationSettingsStorage>()
    }
    single {
        RemovedDownloadsFromDiskTracker(
            get(), get(), get(),
        )
    }
    single {
        val definedPaths = get<DefinedPaths>()
        PreviousVersion(
            systemPath = definedPaths.systemDir.toFile(),
            currentVersion = AppVersion.get(),
        )
    }
    single {
        AppVersionTracker(
            previousVersion = {
                // it MUST be booted first
                get<PreviousVersion>().get()
            },
            currentVersion = AppVersion.get(),
        )
    }

    single {
        val appSettingsStorage: BaseAppSettingsStorage = get()
        AppSSLFactoryProvider(
            ignoreSSLCertificates = appSettingsStorage.ignoreSSLCertificates
        )
    }
    single {
        val appSettingsStorage: BaseAppSettingsStorage = get()
        AppHostNameVerifier(
            delegateHostnameVerifier = OkHostnameVerifier,
            ignoreHostNameVerification = appSettingsStorage.ignoreSSLCertificates
        )
    }
    single {
        val definedPaths = get<DefinedPaths>()
        DNSStorage(
            kotlinxSerializationDataStore(
                definedPaths.dnsSettingsFile.toFile(),
                get(),
                ::DnsSettings
            )
        )
    }.apply {
        bind<IDNSSettingsStorage>()
        bind<DnsOptionProvider>()
    }
    single<OkHttpClient>(BaseOKHttpClientQualifier) {
        val appSSLFactoryProvider: AppSSLFactoryProvider = get()
        val appHostNameVerifier: AppHostNameVerifier = get()
        OkHttpClient
            .Builder()
            .dispatcher(Dispatcher().apply {
                //bypass limit on concurrent connections!
                maxRequests = Int.MAX_VALUE
                maxRequestsPerHost = Int.MAX_VALUE
            })
            .sslSocketFactory(
                appSSLFactoryProvider.createSSLSocketFactory(),
                appSSLFactoryProvider.trustManager,
            )
            .hostnameVerifier(appHostNameVerifier)
            .build()
    }
    single<AppDns> {
        AppDns(get<OkHttpClient>(BaseOKHttpClientQualifier), get())
    }
    single<OkHttpClient> {
        val baseClient = get<OkHttpClient>(BaseOKHttpClientQualifier)
        val appDns: AppDns = get()
        baseClient.newBuilder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .dns(appDns)
            .build()
    }
    single<ILastSavedLocationsStorage> {
        val definedPaths = get<AndroidDefinedPaths>()
        LastSavedLocationStorage(
            kotlinxSerializationDataStore<List<String>>(
                definedPaths.lastSavedLocationFile.toFile(),
                get(),
                ::emptyList,
            )
        )
    }
    single<ISelectQueueStorage> {
        val definedPaths = get<AndroidDefinedPaths>()
        SelectQueueStorage(
            kotlinxSerializationDataStore<SelectQueueSettings>(
                definedPaths.selectQueueSettingsFile.toFile(),
                get(),
                ::SelectQueueSettings,
            )
        )
    }
    single {
        KeepAwakeManager(
            platformKeepAwake(),
            get(),
            get(),
        )
    }
    single<IPerHostSettingsStorage> {
        val definedPaths = get<DefinedPaths>()
        PerHostSettingsDatastoreStorage(
            kotlinxSerializationDataStore<List<PerHostSettingsItem>>(
                definedPaths.perHostSettingsFile.toFile(),
                get(),
                ::emptyList,
            )
        )
    }
    single {
        PerHostSettingsManager(get())
    }
    single {
        DownloadErrorMapperRegistryFactory().createRegistry()
    }
    single<IFailedDownloadErrorStorage> {
        FailedDownloadErrorStorageInMemory()
    }
    single {
        FailedDownloads(
            get(),
            get(),
            get(),
            get(),
        )
    }
    single { context }.apply {
        bind<ABDMApp>()
        bind<Application>()
        bind<Context>()
    }
    single {
        ABDMAppManager(get(), get(), get(), get(), get(), get(), get(), get())
    }
    single {
        ABDMServiceNotificationManager(get(), get(), get(), get(), get(), get())
    }
    single {
        AndroidDownloadItemOpener(get())
    }.apply {
        bind<DownloadItemOpener>()
    }
    single { NotificationManager() }
    single {
        val paths = get<AndroidDefinedPaths>()
        AndroidOnBoardingStorage(
            kotlinxSerializationDataStore(
                paths.onboardingFile.toFile(),
                get(),
                ::OnBoardingData,
            )
        )
    }
    single {
        val paths = get<AndroidDefinedPaths>()
        HomePageStorage(
            kotlinxSerializationDataStore(
                paths.homePageFile.toFile(),
                get(),
                ::HomePageStateToPersist,
            )
        )
    }
    single {
        val paths = get<AndroidDefinedPaths>()
        BrowserBookmarksStorage(
            kotlinxSerializationDataStore(
                paths.browserBookmarksFile.toFile(),
                get(),
                ::emptyList,
            )
        )
    }
}


object Di : KoinComponent {
    fun boot(applicationContext: ABDMApp) {
        startKoin {
            modules(getAppModule(applicationContext))
        }
    }
}
