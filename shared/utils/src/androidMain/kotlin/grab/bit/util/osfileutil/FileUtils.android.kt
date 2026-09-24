package grab.bit.util.osfileutil

actual fun getPlatformFileUtil(): FileUtils {
    return AndroidFileUtil()
}
