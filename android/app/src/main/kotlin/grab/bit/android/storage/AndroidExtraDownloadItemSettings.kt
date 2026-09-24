package grab.bit.android.storage

import grab.bit.shared.storage.IExtraDownloadItemSettings
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class AndroidExtraDownloadItemSettings(
    override val id: Long,
    // turnOffWifi: Boolean
) : IExtraDownloadItemSettings {

    companion object : IExtraDownloadItemSettings.DataClassDefinitions<AndroidExtraDownloadItemSettings> {
        override fun createDefault(id: Long) = AndroidExtraDownloadItemSettings(id = id)
        override val serializer: KSerializer<AndroidExtraDownloadItemSettings> =
            AndroidExtraDownloadItemSettings.serializer()
    }
}
