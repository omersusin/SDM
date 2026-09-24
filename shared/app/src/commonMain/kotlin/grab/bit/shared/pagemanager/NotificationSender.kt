package grab.bit.shared.pagemanager

import grab.bit.shared.ui.widget.MessageDialogType
import grab.bit.shared.ui.widget.NotificationType
import grab.bit.util.compose.StringSource

interface NotificationSender {
    fun sendDialogNotification(title: StringSource, description: StringSource, type: MessageDialogType)
    fun sendNotification(tag: Any, title: StringSource, description: StringSource, type: NotificationType)
}
