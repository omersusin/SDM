package grab.bit.shared.util.keepawake

actual fun platformKeepAwake(): KeepAwake {
    return instance
}

private val instance by lazy { AndroidWakeLock() }

