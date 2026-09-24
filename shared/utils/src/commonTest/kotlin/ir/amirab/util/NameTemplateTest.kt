package ir.amirab.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NameTemplateTest {
    @Test
    fun rendersKnownKeys() {
        assertEquals(
            "cdn.example/clip_7.mp4",
            NameTemplate.render(
                "{host}/{name}_{id}.{ext}",
                mapOf("host" to "cdn.example", "name" to "clip", "id" to "7", "ext" to "mp4"),
            )
        )
    }

    @Test
    fun unknownKeysStayVisible() {
        assertEquals(
            "clip_{oops}.mp4",
            NameTemplate.render("{name}_{oops}.{ext}", mapOf("name" to "clip", "ext" to "mp4")),
        )
    }

    @Test
    fun listsKeys() {
        assertEquals(
            setOf("host", "name"),
            NameTemplate.keys("{host}/{name}.mp4"),
        )
    }
}
