package com.itangcent.idea.plugin.api.export.core

import com.google.inject.ImplementedBy
import com.itangcent.common.model.Doc
import com.itangcent.common.model.MethodDoc
import com.itangcent.common.model.Request
import kotlin.reflect.KClass

/**
 * 类导出接口
 *
 */
@ImplementedBy(DefaultClassExporter::class)
interface ClassExporter {

    /**
     * @return return true if this ClassExporter can parse the cls
     */
    fun export(cls: Any, docHandle: DocHandle): Boolean

    /**
     * the document type which be generate
     */
    fun support(docType: KClass<*>): Boolean
}

typealias DocHandle = (Doc) -> Unit

/**
 * 这里判断是不是Request对象,是才会操作
 *
 * @param requestHandle
 * @return
 */
inline fun requestOnly(crossinline requestHandle: ((Request) -> Unit)): DocHandle {
    return {
        if (it is Request) {
            requestHandle(it)
        }
    }
}

/**
 * 这里判断是不是MethodDoc,是才执行,有可能不是http,普通的也行
 *
 * @param methodDocHandle
 * @return
 */
inline fun methodDocOnly(crossinline methodDocHandle: ((MethodDoc) -> Unit)): DocHandle {
    return {
        if (it is MethodDoc) {
            methodDocHandle(it)
        }
    }
}

/**
 * 直接执行Doc函数
 *
 * @param docHandle
 * @return
 */
inline fun docs(crossinline docHandle: DocHandle): DocHandle {
    return {
        docHandle(it)
    }
}