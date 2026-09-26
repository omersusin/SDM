package grab.bit.shared.util.keepawake

expect fun platformKeepAwake(): KeepAwake

interface KeepAwake {
    /**
     * Prevents the system from going to sleep.
     */
    fun keepAwake()

    /**
     * Allows the system to go to sleep again.
     */
    fun allowSleep()
}
