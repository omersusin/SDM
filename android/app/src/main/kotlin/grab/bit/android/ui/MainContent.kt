package grab.bit.android.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import grab.bit.android.pages.about.AboutPage
import grab.bit.android.pages.batchdownload.BatchDownloadSheet
import grab.bit.android.pages.category.CategorySheet
import grab.bit.android.pages.checksum.FileChecksumPage
import grab.bit.android.pages.home.HomePage
import grab.bit.android.pages.settings.SettingsPage
import grab.bit.android.pages.credits.thirdpartylibraries.ThirdPartyLibrariesPage
import grab.bit.android.pages.credits.translators.TranslatorsPage
import grab.bit.android.pages.editdownload.EditDownloadSheet
import grab.bit.android.pages.newqueue.NewQueueSheet
import grab.bit.android.pages.onboarding.initialsetup.InitialSetupPage
import grab.bit.android.pages.onboarding.permissions.PermissionsPage
import grab.bit.android.pages.perhostsettings.PerHostSettingsPage
import grab.bit.android.pages.queue.QueueConfigSheet
import grab.bit.android.pages.updater.UpdaterSheet
import grab.bit.android.util.compose.rememberIsUiVisible
import grab.bit.shared.ui.widget.NotificationArea
import grab.bit.shared.ui.widget.useNotification
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.rememberChild
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.widget.ScreenSurface
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds


@Composable
fun MainContent(
    mainComponent: MainComponent,
) {
    val activity = LocalActivity.current
    val notificationManager = useNotification()
    val scope = rememberCoroutineScope()
    ScreenSurface(
        modifier = Modifier.fillMaxSize(),
        background = myColors.background,
        contentColor = myColors.onBackground
    ) {
        HandleEffects(mainComponent) { effect ->
            when (effect) {
                is MainComponent.MainAppEffects.StartActivity -> {
                    activity?.startActivity(effect.intent)
                }

                is MainComponent.MainAppEffects.SimpleNotificationNotification -> {
                    scope.launch {
                        withTimeout(5.seconds) {
                            notificationManager.showNotification(effect.notificationModel)
                        }
                    }
                }
            }
        }
        Children(
            mainComponent.stack.collectAsState().value,
            modifier = Modifier.imePadding(),
            animation = stackAnimation { scale() + fade() },
        ) {
            when (val screen = it.instance) {
                is Screen.Home -> {
                    HomePage(screen.component)
                }

                is Screen.Settings -> {
                    SettingsPage(screen.component)
                }

                Screen.About -> {
                    AboutPage(
                        onRequestShowOpenSourceLibraries = {
                            mainComponent.openOpenSourceLibrariesPage()
                        },
                        onRequestShowTranslators = {
                            mainComponent.openTranslatorsPage()
                        }
                    )
                }

                Screen.OpenSourceThirdPartyLibraries -> {
                    ThirdPartyLibrariesPage()
                }

                Screen.Translators -> {
                    TranslatorsPage(
                        onBack = {
                            mainComponent.closeTranslatorsPage()
                        }
                    )
                }

                is Screen.PerHostSettings -> {
                    PerHostSettingsPage(component = screen.component)
                }

                is Screen.FileChecksum -> {
                    FileChecksumPage(component = screen.component)
                }

                is Screen.InitialSetup -> {
                    InitialSetupPage(component = screen.component)
                }

                is Screen.Permissions -> {
                    PermissionsPage(component = screen.component)
                }
            }
        }
        CategorySheet(
            mainComponent.categorySlot.rememberChild(),
            mainComponent::closeCategoryDialog
        )
        QueueConfigSheet(
            mainComponent.queueConfigSlot.rememberChild(),
            mainComponent::closeQueues
        )
        NewQueueSheet(
            onQueueCreate = mainComponent::createQueueWithName,
            isOpened = mainComponent.showAddQueue.collectAsState().value,
            onCloseRequest = { mainComponent.setShowAddQueue(false) },
        )
        BatchDownloadSheet(
            component = mainComponent.batchDownloadSlot.rememberChild(),
            onDismiss = mainComponent::closeBatchDownload
        )
        EditDownloadSheet(
            component = mainComponent.editDownloadSlot.rememberChild(),
            onDismiss = mainComponent::closeEditDownloadDialog,
        )
        UpdaterSheet(
            updaterComponent = mainComponent.updaterComponent,
        )
        val isUiVisible = rememberIsUiVisible()
        LaunchedEffect(isUiVisible) {
            mainComponent.abdmAppManager.setNotificationsHandledInUi(isUiVisible)
        }
        // is this really necessary?
        DisposableEffect(Unit) {
            onDispose {
                mainComponent.abdmAppManager.setNotificationsHandledInUi(false)
            }
        }
        if (isUiVisible) {
            NotificationArea(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 96.dp)
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
            )
        }
    }
}
