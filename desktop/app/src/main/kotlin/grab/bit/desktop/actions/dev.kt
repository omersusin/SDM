package grab.bit.desktop.actions

import grab.bit.desktop.AppComponent
import grab.bit.desktop.di.Di
import grab.bit.desktop.pages.poweractionalert.PowerActionComponent
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.ui.widget.MessageDialogType
import grab.bit.resources.Res
import grab.bit.shared.action.createDummyExceptionAction
import grab.bit.shared.action.createDummyMessageAction
import grab.bit.util.compose.action.AnAction
import grab.bit.util.compose.action.MenuItem
import grab.bit.util.compose.action.simpleAction
import grab.bit.util.compose.asStringSource
import grab.bit.util.desktop.poweraction.PowerActionConfig
import org.koin.core.component.get

private val appComponent = Di.get<AppComponent>()
val dummyMessage = createDummyMessageAction(appComponent)
val dummyException = createDummyExceptionAction()
val shutdown = simpleAction(
    Res.string.shutdown_now.asStringSource(),
    MyIcons.exit,
) {
    appComponent.initiatePowerAction(
        PowerActionConfig(PowerActionConfig.Type.Shutdown, false),
        PowerActionComponent.PowerActionReason.Unknown
    )
}
