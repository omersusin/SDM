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

    @Test
    fun appendsExtraArgs() {
        val options = YtDlpRequestBuilder.buildOptions(
            YtDlpDownloadRequest(
                url = "https://video.example/v",
                formatId = null,
                subtitleLangs = emptyList(),
                outputTemplate = "%(title)s.%(ext)s",
                extraArgs = "--extractor-args youtube:player_client=android --impersonate chrome",
            )
        )
        assertEquals(
            listOf(
                "--no-playlist" to null,
                "-o" to "%(title)s.%(ext)s",
                "--extractor-args" to "youtube:player_client=android",
                "--impersonate" to "chrome",
            ),
            options,
        )
    }

    @Test
    fun extraArgsSupportsFlagsAndQuotes() {
        val options = YtDlpRequestBuilder.buildOptions(
            YtDlpDownloadRequest(
                url = "https://video.example/v",
                formatId = null,
                subtitleLangs = emptyList(),
                outputTemplate = "%(title)s.%(ext)s",
                extraArgs = "--no-check-certificate --postprocessor-args \"-threads 4\"",
            )
        )
        assertEquals(
            listOf(
                "--no-playlist" to null,
                "-o" to "%(title)s.%(ext)s",
                "--no-check-certificate" to null,
                "--postprocessor-args" to "-threads 4",
            ),
            options,
        )
    }

    @Test
    fun blankExtraArgsAddsNothing() {
        val options = YtDlpRequestBuilder.buildOptions(
            YtDlpDownloadRequest(
                url = "https://video.example/v",
                formatId = null,
                subtitleLangs = emptyList(),
                outputTemplate = "%(title)s.%(ext)s",
                extraArgs = "   ",
            )
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
