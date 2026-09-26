package grab.bit.shared.util.archive

import java.io.File
import java.util.zip.ZipFile

actual fun uncompressArchive(filePath: String, destDir: String): Int {
    val dest = File(destDir).canonicalFile
    if (!dest.isDirectory && !dest.mkdirs()) {
        return 0
    }
    var count = 0
    ZipFile(File(filePath)).use { zip ->
        val entries = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory) {
                continue
            }
            val out = File(dest, entry.name).canonicalFile
            // Zip-Slip guard: refuse entries escaping the destination.
            if (!out.path.startsWith(dest.path + File.separator)) {
                continue
            }
            out.parentFile?.mkdirs()
            zip.getInputStream(entry).use { input ->
                out.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            count++
        }
    }
    return count
}
