package com.itangcent.idea.plugin.api.export.core

import com.google.inject.Singleton
import com.itangcent.common.model.MethodDoc
import com.itangcent.common.model.Param

/**
 * 默认的 `MethodDocBuilderListener` 实现类，用于构建方法的文档信息。
 * 该类提供了默认的逻辑：
 * - 设置方法名称
 * - 追加方法描述
 * - 维护方法参数列表
 * - 设置方法返回值
 * - 追加返回值描述
 *
 * 其中 `startProcessMethod` 和 `processCompleted` 为空实现 (`NOP`)，
 * 子类可以选择性地重写它们以添加额外的逻辑。
 */
@Singleton
open class DefaultMethodDocBuilderListener : MethodDocBuilderListener {

    /**
     * 设置方法的名称。
     *
     * @param exportContext  导出上下文
     * @param methodDoc      目标方法文档对象
     * @param name           方法名称
     */
    override fun setName(
        exportContext: ExportContext,
        methodDoc: MethodDoc, name: String
    ) {
        methodDoc.name = name
    }

    /**
     * 追加方法的描述信息。
     * 如果 `methodDoc.desc` 为空，则直接赋值；否则，将 `desc` 追加到原来的描述上。
     *
     * @param exportContext  导出上下文
     * @param methodDoc      目标方法文档对象
     * @param desc           方法的描述信息
     */
    override fun appendDesc(
        exportContext: ExportContext,
        methodDoc: MethodDoc, desc: String?
    ) {
        if (methodDoc.desc == null) {
            methodDoc.desc = desc
        } else {
            methodDoc.desc = "${methodDoc.desc}$desc"
        }
    }

    /**
     * 添加方法的参数信息。
     * 如果 `methodDoc.params` 为空，则初始化参数列表；然后将 `param` 添加到 `params` 列表中。
     *
     * @param exportContext  导出上下文
     * @param methodDoc      目标方法文档对象
     * @param param          方法的参数信息
     */
    override fun addParam(
        exportContext: ExportContext,
        methodDoc: MethodDoc, param: Param
    ) {
        if (methodDoc.params == null) {
            methodDoc.params = ArrayList()
        }
        methodDoc.params!!.add(param)
    }

    /**
     * 设置方法的返回值信息。
     *
     * @param exportContext  导出上下文
     * @param methodDoc      目标方法文档对象
     * @param ret            方法的返回值
     */
    override fun setRet(
        exportContext: ExportContext,
        methodDoc: MethodDoc, ret: Any?
    ) {
        methodDoc.ret = ret
    }

    /**
     * 追加方法的返回值描述信息。
     * 如果 `methodDoc.retDesc` 为空，则直接赋值；否则，在原有描述的基础上追加换行符后再拼接 `retDesc`。
     *
     * @param exportContext  导出上下文
     * @param methodDoc      目标方法文档对象
     * @param retDesc        返回值的描述信息
     */
    override fun appendRetDesc(
        exportContext: ExportContext,
        methodDoc: MethodDoc, retDesc: String?
    ) {
        if (methodDoc.retDesc.isNullOrBlank()) {
            methodDoc.retDesc = retDesc
        } else {
            methodDoc.retDesc = methodDoc.retDesc + "\n" + retDesc
        }
    }

    /**
     * 在方法处理开始时调用。
     * 该默认实现为空操作 (`NOP`)，子类可以重写此方法以添加处理逻辑。
     *
     * @param methodExportContext  方法导出上下文
     * @param methodDoc            目标方法文档对象
     */
    override fun startProcessMethod(methodExportContext: MethodExportContext, methodDoc: MethodDoc) {
        // NOP（No Operation），默认无操作，留给子类扩展
    }

    /**
     * 在方法处理完成后调用。
     * 该默认实现为空操作 (`NOP`)，子类可以重写此方法以添加处理逻辑。
     *
     * @param methodExportContext  方法导出上下文
     * @param methodDoc            目标方法文档对象
     */
    override fun processCompleted(methodExportContext: MethodExportContext, methodDoc: MethodDoc) {
        // NOP（No Operation），默认无操作，留给子类扩展
    }
}