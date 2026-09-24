package grab.bit.util

enum class GrabberUiMode {
    MENU_BADGE,
    AUTO_POPUP,
}

object GrabberUiModes {
    val default: GrabberUiMode get() = GrabberUiMode.MENU_BADGE
}
