package grab.bit.desktop.utils.singleInstance.service

import grab.bit.desktop.AppComponent
import grab.bit.desktop.utils.IntegrationPortBroadcaster
import grab.bit.desktop.utils.singleInstance.SingleInstanceInitialized
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SingleInstanceServiceImpl : ISingleInstanceService, KoinComponent {
    private val appComponent by inject<AppComponent>()

    override suspend fun awaitReady() {
        SingleInstanceInitialized.awaitDone()
    }

    override suspend fun getIntegrationPort(): Int {
        awaitReady()
        return IntegrationPortBroadcaster
            .getIntegrationPort()
    }

    override suspend fun isReady(): Boolean {
        return SingleInstanceInitialized.isDone()
    }

    override suspend fun showUserThatAppIsRunning() {
        awaitReady()
        appComponent.openHome()
    }

    override suspend fun exit() {
        awaitReady()
        appComponent.exitApp()
    }
}
