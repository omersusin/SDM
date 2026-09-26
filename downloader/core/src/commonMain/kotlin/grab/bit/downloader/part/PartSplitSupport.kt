package grab.bit.downloader.part

import kotlin.math.min


class PartSplitSupport(
    val part: RangedPart,
    private val partEndLock: Any = Any(),
) {
    //initial remainingSafe will be 0
    @Volatile
    var safeZone = part.current - 1

    //pure
    fun remainingSafe(): Long {
        // +1 is because of end inclusive
        return (safeZone + 1 - part.current).coerceAtLeast(0)
    }

    fun howMuchCanRead(
        expandToBufferSize: Long? = null,
        tryToExtendSafeZone: Boolean = expandToBufferSize != null,
    ): Long {
        val defaultRemaining = remainingSafe()
        if (tryToExtendSafeZone) {
            if (expandToBufferSize != null) {
                if (defaultRemaining < expandToBufferSize) {
                    if (extendSafeZone()) {
                        return remainingSafe()
                    }
                }
            } else if (defaultRemaining == 0L) {
                if (extendSafeZone()) {
                    return remainingSafe()
                }
            }
        }
        return defaultRemaining
    }

    fun extendSafeZone(): Boolean {
        synchronized(partEndLock) {
            //remaining
            val remaining = part.remainingLength?:Long.MAX_VALUE
            if (remaining == 0L) {
                return false
            }
            val oldSafeZone = safeZone
            val newSafeZone = (oldSafeZone + min(remaining, SAFE_ZONE_SIZE))
                .coerceAtMost(part.to ?: Long.MAX_VALUE)

            if (oldSafeZone==newSafeZone){
                return false
            }
            safeZone=newSafeZone
//            require(safeZone<=part.to)
            return true
        }
    }

    fun splitPart(): RangedPart? {
        synchronized(partEndLock) {
            if (!canSplit()) return null

            val delta = part.to!! - safeZone
            val safeZoneToEnd = safeZone + (delta / 2) + delta % 2
//            val oldPart = part.copy()
            if (safeZoneToEnd + 1 > part.to!!) {
                //new part will exceed current part boundaries
                return null
            }
            val newPart = RangedPart(
                from = safeZoneToEnd + 1,
                to = part.to!!
            )
            part.to = safeZoneToEnd
//            val isValid = isSplitValid(oldPart, part, newPart)
//            val safeZoneRespected = safeZone <= part.to!!
//            println(
//                "split = $this ," +
//                        "safezone respected =$safeZoneRespected ,"+
//                        "old = $oldPart ," +
//                        "changed = ${part.copy()} ," +
//                        "newPart= ${newPart.copy()} ," +
//                        "valid = $isValid ,"
//            )
            return newPart
        }
    }

    fun canSplit(): Boolean {
        if (part.to == null) {
            return false
        }
        val delta = part.to!! - safeZone
        //We only want split a part that worth it!
        return delta >= SAFE_ZONE_SIZE
    }

    override fun toString(): String {
        return "PartSplitSupport(part=$part, safeZone=$safeZone, remainingSafe=${remainingSafe()}"
    }

    companion object {
        private fun isSplitValid(
            oldPart: RangedPart,
            reducedPart: RangedPart,
            newPart: RangedPart,
        ): Boolean {
            return (reducedPart.to == newPart.from - 1) && (oldPart.to == newPart.to)
        }

        //        const val SAFE_ZONE_SIZE: Long = 5
        const val SAFE_ZONE_SIZE: Long = 128 * 8192
    }
}
