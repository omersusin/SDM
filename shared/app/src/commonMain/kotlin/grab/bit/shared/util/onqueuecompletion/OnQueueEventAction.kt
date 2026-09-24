package grab.bit.shared.util.onqueuecompletion

interface OnQueueEventAction {
    suspend fun onQueueCompleted(queueId: Long)
    suspend fun onQueueEndTimeReached(queueId: Long)
}
