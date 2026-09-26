package grab.bit.downloader.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    // ponytail: SimpleDateFormat allocs per header parse; thread-local cache
    private val dateFormatHolder = ThreadLocal.withInitial {
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
    }

    fun convertLastModifiedHeaderToTimestamp(lastModified: String): Long {
        // Define the format of the Last-Modified header
        val dateFormat = dateFormatHolder.get()

        // Parse the date string into a Date object
        val date = dateFormat.parse(lastModified)

        // Convert the Date object to a timestamp (milliseconds since epoch)
        return date?.time ?: throw IllegalArgumentException("Invalid Last-Modified header")
    }
}