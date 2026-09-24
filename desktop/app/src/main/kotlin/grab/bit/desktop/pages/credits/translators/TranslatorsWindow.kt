package grab.bit.desktop.pages.credits.translators

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.AppComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.desktop.window.custom.WindowTitle
import grab.bit.resources.Res
import grab.bit.util.compose.resources.myStringResource


@Composable
fun ShowTranslators(
    appComponent: AppComponent,
) {
    TranslatorsWindow(
        isVisible = appComponent.showTranslators.collectAsState().value,
        onRequestClose = {
            appComponent.closeTranslatorsPage()
        }
    )
}

@Composable
private fun TranslatorsWindow(
    isVisible: Boolean,
    onRequestClose: () -> Unit,
) {
    if (!isVisible) return
    CustomWindow(
        onCloseRequest = onRequestClose,
        state = rememberWindowState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = WindowSizeProvider.Fixed(
                    size = DpSize(650.dp, 500.dp)
                ),
            )
        )
    ) {
        WindowTitle(myStringResource(Res.string.meet_the_translators))
        Translators(
            modifier = Modifier.fillMaxSize(),
        )
    }
}