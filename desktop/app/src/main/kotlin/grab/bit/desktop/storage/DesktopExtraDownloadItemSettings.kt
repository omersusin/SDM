package grab.bit.desktop.storage

import grab.bit.shared.storage.IExtraDownloadItemSettings
import grab.bit.util.desktop.poweraction.ContainsPowerActionConfigOnFinish
import grab.bit.util.desktop.poweraction.PowerActionConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class DesktopExtraDownloadItemSettings(
    override val id: Long,
    val powerActionTypeOnFinish: PowerActionConfig.Type? = null,
    val powerActionUseForceOnFinish: Boolean = false,
) : IExtraDownloadItemSettings, ContainsPowerActionConfigOnFinish {

    override fun getPowerActionConfigOnFinish() = powerActionTypeOnFinish?.let {
        PowerActionConfig(
            powerActionTypeOnFinish,
            powerActionUseForceOnFinish,
        )
    }

    companion object : IExtraDownloadItemSettings.DataClassDefinitions<DesktopExtraDownloadItemSettings> {
        override fun createDefault(id: Long) = DesktopExtraDownloadItemSettings(id = id)
        override val serializer: KSerializer<DesktopExtraDownloadItemSettings> =
            DesktopExtraDownloadItemSettings.serializer()
    }
}
