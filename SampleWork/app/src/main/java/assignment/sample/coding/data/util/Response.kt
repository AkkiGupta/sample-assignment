package assignment.sample.coding.data.util

/**
 * Response Class takes generic input and serve it with respected Success, Loading or Error status
 *
 * To make use, Wrap your response object in Response<T>
 */
sealed class Response<T>(val data: T? = null, val message: String? = null) {
    class Loading<T>(data: T? = null): Response<T>(data)
    class Success<T>(data: T): Response<T>(data)
    class Error<T>(message: String, data: T? = null): Response<T>(data, message)
}
