package grab.bit.desktop.window.custom.titlebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import grab.bit.desktop.window.custom.TitlePosition
import grab.bit.desktop.window.custom.titlebar.linux.LinuxTitleBar
import grab.bit.desktop.window.custom.titlebar.mac.MacTitleBar
import grab.bit.desktop.window.custom.titlebar.windows.WindowsTitleBar
import grab.bit.util.platform.Platform
import grab.bit.util.platform.asDesktop

interface TitleBar {
    val titleBarHeight: Dp
    val systemButtonsPosition: SystemButtonsPosition
    @Composable
    fun RenderSystemButtons(
        onRequestClose: () -> Unit,
        onRequestMinimize: (() -> Unit)?,
        onToggleMaximize: (() -> Unit)?,
    )

    @Composable
    fun RenderTitleBarContent(
        title: String,
        titlePosition: TitlePosition,
        modifier: Modifier,
        windowIcon: Painter?,
        start: (@Composable () -> Unit)?,
        end: (@Composable () -> Unit)?,
    )

    @Composable
    fun RenderTitleBar(
        modifier: Modifier,
        titleBar: TitleBar,
        title: String,
        windowIcon: Painter?,
        titlePosition: TitlePosition,
        start: (@Composable () -> Unit)?,
        end: (@Composable () -> Unit)?,
        onRequestClose: () -> Unit,
        onRequestMinimize: (() -> Unit)?,
        onRequestToggleMaximize: (() -> Unit)?,
    )

    companion object {
        val DefaultTitleBarHeigh = 32.dp
        fun getPlatformTitleBar(): TitleBar {
            return when (Platform.asDesktop()) {
                Platform.Desktop.Windows -> WindowsTitleBar
                Platform.Desktop.MacOS -> MacTitleBar
                Platform.Desktop.Linux -> LinuxTitleBar
            }
        }
    }
}

enum class SystemButtonType {
    Close,
    Minimize,
    Maximize,
}

data class SystemButtonsPosition(
    val buttons: List<SystemButtonType>,
    val isLeft: Boolean,
)
