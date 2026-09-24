package grab.bit.updateapplier

import grab.bit.updatechecker.UpdateInfo

interface UpdateApplier {
    fun updateSupported(): Boolean
    suspend fun applyUpdate(updateInfo: UpdateInfo)
    suspend fun cleanup()
}