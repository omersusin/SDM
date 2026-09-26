package grab.bit.downloader.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class SuspendLockList<T> {
    private data class LockWithCounter(
        val lock: Mutex = Mutex(),
        var counter: Int = 1,
    )

    private val locks: ConcurrentHashMap<T, LockWithCounter> = ConcurrentHashMap()
    suspend fun <R> withLock(item: T, block: suspend (T) -> R): R {
        val itemLock = locks.compute(item) { key, existing ->
            if (existing == null) {
                return@compute LockWithCounter()
            }
            existing.counter++
            existing
        }!!
        return try {
            itemLock.lock.withLock {
                block(item)
            }
        } finally {
            locks.compute(item) { key, existing ->
                if (existing == null) {
                    return@compute null
                }
                if ((--existing.counter) == 0) {
                    return@compute null
                }
                existing
            }
        }
    }
}
