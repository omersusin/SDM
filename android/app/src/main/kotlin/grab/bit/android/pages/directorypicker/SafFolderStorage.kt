package grab.bit.android.pages.directorypicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import java.io.File

/**
 * Feature #15 (SD-card writes, minimal step).
 *
 * Persists the Storage Access Framework tree URI the user picks for an
 * external/removable volume (takePersistableUriPermission) so the grant
 * survives process death and reboot.
 *
 * Known gap (documented, not silently dropped): the download engine still
 * writes via plain [File] paths ([grab.bit.downloader.DownloadManager.calculateOutputFile]).
 * Full SAF support needs a DocumentFile output-stream path in the file layer,
 * which is a deeper change. The persisted URI recorded here is the hook that
 * future change will consume; until then only volumes the OS exposes as
 * direct [File] paths are actually writable.
 */
object SafFolderStorage {
    private const val PREFS = "saf_folders"
    private const val KEY_TREE_URI = "tree_uri"

    fun persistTreePermission(context: Context, treeUri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TREE_URI, treeUri.toString())
            .apply()
    }

    fun getPersistedTreeUri(context: Context): Uri? {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TREE_URI, null)
            ?: return null
        return runCatching { Uri.parse(raw) }.getOrNull()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TREE_URI)
            .apply()
    }

    /**
     * Best-effort mapping of an ExternalStorageProvider tree URI
     * (e.g. `content://.../tree/XXXX-XXXX%3ADownloads`) to a direct
     * filesystem path (`/storage/XXXX-XXXX/Downloads`).
     * Returns null when the URI uses another provider or cannot be resolved.
     */
    fun treeUriToFilePath(context: Context, treeUri: Uri): String? {
        return runCatching {
            if (treeUri.authority != "com.android.externalstorage.documents") return@runCatching null
            val docId = DocumentsContract.getTreeDocumentId(treeUri) ?: return@runCatching null
            val colon = docId.indexOf(':')
            val volumeId = if (colon < 0) docId else docId.substring(0, colon)
            val relative = if (colon < 0) "" else Uri.decode(docId.substring(colon + 1))
            val base = if (volumeId == "primary") {
                Environment.getExternalStorageDirectory().path
            } else {
                "/storage/$volumeId"
            }
            File(base, relative).path
        }.getOrNull()
    }
}
