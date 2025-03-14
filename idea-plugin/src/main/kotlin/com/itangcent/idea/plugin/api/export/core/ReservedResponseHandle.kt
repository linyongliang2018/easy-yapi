package com.itangcent.idea.plugin.api.export.core

import org.apache.http.HttpResponse
import org.apache.http.client.ResponseHandler
import java.util.*

/**
 * A wrapper for `ResponseHandler` that preserves the HTTP response.
 * It stores the response object along with the result processed by the delegate.
 *
 * `ResponseHandler` 的包装类，可保留 HTTP 响应。
 * 它存储由 `delegate` 处理的结果，同时保留 `HttpResponse` 以供后续使用。
 */
class ReservedResponseHandle<T>(private var delegate: ResponseHandler<T>) : ResponseHandler<ReservedResult<T>> {

    /**
     * Handles the HTTP response and returns a `ReservedResult` that contains both
     * the processed result and the original HTTP response.
     *
     * 处理 HTTP 响应，并返回包含处理结果和原始 HTTP 响应的 `ReservedResult`。
     */
    override fun handleResponse(httpResponse: HttpResponse?): ReservedResult<T> {
        val result = delegate.handleResponse(httpResponse)
        return ReservedResult(result, httpResponse)
    }

    companion object {

        /**
         * Wraps a given `ResponseHandler` with a `ReservedResponseHandle`.
         * If the handler is already wrapped, it reuses the existing instance.
         *
         * 用 `ReservedResponseHandle` 包装 `ResponseHandler`。
         * 如果 `delegate` 已经被包装，则直接返回已有实例。
         *
         * @param delegate The original `ResponseHandler`.
         *                 原始的 `ResponseHandler`。
         * @return A wrapped `ReservedResponseHandle`.
         *         包装后的 `ReservedResponseHandle`。
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> wrap(delegate: ResponseHandler<T>): ReservedResponseHandle<T> {
            if (delegate is ReservedResponseHandle<*>) {
                return delegate as ReservedResponseHandle<T>
            }

            var reservedResponseHandle = weakHashMap[delegate]
            if (reservedResponseHandle != null) {
                return reservedResponseHandle as ReservedResponseHandle<T>
            }
            synchronized(weakHashMap) {
                reservedResponseHandle = weakHashMap[delegate] ?: ReservedResponseHandle(delegate)
                weakHashMap[delegate] = reservedResponseHandle
                return reservedResponseHandle as ReservedResponseHandle<T>
            }
        }

        /**
         * A weak reference cache to store previously wrapped handlers.
         *
         * 弱引用缓存，用于存储已经包装的 `ResponseHandler`。
         */
        private val weakHashMap = WeakHashMap<ResponseHandler<*>, ReservedResponseHandle<*>>()
    }
}

/**
 * A wrapper for the HTTP response that preserves both the result and HTTP metadata.
 *
 * `ReservedResult` 用于存储 HTTP 响应的处理结果，并提供相关元数据。
 */
class ReservedResult<T>(
    private var result: T,
    private var httpResponse: HttpResponse?
) {

    /**
     * Returns the processed result.
     *
     * 获取处理后的结果。
     */
    fun result(): T {
        return this.result
    }

    /**
     * Returns the HTTP status code of the response.
     *
     * 获取 HTTP 响应的状态码。
     */
    fun status(): Int? {
        return httpResponse?.statusLine?.statusCode
    }

    /**
     * Retrieves the last value of a specific header in the response.
     *
     * 获取 HTTP 响应中的特定 `header` 的最后一个值。
     *
     * @param headerName The name of the header to retrieve.
     *                   需要获取的 `header` 名称。
     * @return The value of the header, or `null` if not found.
     *         `header` 的值，如果未找到则返回 `null`。
     */
    fun header(headerName: String): String? {
        return httpResponse?.getLastHeader(headerName)?.value
    }

    /**
     * Retrieves all values of a specific header in the response.
     *
     * 获取 HTTP 响应中所有匹配 `header` 名称的值。
     *
     * @param headerName The name of the header.
     *                   `header` 名称。
     * @return A list of header values, or an empty list if not found.
     *         `header` 的所有值，如果未找到则返回空列表。
     */
    fun headers(headerName: String): List<String> {
        return httpResponse?.getHeaders(headerName)
            ?.mapNotNull { it.value } ?: emptyList()
    }
}

/**
 * Extension function to wrap a `ResponseHandler` with `ReservedResponseHandle`.
 *
 * 扩展函数：为 `ResponseHandler` 添加 `ReservedResponseHandle` 包装。
 */
fun <T> ResponseHandler<T>.reserved(): ReservedResponseHandle<T> {
    return ReservedResponseHandle.wrap(this)
}
