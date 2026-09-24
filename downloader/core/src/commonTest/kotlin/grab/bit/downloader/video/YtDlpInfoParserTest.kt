package grab.bit.downloader.video

import kotlin.test.Test
import kotlin.test.assertEquals

class YtDlpInfoParserTest {
    private val sample = """
        {"id":"abc","title":"Sample","formats":[
          {"id":"18","ext":"mp4","vcodec":"avc1","acodec":"mp4a","width":640,"height":360,"filesize":1000,"url":"https://cdn.example/18"},
          {"id":"140","ext":"m4a","vcodec":"none","acodec":"mp4a","filesize_approx":200,"url":"https://cdn.example/140"},
          {"id":"sb3","ext":"mhtml","url":""}
        ],"extra_unknown_field":{"nested":true}}
    """.trimIndent()

    @Test
    fun parsesFormatsAndSkipsUrlLess() {
        val info = YtDlpInfoParser.parse(sample)
        assertEquals("abc", info.id)
        val formats = YtDlpInfoParser.toVideoFormats(info)
        assertEquals(listOf("18", "140"), formats.map { it.id })
        assertEquals(200L, formats[1].filesize)
    }

    @Test
    fun feedsPicker() {
        val formats = YtDlpInfoParser.toVideoFormats(YtDlpInfoParser.parse(sample))
        assertEquals("18", VideoFormatPicker.bestForHeight(formats, 720)?.id)
        assertEquals("140", VideoFormatPicker.audioOnly(formats).map { it.id }.single())
    }

    @Test
    fun parsesSubtitles() {
        val json = """
            {"id":"abc","subtitles":{
              "en":[{"ext":"vtt","url":"https://cdn.example/en.vtt","name":"English"}],
              "tr":[{"ext":"vtt","url":"https://cdn.example/tr.vtt"}]
            }}
        """.trimIndent()
        val info = YtDlpInfoParser.parse(json)
        assertEquals(listOf("en", "tr"), YtDlpInfoParser.subtitleLanguages(info))
        assertEquals(
            listOf("https://cdn.example/tr.vtt"),
            YtDlpInfoParser.subtitleUrls(info, "tr"),
        )
        assertEquals(emptyList(), YtDlpInfoParser.subtitleUrls(info, "de"))
    }

    @Test
    fun parsesFlatPlaylist() {
        val json = """
            {"_type":"playlist","id":"PL1","title":"Mix","entries":[
              {"id":"v1","title":"First","url":"https://video.example/v1"},
              {"id":"v2","title":"Second","url":"https://video.example/v2"},
              {"id":"v3"}
            ]}
        """.trimIndent()
        val playlist = YtDlpInfoParser.parsePlaylist(json)
        assertEquals("PL1", playlist.id)
        assertEquals(
            listOf("https://video.example/v1", "https://video.example/v2"),
            YtDlpInfoParser.playlistUrls(playlist),
        )
    }
}
