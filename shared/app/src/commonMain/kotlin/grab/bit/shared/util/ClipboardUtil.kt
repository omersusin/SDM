package grab.bit.shared.util


expect object ClipboardUtil {
    fun read(): String?
    fun copy(text: String)
}
