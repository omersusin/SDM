package grab.bit.shared.util

import grab.bit.util.ifThen
import grab.bit.util.platform.Platform
import grab.bit.util.platform.isWindows

/**
 * This utility class removes characters that are not supported by the current OS.
 *
 * If additional modifications are required, it may be better to create a separate class for each platform.
 */
object FilenameFixer {
    private const val DEFAULT_REPLACEMENT_CHAR = "_"
    private const val MAX_FILE_NAME_LENGTH = 255
    private const val MAX_EXTENSION_LENGTH = 16
    private val illegalChars by lazy {
        when (Platform.getCurrentPlatform()) {
            Platform.Desktop.Windows -> setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')
            Platform.Desktop.MacOS -> setOf(':')
            Platform.Desktop.Linux,
            Platform.Android,
                -> setOf('/')
        }
    }

    fun fix(name: String): String {
        var fixed = buildString {
            name.forEach { char ->
                append(
                    if (char in illegalChars) {
                        DEFAULT_REPLACEMENT_CHAR
                    } else {
                        char
                    }
                )
            }
        }
            .ifThen(Platform.isWindows()) {
                trimEnd(' ', '.')
            }
        // Never allow bare dot segments (directory traversal): ".." as a
        // whole file name would escape the download folder.
        if (fixed.isNotEmpty() && fixed.all { it == '.' }) {
            fixed = fixed.replace(".", DEFAULT_REPLACEMENT_CHAR)
        }
        // Strip control characters (display spoofing / broken file creation).
        fixed = fixed.filterNot { it.code < 0x20 || it.code == 0x7f }
        // Cap length so filesystem creation cannot fail (keep extension).
        if (fixed.length > MAX_FILE_NAME_LENGTH) {
            val dot = fixed.lastIndexOf('.')
            fixed = if (dot > 0 && fixed.length - dot <= MAX_EXTENSION_LENGTH) {
                fixed.take(MAX_FILE_NAME_LENGTH - (fixed.length - dot)) + fixed.substring(dot)
            } else {
                fixed.take(MAX_FILE_NAME_LENGTH)
            }
        }
        return fixed.ifBlank { DEFAULT_REPLACEMENT_CHAR }
    }
}
