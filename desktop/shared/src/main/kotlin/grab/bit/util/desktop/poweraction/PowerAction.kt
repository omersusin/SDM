package grab.bit.util.desktop.poweraction

interface PowerAction {
    fun initiate(config: PowerActionConfig): Boolean
}
