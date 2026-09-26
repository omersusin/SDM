package grab.bit.shared.util.webhook

import grab.bit.downloader.DownloadManagerEvents
import grab.bit.downloader.DownloadManagerMinimalControl
import grab.bit.downloader.downloaditem.DownloadItemContext
import grab.bit.downloader.downloaditem.EmptyContext
import grab.bit.downloader.downloaditem.http.HttpDownloadItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeControl(
    override val listOfJobsEvents: SharedFlow<DownloadManagerEvents>,
) : DownloadManagerMinimalControl {
    override suspend fun startJob(id: Long, context: DownloadItemContext) = Unit
    override suspend fun stopJob(id: Long, context: DownloadItemContext) = Unit
    override fun canActivateJob(id: Long) = true
    override fun isHostSlotAvailable(id: Long) = true
    override val interDownloadDelayMs = 0
    override fun limitGlobalSpeed(bytesPerSec: Long) = Unit
    override fun currentGlobalSpeedLimit() = 0L
}

class WebhookNotifierTest {
    private fun item() = HttpDownloadItem(
        link = "https://cdn.example/f.bin",
        id = 7L,
        folder = "/dl",
        name = "f.bin",
    )

    private fun notifier(
        events: MutableSharedFlow<DownloadManagerEvents>,
        url: String,
        sent: MutableList<Pair<String, String>>,
        scope: CoroutineScope,
    ) = WebhookNotifier(
        downloadManager = FakeControl(events),
        scope = scope,
        webhookUrl = MutableStateFlow(url),
        sender = { u, body -> sent.add(u to body) },
    )

    @Test
    fun completedPostsCompletedEvent() {
        val body = buildWebhookPayload("completed", item(), null)
        assertTrue(body.contains("\"event\":\"completed\""))
        assertTrue(body.contains("\"id\":7"))
        assertTrue(body.contains("f.bin"))
        assertTrue(body.contains("https://cdn.example/f.bin"))
    }

    @Test
    fun canceledPostsFailedEventWithError() {
        val body = buildWebhookPayload("failed", item(), "boom")
        assertTrue(body.contains("\"event\":\"failed\""))
        assertTrue(body.contains("boom"))
    }

    @Test
    fun blankUrlSendsNothing() = runBlocking {
        // Independent scope: the notifier collects forever, so it must not
        // be a child of runBlocking (which would never return).
        val scope = CoroutineScope(SupervisorJob())
        try {
            val events = MutableSharedFlow<DownloadManagerEvents>(extraBufferCapacity = 64)
            val sent = mutableListOf<Pair<String, String>>()
            notifier(events, "", sent, scope).boot()
            events.emit(DownloadManagerEvents.OnJobCompleted(item(), EmptyContext))
            delay(500)
            assertTrue(sent.isEmpty())
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun completedEmitsPost() = runBlocking {
        val scope = CoroutineScope(SupervisorJob())
        try {
            val events = MutableSharedFlow<DownloadManagerEvents>(extraBufferCapacity = 64)
            val sent = mutableListOf<Pair<String, String>>()
            notifier(events, "https://hooks.example/done", sent, scope).boot()
            events.emit(DownloadManagerEvents.OnJobCompleted(item(), EmptyContext))
            delay(500)
            assertEquals(1, sent.size)
            assertEquals("https://hooks.example/done", sent[0].first)
            assertTrue(sent[0].second.contains("\"event\":\"completed\""))
        } finally {
            scope.cancel()
        }
    }
}
