package grab.bit.downloader.downloaditem.http

import grab.bit.downloader.exception.TooManyErrorException
import grab.bit.downloader.exception.UnSuccessfulResponseException

// Trips the queue when a host keeps answering with bot-protection errors.
// 403 from the unwrapped root cause counts; anything else resets the streak.
// Checked in the queue layer on job-cancel events (see DownloadQueue).
object BotErrorTripPolicy {
    const val DEFAULT_THRESHOLD = 3
    const val BOT_HTTP_CODE = 403

    fun isBotError(e: Throwable): Boolean {
        val root = (e as? TooManyErrorException)?.findActualDownloadErrorCause() ?: e
        return (root as? UnSuccessfulResponseException)?.code == BOT_HTTP_CODE
    }
}
