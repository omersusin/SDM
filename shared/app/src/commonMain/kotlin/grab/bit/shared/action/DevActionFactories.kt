package grab.bit.shared.action

import grab.bit.shared.pagemanager.NotificationSender
import grab.bit.shared.ui.widget.MessageDialogType
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.util.compose.action.AnAction
import grab.bit.util.compose.action.MenuItem
import grab.bit.util.compose.action.simpleAction
import grab.bit.util.compose.asStringSource


fun createDummyExceptionAction(): AnAction {
    return simpleAction(
        "Dummy Exception".asStringSource(),
        MyIcons.info
    ) {
        error("This is a dummy exception that is thrown by developer")
    }
}

fun createDummyMessageAction(
    notificationSender: NotificationSender,
): MenuItem.SubMenu {
    return MenuItem.SubMenu(
        title = "Show Dialog Message".asStringSource(),
        icon = MyIcons.info,
        items = listOf(
            MessageDialogType.Info,
            MessageDialogType.Error,
            MessageDialogType.Warning,
            MessageDialogType.Success,
        ).map {
            createDummyMessage(it, notificationSender)
        }
    )
}

private fun createDummyMessage(
    type: MessageDialogType,
    notificationSender: NotificationSender,
): AnAction {
    return simpleAction(
        "$type Message".asStringSource(),
        MyIcons.info,
    ) {
        notificationSender.sendDialogNotification(
            type = type,
            title = "Dummy Message".asStringSource(),
            description = "This is a test message".asStringSource()
        )
    }
}
