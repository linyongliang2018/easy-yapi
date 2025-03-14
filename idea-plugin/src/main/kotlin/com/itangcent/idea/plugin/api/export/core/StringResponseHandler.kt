package com.itangcent.idea.plugin.api.export.core

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpResponseException
import org.apache.http.client.ResponseHandler
import org.apache.http.util.consume
import org.apache.http.util.readString
import java.io.IOException

/**
 * A `ResponseHandler` implementation that processes HTTP responses
 * and returns the response body as a `String`.
 *
 * `ResponseHandler` 的实现类，处理 HTTP 响应并返回 `String` 类型的响应体。
 */
class StringResponseHandler : ResponseHandler<String> {

    /**
     * Reads and returns the content of an `HttpEntity` as a string.
     *
     * 读取 `HttpEntity` 的内容并返回 `String` 类型的数据。
     *
     * @param entity The `HttpEntity` to be read.
     *               需要读取的 `HttpEntity`。
     * @return The content of the entity as a string.
     *         `HttpEntity` 的内容，以 `String` 形式返回。
     * @throws IOException If an I/O error occurs while reading the entity.
     *                     读取实体时发生 I/O 错误。
     */
    @Throws(IOException::class)
    fun handleEntity(entity: HttpEntity): String {
        return entity.readString()
    }

    /**
     * Handles the HTTP response and extracts the response body as a string.
     * If the response contains an error status (>= 300), an exception is thrown.
     *
     * 处理 HTTP 响应并提取响应体为字符串。
     * 如果 HTTP 响应状态码大于等于 300，则抛出异常。
     *
     * @param response The HTTP response object.
     *                 HTTP 响应对象。
     * @return The response body as a string, or `"empty response"` if the entity is null.
     *         返回 `String` 类型的响应体内容，如果 `entity` 为空，则返回 `"empty response"`。
     * @throws HttpResponseException If the response status code is >= 300.
     *                               当 HTTP 响应状态码大于等于 300 时抛出异常。
     * @throws IOException If an I/O error occurs while processing the response.
     *                     处理响应时发生 I/O 错误。
     */
    @Throws(HttpResponseException::class, IOException::class)
    override fun handleResponse(response: HttpResponse): String? {
        val statusLine = response.statusLine
        val entity = response.entity

        return try {
            if (entity == null) null else this.handleEntity(entity)
        } catch (e: Exception) {
            if (statusLine.statusCode >= 300) {
                entity.consume()
                throw HttpResponseException(statusLine.statusCode, statusLine.reasonPhrase)
            }
            "empty response"
        }
    }

    companion object {
        /**
         * Default instance of `StringResponseHandler` for reuse.
         *
         * `StringResponseHandler` 的默认实例，可复用。
         */
        val DEFAULT_RESPONSE_HANDLER = StringResponseHandler()
    }
}
