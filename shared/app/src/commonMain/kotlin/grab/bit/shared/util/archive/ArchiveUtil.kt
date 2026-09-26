package grab.bit.shared.util.archive

// Extracts a ZIP archive into [destDir]. Returns extracted file count.
// Paths escaping [destDir] (Zip-Slip) are skipped, never written.
expect fun uncompressArchive(filePath: String, destDir: String): Int
