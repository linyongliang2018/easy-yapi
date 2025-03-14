package com.itangcent.idea.plugin.api.export.core

import com.google.inject.Inject
import com.google.inject.Singleton
import com.itangcent.common.constant.Attrs
import com.itangcent.common.kit.KVUtils
import com.itangcent.common.model.*
import com.itangcent.common.utils.appendln
import com.itangcent.intellij.extend.toPrettyString
import com.itangcent.intellij.logger.Logger
import com.itangcent.intellij.util.forEachValid
import java.util.*

@Singleton
open class DefaultRequestBuilderListener : RequestBuilderListener {

    @Inject
    private lateinit var logger: Logger

    /**
     * 设置请求的名称。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param name 请求名称
     */
    override fun setName(exportContext: ExportContext, request: Request, name: String) {
        request.name = name
    }


    /**
     * 设置请求的方法（GET/POST/PUT等）。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param method 请求方法
     */
    override fun setMethod(exportContext: ExportContext, request: Request, method: String) {
        request.method = method
    }


    /**
     * 设置请求路径。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param path 请求路径
     */
    override fun setPath(exportContext: ExportContext, request: Request, path: URL) {
        request.path = path
    }

    /**
     * 以 JSON 形式设置请求体。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param model 请求体数据
     */
    override fun setModelAsBody(exportContext: ExportContext, request: Request, model: Any) {
        request.body = model
    }


    /**
     * 以查询参数的形式添加请求参数。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param model 参数数据（Map 形式）
     */
    override fun addModelAsParam(exportContext: ExportContext, request: Request, model: Any) {
        if (model is Map<*, *>) {
            val comment = model[Attrs.COMMENT_ATTR] as Map<*, *>?
            val default = model[Attrs.DEFAULT_VALUE_ATTR] as Map<*, *>?
            model.forEachValid { k, v ->
                addParam(
                    exportContext, request, k.toString(), (default?.get(k) ?: v).toPrettyString(),
                    KVUtils.getUltimateComment(comment, k)
                )
            }
        } else {
            logger.warn("addModelAsParam failed, invalid model:$model, type: ${model::class.qualifiedName}")
        }
    }

    /**
     * 以表单参数的形式添加请求参数。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param model 表单参数数据（Map 形式）
     */
    override fun addModelAsFormParam(exportContext: ExportContext, request: Request, model: Any) {
        if (model is Map<*, *>) {
            val comment = model[Attrs.COMMENT_ATTR] as Map<*, *>?
            val default = model[Attrs.DEFAULT_VALUE_ATTR] as Map<*, *>?
            model.forEachValid { k, v ->
                addFormParam(
                    exportContext, request, k.toString(), (default?.get(k) ?: v).toPrettyString(),
                    KVUtils.getUltimateComment(comment, k)
                )
            }
        } else {
            logger.warn("addModelAsFormParam failed, invalid model:$model, type: ${model::class.qualifiedName}")
        }
    }

    /**
     * 添加表单参数。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param formParam 表单参数
     */
    override fun addFormParam(exportContext: ExportContext, request: Request, formParam: FormParam) {
        if (request.formParams == null) {
            request.formParams = LinkedList()
        }
        request.formParams!!.add(formParam)
    }

    /**
     * 添加查询参数。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param param 查询参数
     */
    override fun addParam(exportContext: ExportContext, request: Request, param: Param) {
        if (request.querys == null) {
            request.querys = LinkedList()
        }
        request.querys!!.add(param)
    }

    /**
     * 移除查询参数。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param param 要移除的参数
     */
    override fun removeParam(exportContext: ExportContext, request: Request, param: Param) {
        request.querys?.remove(param)
    }

    /**
     * 添加路径参数。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param pathParam 路径参数
     */
    override fun addPathParam(exportContext: ExportContext, request: Request, pathParam: PathParam) {
        if (request.paths == null) {
            request.paths = LinkedList()
        }
        request.paths!!.add(pathParam)
    }


    /**
     * 设置 JSON 请求体。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param body JSON 数据
     * @param bodyAttr 请求体属性
     */
    override fun setJsonBody(exportContext: ExportContext, request: Request, body: Any?, bodyAttr: String?) {
        request.body = body
        request.bodyAttr = bodyAttr
        request.bodyType = "json"
    }

    /**
     * 追加描述信息。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param desc 描述信息
     */
    override fun appendDesc(exportContext: ExportContext, request: Request, desc: String?) {
        request.desc = request.desc.appendln(desc)
    }

    /**
     * 添加请求头信息。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param header 请求头
     */
    override fun addHeader(exportContext: ExportContext, request: Request, header: Header) {
        if (request.headers == null) {
            request.headers = LinkedList()
        }
        request.headers!!.removeIf { it.name == header.name }
        request.headers!!.add(header)
    }

    /**
     * 添加响应体信息。
     * @param exportContext 导出上下文
     * @param request 请求对象
     * @param response 响应体
     */
    override fun addResponse(exportContext: ExportContext, request: Request, response: Response) {
        if (request.response == null) {
            request.response = LinkedList()
        }
        request.response!!.add(response)
    }


    /**
     * 添加响应头
     *
     * @param exportContext
     * @param response
     * @param header
     */
    override fun addResponseHeader(exportContext: ExportContext, response: Response, header: Header) {

        if (response.headers == null) {
            response.headers = LinkedList()
        }
        response.headers!!.add(header)
    }

    override fun setResponseBody(exportContext: ExportContext, response: Response, bodyType: String, body: Any?) {
        response.bodyType = bodyType
        response.body = body
    }

    override fun setResponseCode(exportContext: ExportContext, response: Response, code: Int) {
        response.code = code
    }

    /**
     * 追加响应体描述信息。
     * @param exportContext 导出上下文
     * @param response 响应对象
     * @param bodyDesc 响应体描述
     */
    override fun appendResponseBodyDesc(exportContext: ExportContext, response: Response, bodyDesc: String?) {
        if (response.bodyDesc.isNullOrBlank()) {
            response.bodyDesc = bodyDesc
        } else {
            response.bodyDesc = response.bodyDesc + "\n" + bodyDesc
        }
    }

    /**
     * 方法开始处理时调用（默认无操作）。
     */
    override fun startProcessMethod(methodExportContext: MethodExportContext, request: Request) {
        //NOP
    }

    /**
     * 方法处理完成时调用（默认无操作）。
     */
    override fun processCompleted(methodExportContext: MethodExportContext, request: Request) {
        //NOP
    }
}