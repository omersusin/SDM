package ir.amirab.downloader.video

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Thin runner over youtubedl-android: executes yt-dlp and returns stdout.
// Parsing lives in YtDlpInfoParser (tested); this needs a device.
object YtDlpRunner {
    fun init(context: Context) {
        YoutubeDL.getInstance().init(context)
    }

    suspend fun dumpInfo(url: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val request = YoutubeDLRequest(url).apply {
                addOption("--dump-single-json")
                addOption("--no-playlist")
            }
            YoutubeDL.getInstance().execute(request, null, null).out
        }.getOrNull()
    }
}
