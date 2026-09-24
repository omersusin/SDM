package grab.bit.downloader.video

import kotlin.test.Test
import kotlin.test.assertEquals

class YtDlpRequestBuilderTest {
    @Test
    fun buildsFormatAndSubs() {
        val options = YtDlpRequestBuilder.buildOptions(
            YtDlpDownloadRequest(
                url = "https://video.example/v",
                formatId = "18",
                subtitleLangs = listOf("en", "tr"),
                outputTemplate = "%(title)s.%(ext)s",
            )
        )
        assertEquals(
            listOf(
                "-f" to "18",
                "--write-subs" to null,
                "--embed-subs" to null,
                "--sub-langs" to "en,tr",
                "--no-playlist" to null,
                "-o" to "%(title)s.%(ext)s",
            ),
            options,
        )
    }

    @Test
    fun omitsEmptyParts() {
        val options = YtDlpRequestBuilder.buildOptions(
            YtDlpDownloadRequest("https://video.example/v", null, emptyList(), "%(title)s.%(ext)s")
        )
        assertEquals(
            listOf(
                "--no-playlist" to null,
                "-o" to "%(title)s.%(ext)s",
            ),
            options,
        )
    }
}
