package grab.bit.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import grab.bit.android.util.ABDMAppManager
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StartOnBootBroadcastReceiver : BroadcastReceiver(), KoinComponent {
    private val appManager: ABDMAppManager by inject()
    private val appSettingStorage: BaseAppSettingsStorage by inject()
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (appSettingStorage.autoStartOnBoot.value) {
                appManager.bootDownloadSystemAndService()
            }
        }
    }
}
