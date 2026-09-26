package grab.bit.util

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

    @Test
    fun batchValuesCarryCounterAndHost() {
        assertEquals(
            "cdn.example/clip_3.mp4",
            NameTemplate.render(
                "{host}/{name}_{n}.{ext}",
                NameTemplate.valuesForLink("https://cdn.example/v/clip.mp4", 3),
            ),
        )
    }

    @Test
    fun batchDisplayNameUsesDefaultMask() {
        assertEquals(
            "clip_1.mp4",
            NameTemplate.batchDisplayName("https://cdn.example/v/clip.mp4", 1),
        )
    }

    @Test
    fun batchDisplayNameDropsStrayDotWithoutExtension() {
        assertEquals(
            "README_2",
            NameTemplate.batchDisplayName("https://cdn.example/v/README", 2),
        )
    }

    @Test
    fun batchDisplayNameHonorsCustomTemplate() {
        assertEquals(
            "cdn.example-7",
            NameTemplate.batchDisplayName("https://cdn.example/v/clip.mp4", 7, "{host}-{n}"),
        )
    }
}
