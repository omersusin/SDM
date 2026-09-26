package grab.bit.updateapplier

import grab.bit.updatechecker.UpdateSource
import java.io.File

interface UpdatePreparer {
    interface PreparedUpdate {
        fun isValid(): Boolean
    }

    suspend fun prepareUpdate(source: UpdateSource): PreparedUpdate
    suspend fun disposeUpdate(updateSource: UpdateSource)
    suspend fun disposeAllUpdates()
    fun accept(updateSource: UpdateSource): Boolean
}

abstract class UpdateDownloader : UpdatePreparer {
    data class PreparedUpdateFile(
        val file: File,
        val expectedHash: String? = null,
    ) : UpdatePreparer.PreparedUpdate {
        override fun isValid(): Boolean {
            if (!file.exists()) {
                return false
            }
            val hash = expectedHash ?: return true
            return verifyFileHash(file, hash)
        }

    }

    override fun accept(updateSource: UpdateSource): Boolean {
        return updateSource is UpdateSource.DirectDownloadLink
    }

    override suspend fun prepareUpdate(source: UpdateSource): UpdatePreparer.PreparedUpdate {
        val link = source as UpdateSource.DirectDownloadLink
        return PreparedUpdateFile(
            downloadUpdateFile(link),
            link.hash,
        )
    }

    override suspend fun disposeUpdate(updateSource: UpdateSource) {
        removeUpdateFiles(updateSource as UpdateSource.DirectDownloadLink)
    }

    override suspend fun disposeAllUpdates() {
        return removeAllUpdateFiles()
    }


    abstract suspend fun downloadUpdateFile(updateDirectDownloadLink: UpdateSource.DirectDownloadLink): File
    abstract suspend fun removeUpdateFiles(updateDirectDownloadLink: UpdateSource.DirectDownloadLink)
    abstract suspend fun removeAllUpdateFiles()

    companion object {
        // Supports "md5:<hex>" hashes published as release sidecar files.
        fun verifyFileHash(file: File, expected: String): Boolean {
            val parts = expected.split(":", limit = 2)
            if (parts.size != 2 || !parts[0].equals("md5", ignoreCase = true)) {
                return false
            }
            return runCatching {
                val digest = java.security.MessageDigest.getInstance("MD5")
                file.inputStream().use { input ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        digest.update(buffer, 0, read)
                    }
                }
                digest.digest().joinToString("") { "%02x".format(it) }
                    .equals(parts[1].trim(), ignoreCase = true)
            }.getOrDefault(false)
        }
    }
}
