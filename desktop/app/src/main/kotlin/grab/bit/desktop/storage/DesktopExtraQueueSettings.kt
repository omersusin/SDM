package grab.bit.desktop.storage

import grab.bit.shared.storage.IExtraQueueSettings
import grab.bit.util.desktop.poweraction.ContainsPowerActionConfigOnFinish
import grab.bit.util.desktop.poweraction.PowerActionConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class DesktopExtraQueueSettings(
    override val id: Long,
    val powerActionTypeOnFinish: PowerActionConfig.Type? = null,
    val powerActionUseForceOnFinish: Boolean = false,
) : IExtraQueueSettings, ContainsPowerActionConfigOnFinish {

    override fun getPowerActionConfigOnFinish() = powerActionTypeOnFinish?.let {
        PowerActionConfig(
            powerActionTypeOnFinish,
            powerActionUseForceOnFinish,
        )
    }

    companion object : IExtraQueueSettings.DataClassDefinitions<DesktopExtraQueueSettings> {
        override fun createDefault(id: Long) = DesktopExtraQueueSettings(id)
        override val serializer: KSerializer<DesktopExtraQueueSettings> = DesktopExtraQueueSettings.serializer()
    }
}
