package ir.amirab.downloader.monitor

// Obtainium-style ordering: active downloads on top, newest first inside
// each band. Pure pair logic so it stays unit-tested; UI maps items to
// (statusOrder, dateAdded) pairs.
object ActiveFirstSort {
    fun comparator(): Comparator<Pair<Int, Long>> {
        return compareBy<Pair<Int, Long>> { it.first }
            .thenByDescending { it.second }
    }
}
