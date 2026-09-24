package grab.bit.android.util.activity

import android.content.Intent
import grab.bit.shared.util.mvi.ContainsEffects

interface ActivityActions {
    fun startActivityAction(intent: Intent)
    fun finishActivityAction()
}
