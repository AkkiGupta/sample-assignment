package assignment.sample.coding.data.util

/**
 * Event class is a utility class to handle scenarios where LiveData provides repetitive values
 * to observe though it has been consumed already.
 *
 * Should wrap LiveData in Event and serve it
 *
 */
open class Event<out T>(private val content: T) {

    var hasAlreadyBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and refrain from future use.
     */
    fun getContentIfNotAlreadyHandled(): T? {
        return if (hasAlreadyBeenHandled) {
            null
        } else {
            hasAlreadyBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}