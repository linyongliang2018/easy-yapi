package com.itangcent.idea.plugin.api.export.core

import com.google.inject.ImplementedBy
import com.itangcent.common.model.*

/**
 * Interface for building request structures.
 * Defines methods for setting request attributes, adding parameters, and modifying response details.
 *
 * 请求构造器接口。
 * 该接口定义了设置请求属性、添加参数和修改响应详情的方法。
 */
@ImplementedBy(DefaultRequestBuilderListener::class)
interface RequestBuilderListener {

    /**
     * Sets the name of the request.
     *
     * 设置请求的名称。
     */
    fun setName(
        exportContext: ExportContext,
        request: Request,
        name: String,
    )

    /**
     * Sets the HTTP method (GET, POST, etc.) for the request.
     *
     * 设置请求的 HTTP 方法（如 GET, POST 等）。
     */
    fun setMethod(
        exportContext: ExportContext,
        request: Request,
        method: String,
    )

    /**
     * Sets the request URL.
     *
     * 设置请求的 URL 地址。
     */
    fun setPath(
        exportContext: ExportContext,
        request: Request,
        path: URL,
    )

    /**
     * Adds the model as the request body.
     * If the content-type is JSON, the model is added as a JSON body.
     * Otherwise, it is added as a form parameter.
     *
     * 将模型作为请求体添加。
     * 如果 `content-type` 为 JSON，则作为 JSON 请求体添加。
     * 否则，它将作为表单参数添加。
     */
    fun setModelAsBody(
        exportContext: ExportContext,
        request: Request,
        model: Any,
    )

    /**
     * Adds a model as request parameters.
     *
     * 将模型添加为请求参数。
     */
    fun addModelAsParam(
        exportContext: ExportContext,
        request: Request,
        model: Any,
    )

    /**
     * Adds a model as form parameters.
     *
     * 将模型添加为表单参数。
     */
    fun addModelAsFormParam(
        exportContext: ExportContext,
        request: Request,
        model: Any,
    )

    /**
     * Adds a form parameter to the request.
     *
     * 添加表单参数到请求中。
     */
    fun addFormParam(
        exportContext: ExportContext,
        request: Request,
        formParam: FormParam,
    )

    /**
     * Adds a query parameter to the request.
     *
     * 添加查询参数到请求中。
     */
    fun addParam(
        exportContext: ExportContext,
        request: Request,
        param: Param,
    )

    /**
     * Removes a query parameter from the request.
     *
     * 从请求中移除查询参数。
     */
    fun removeParam(
        exportContext: ExportContext,
        request: Request,
        param: Param,
    )

    /**
     * Adds a path parameter to the request.
     *
     * 向请求中添加路径参数。
     */
    fun addPathParam(
        exportContext: ExportContext,
        request: Request,
        pathParam: PathParam,
    )

    /**
     * Sets the request body as JSON.
     *
     * 将请求体设置为 JSON 格式。
     */
    fun setJsonBody(
        exportContext: ExportContext,
        request: Request,
        body: Any?,
        bodyAttr: String?,
    )

    /**
     * Appends a description to the request.
     *
     * 向请求追加描述信息。
     */
    fun appendDesc(
        exportContext: ExportContext,
        request: Request,
        desc: String?,
    )

    /**
     * Adds a header to the request.
     *
     * 添加请求头信息。
     */
    fun addHeader(
        exportContext: ExportContext,
        request: Request,
        header: Header,
    )

    //region response

    /**
     * Adds a response to the request.
     *
     * 向请求添加响应信息。
     */
    fun addResponse(
        exportContext: ExportContext,
        request: Request,
        response: Response,
    )

    /**
     * Adds a response header.
     *
     * 添加响应头信息。
     */
    fun addResponseHeader(
        exportContext: ExportContext,
        response: Response,
        header: Header,
    )

    /**
     * Sets the response body.
     *
     * 设置响应体。
     */
    fun setResponseBody(
        exportContext: ExportContext,
        response: Response,
        bodyType: String,
        body: Any?,
    )

    /**
     * Sets the HTTP response code.
     *
     * 设置 HTTP 响应码。
     */
    fun setResponseCode(
        exportContext: ExportContext,
        response: Response,
        code: Int,
    )

    /**
     * Appends a description to the response body.
     *
     * 向响应体追加描述信息。
     */
    fun appendResponseBodyDesc(
        exportContext: ExportContext,
        response: Response,
        bodyDesc: String?,
    )

    /**
     * Called when a method starts processing.
     *
     * 当方法开始处理时调用。
     */
    fun startProcessMethod(methodExportContext: MethodExportContext, request: Request)

    /**
     * Called when method processing is completed.
     *
     * 当方法处理完成时调用。
     */
    fun processCompleted(methodExportContext: MethodExportContext, request: Request)

    //endregion
}

//region utils------------------------------------------------------------------

/**
 * Adds a query parameter with a default required value of `false`.
 *
 * 添加查询参数，默认 `required` 为 `false`。
 */
fun RequestBuilderListener.addParam(
    exportContext: ExportContext, request: Request,
    paramName: String, value: Any?, attr: String?,
) {
    addParam(
        exportContext, request,
        paramName, value, false, attr
    )
}

/**
 * Adds a query parameter with a custom required flag.
 *
 * 添加查询参数，可指定是否 `required`。
 */
fun RequestBuilderListener.addParam(
    exportContext: ExportContext, request: Request,
    paramName: String, value: Any?, required: Boolean, desc: String?,
): Param {
    val param = Param()
    param.name = paramName
    param.value = value
    param.required = required
    param.desc = desc
    this.addParam(exportContext, request, param)
    return param
}

fun RequestBuilderListener.addFormParam(
    exportContext: ExportContext, request: Request,
    paramName: String, defaultVal: String?, desc: String?,
) {
    addFormParam(
        exportContext, request,
        paramName, defaultVal, false, desc
    )
}

fun RequestBuilderListener.addFormParam(
    exportContext: ExportContext, request: Request,
    paramName: String, value: String?, required: Boolean, desc: String?,
): FormParam {
    val param = FormParam()
    param.name = paramName
    param.value = value
    param.required = required
    param.desc = desc
    param.type = "text"
    this.addFormParam(exportContext, request, param)
    return param
}

/**
 * Adds a file parameter to a form request.
 *
 * 向表单请求中添加文件参数。
 */
fun RequestBuilderListener.addFormFileParam(
    exportContext: ExportContext, request: Request,
    paramName: String, required: Boolean, desc: String?,
): FormParam {
    val param = FormParam()
    param.name = paramName
    param.required = required
    param.desc = desc
    param.type = "file"
    this.addFormParam(exportContext, request, param)
    return param
}

/**
 * Adds a header to the request.
 *
 * 向请求添加头信息。
 */
fun RequestBuilderListener.addHeader(
    exportContext: ExportContext, request: Request,
    name: String, value: String?,
): Header {
    val header = Header()
    header.name = name
    header.value = value
    header.required = true
    addHeader(exportContext, request, header)
    return header
}

fun RequestBuilderListener.addHeaderIfMissed(
    exportContext: ExportContext, request: Request,
    name: String, value: String?,
): Boolean {
    if (request.header(name) != null) {
        return false
    }
    addHeader(
        exportContext, request,
        name, value
    )
    return true
}

fun RequestBuilderListener.addPathParam(
    exportContext: ExportContext, request: Request,
    name: String, desc: String?,
) {
    val pathParam = PathParam()
    pathParam.name = name
    pathParam.desc = desc
    this.addPathParam(
        exportContext, request,
        pathParam
    )
}

fun RequestBuilderListener.addPathParam(
    exportContext: ExportContext, request: Request,
    name: String, value: String?, desc: String?,
) {
    val pathParam = PathParam()
    pathParam.name = name
    pathParam.value = value
    pathParam.desc = desc
    this.addPathParam(
        exportContext, request,
        pathParam
    )
}

fun RequestBuilderListener.addResponseHeader(
    exportContext: ExportContext,
    response: Response,
    name: String,
    value: String?,
) {
    val header = Header()
    header.name = name
    header.value = value
    addResponseHeader(exportContext, response, header)
}

fun RequestBuilderListener.setMethodIfMissed(
    exportContext: ExportContext, request: Request,
    method: String,
) {
    if (request.hasMethod()) {
        return
    }
    this.setMethod(exportContext, request, method)
}

/**
 * Sets the content type header.
 *
 * 设置 `Content-Type` 头信息。
 */
fun RequestBuilderListener.setContentType(
    exportContext: ExportContext, request: Request,
    contentType: String,
) {
    this.addHeader(exportContext, request, "Content-Type", contentType)
}

//endregion utils------------------------------------------------------------------
