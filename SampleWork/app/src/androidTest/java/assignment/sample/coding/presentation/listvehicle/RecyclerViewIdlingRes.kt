package assignment.sample.coding.presentation.listvehicle

import androidx.test.espresso.IdlingResource

/**
 * Resource Idling class to identify if data has been drawn in it so that UI tests can proceed.
 *
 * It works on subscribe listener pattern
 */
class RecyclerViewIdlingRes(private val resName: String) : IdlingResource,
    RecyclerViewHaveDataListener {

    private var isIdle = false
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return resName
    }

    override fun isIdleNow(): Boolean {
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }

    override fun recyclerViewHaveData() {
        if (resourceCallback == null) {
            return
        }
        isIdle = true
        resourceCallback?.onTransitionToIdle()
    }
}