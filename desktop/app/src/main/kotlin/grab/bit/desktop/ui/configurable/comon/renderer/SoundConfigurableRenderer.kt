package grab.bit.desktop.ui.configurable.comon.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import grab.bit.desktop.ui.configurable.ConfigTemplate
import grab.bit.desktop.ui.configurable.TitleAndDescription
import grab.bit.desktop.ui.util.rememberMyFilePickerLauncher
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.item.SoundConfigurable
import grab.bit.shared.ui.widget.MyTextField
import grab.bit.shared.util.notification.INotificationSound
import grab.bit.shared.util.notification.platformNotificationSound
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.myShapes
import grab.bit.shared.util.ui.widget.MyIcon
import io.github.vinceglb.filekit.core.PickerType
import java.io.File

object SoundConfigurableRenderer : ConfigurableRenderer<SoundConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: SoundConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderSoundConfig(configurable, configurableUiProps)
    }

    @Composable
    private fun RenderSoundConfig(cfg: SoundConfigurable, configurableUiProps: ConfigurableUiProps) {
        val value by cfg.stateFlow.collectAsState()
        val setValue = cfg::set

        val pickFileLauncher = rememberMyFilePickerLauncher(
            title = cfg.title.rememberString(),
            initialDirectory = remember(value) {
                if (value == INotificationSound.DEFAULT_VALUE) {
                    null
                } else {
                    runCatching {
                        File(value).parentFile.canonicalPath
                    }.getOrNull()
                }
            },
            fileTypes = PickerType.File(listOf("wav")),
            onResult = { file ->
                file?.let(setValue)
            }
        )


        ConfigTemplate(
            modifier = configurableUiProps.modifier.padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                MyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = value,
                    onTextChange = {
                        setValue(it)
                    },
                    shape = myShapes.defaultRounded,
                    textPadding = PaddingValues(4.dp),
                    placeholder = cfg.title.rememberString(),
                    end = {
                        if (value != INotificationSound.DEFAULT_VALUE) {
                            MyIcon(
                                icon = MyIcons.clear,
                                contentDescription = null,
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Default)
                                    .fillMaxHeight()
                                    .clickable { setValue(INotificationSound.DEFAULT_VALUE) }
                                    .wrapContentHeight()
                                    .padding(horizontal = 8.dp)
                                    .size(16.dp)
                            )
                        }
                        MyIcon(
                            icon = MyIcons.resume,
                            contentDescription = null,
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Default)
                                .fillMaxHeight()
                                .clickable {
                                    platformNotificationSound()
                                        .actualPlay(value)
                                }
                                .wrapContentHeight()
                                .padding(horizontal = 8.dp)
                                .size(16.dp)
                        )
                        MyIcon(
                            icon = MyIcons.folder,
                            contentDescription = null,
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Default)
                                .fillMaxHeight()
                                .clickable { pickFileLauncher.launch() }
                                .wrapContentHeight()
                                .padding(horizontal = 8.dp)
                                .size(16.dp)
                        )
                    }
                )
            }
        )
    }
}
