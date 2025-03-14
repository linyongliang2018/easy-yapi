package com.itangcent.idea.plugin.api.export.core

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.itangcent.common.utils.Extensible
import com.itangcent.common.utils.SimpleExtensible
import com.itangcent.intellij.jvm.duck.DuckType
import com.itangcent.intellij.jvm.element.ExplicitElement
import com.itangcent.intellij.jvm.element.ExplicitMethod
import com.itangcent.intellij.jvm.element.ExplicitParameter
import kotlin.reflect.KClass

/**
 * The base interface for export contexts.
 * It provides a mechanism to navigate up the context hierarchy and retrieve PSI elements.
 *
 * 导出上下文的基本接口。
 * 它提供了一种机制，可以在上下文层级中向上导航，并检索 PSI 元素。
 */
interface ExportContext : Extensible {

    /**
     * The parent context, allowing navigation up the context hierarchy.
     *
     * 获取当前上下文的父级，以便在层级结构中向上导航。
     * @return 父级 `ExportContext`，如果没有父级，则返回 `null`。
     */
    fun parent(): ExportContext?

    /**
     * Returns the PSI element which corresponds to this element.
     *
     * 返回与此上下文关联的 PSI 元素。
     * @return 关联的 `PsiElement`。
     */
    fun psi(): PsiElement
}

/**
 * Extends ExportContext for contexts dealing with variables (methods or parameters).
 *
 * 扩展 `ExportContext`，用于处理变量（方法或参数）的上下文。
 */
interface VariableExportContext : ExportContext {

    /**
     * The name of the variable.
     *
     * 变量的名称。
     * @return 变量名称。
     */
    fun name(): String

    /**
     * The type of the variable, which may be null if the type is not resolved.
     *
     * 变量的类型，如果类型未解析，则可能返回 `null`。
     * @return 变量的 `DuckType` 类型。
     */
    fun type(): DuckType?

    /**
     * The explicit element representation of the variable.
     *
     * 变量的显式元素表示。
     * @return `ExplicitElement` 的实例。
     */
    fun element(): ExplicitElement<*>

    /**
     * Sets a resolved name for the variable, typically used for renaming.
     *
     * 设置变量的已解析名称，通常用于重命名。
     * @param name 变量的新名称。
     */
    fun setResolvedName(name: String)
}

//region kits of ExportContext

/**
 * Find specific contexts by type.
 *
 * 通过类型查找特定的上下文。
 */
@Suppress("UNCHECKED_CAST")
fun <T : ExportContext> ExportContext.findContext(condition: KClass<T>): T? {
    return findContext { condition.isInstance(it) } as? T
}

/**
 * Find specific contexts by condition.
 *
 * 根据条件查找特定的上下文。
 */
fun ExportContext.findContext(condition: (ExportContext) -> Boolean): ExportContext? {
    var exportContext: ExportContext? = this
    while (exportContext != null) {
        if (condition(exportContext)) {
            return exportContext
        }
        exportContext = exportContext.parent()
    }
    return null
}

//endregion

/**
 * Base context with no parent, typically used for top-level classes.
 *
 * 没有父级的基础上下文，通常用于顶级类。
 */
abstract class RootExportContext :
    SimpleExtensible(), ExportContext {
    override fun parent(): ExportContext? {
        return null
    }
}

/**
 * General purpose context implementation with a specified parent context.
 *
 * 具有特定父级上下文的通用上下文实现。
 */
abstract class AbstractExportContext(private val parent: ExportContext) :
    SimpleExtensible(), VariableExportContext {

    private var resolvedName: String? = null

    override fun parent(): ExportContext? {
        return this.parent
    }

    /**
     * Returns the name of the element.
     *
     * 返回元素的名称。
     * @return 元素名称。
     */
    override fun name(): String {
        return resolvedName ?: element().name()
    }

    override fun setResolvedName(name: String) {
        this.resolvedName = name
    }
}

/**
 * Context specifically for a class.
 *
 * 用于表示类的上下文。
 */
class ClassExportContext(val cls: PsiClass) : RootExportContext() {
    override fun psi(): PsiClass {
        return cls
    }
}

/**
 * Context for a method, containing specifics about the method being exported.
 *
 * 用于方法的上下文，包含有关正在导出的方法的详细信息。
 */
class MethodExportContext(
    parent: ExportContext,
    private val method: ExplicitMethod
) : AbstractExportContext(parent), VariableExportContext {

    /**
     * Returns the name of the element.
     *
     * 返回方法的名称。
     * @return 方法名称。
     */
    override fun name(): String {
        return method.name()
    }

    /**
     * Returns the type of the variable.
     *
     * 返回方法的返回类型。
     * @return 方法的返回 `DuckType`。
     */
    override fun type(): DuckType? {
        return method.getReturnType()
    }

    override fun element(): ExplicitMethod {
        return method
    }

    override fun psi(): PsiMethod {
        return method.psi()
    }
}

/**
 * Context for a parameter, containing specifics about the parameter being exported.
 *
 * 用于参数的上下文，包含有关正在导出的参数的详细信息。
 */
interface ParameterExportContext : VariableExportContext {

    override fun element(): ExplicitParameter

    override fun psi(): PsiParameter
}

/**
 * Creates a `ParameterExportContext` instance.
 *
 * 创建 `ParameterExportContext` 实例。
 */
fun ParameterExportContext(
    parent: ExportContext,
    parameter: ExplicitParameter
): ParameterExportContext {
    return ParameterExportContextImpl(parent, parameter)
}

class ParameterExportContextImpl(
    parent: ExportContext,
    private val parameter: ExplicitParameter
) : AbstractExportContext(parent), ParameterExportContext {

    /**
     * Returns the type of the variable.
     *
     * 返回参数的类型。
     * @return 参数的 `DuckType`。
     */
    override fun type(): DuckType? {
        return parameter.getType()
    }

    override fun element(): ExplicitParameter {
        return parameter
    }

    override fun psi(): PsiParameter {
        return parameter.psi()
    }
}

/**
 * Retrieve `ClassExportContext` based on the current context.
 *
 * 根据当前上下文获取 `ClassExportContext`。
 */
fun ExportContext.classContext(): ClassExportContext? {
    return this.findContext(ClassExportContext::class)
}

/**
 * Retrieve `MethodExportContext` based on the current context.
 *
 * 根据当前上下文获取 `MethodExportContext`。
 */
fun ExportContext.methodContext(): MethodExportContext? {
    return this.findContext(MethodExportContext::class)
}

/**
 * Retrieve `ParameterExportContext` based on the current context.
 *
 * 根据当前上下文获取 `ParameterExportContext`。
 */
fun ExportContext.paramContext(): ParameterExportContext? {
    return this.findContext(ParameterExportContext::class)
}

/**
 * Searches for an extended property, first locally then up the context hierarchy.
 *
 * 搜索扩展属性，首先在本地查找，如果未找到，则向上层级查找。
 */
fun <T> ExportContext.searchExt(attr: String): T? {
    this.getExt<T>(attr)?.let { return it }
    this.parent()?.searchExt<T>(attr)?.let { return it }
    return null
}