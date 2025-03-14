package com.itangcent.idea.plugin.api.export.core

import com.google.inject.ImplementedBy
import com.google.inject.Inject
import com.google.inject.Singleton
import com.intellij.psi.PsiMethod
import com.itangcent.idea.plugin.settings.helper.IntelligentSettingsHelper
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.extend.findCurrentMethod

/**
 * Interface for filtering methods.
 * Implementations define specific filtering logic to determine whether a method should be processed.
 *
 * 方法过滤器接口。
 * 具体实现定义过滤逻辑，以决定某个方法是否应被处理。
 */
@ImplementedBy(EmptyMethodFilter::class)
interface MethodFilter {

    /**
     * Determines whether the given method passes the filter.
     *
     * 判断给定的方法是否通过过滤器。
     * @param method The method to check. 要检查的方法。
     * @return `true` if the method is allowed, `false` otherwise. 若方法通过检查返回 `true`，否则返回 `false`。
     */
    fun checkMethod(method: PsiMethod): Boolean
}

/**
 * Default implementation of MethodFilter that allows all methods.
 *
 * `MethodFilter` 的默认实现，允许所有方法通过。
 */
@Singleton
class EmptyMethodFilter : MethodFilter {

    /**
     * Always returns `true`, allowing all methods.
     *
     * 始终返回 `true`，允许所有方法通过。
     */
    override fun checkMethod(method: PsiMethod): Boolean {
        return true
    }
}

/**
 * Configurable method filter based on user-selected settings.
 *
 * 可配置的方法过滤器，根据用户的选项决定是否过滤方法。
 */
@Singleton
class ConfigurableMethodFilter : MethodFilter {

    @Inject
    private lateinit var intelligentSettingsHelper: IntelligentSettingsHelper

    /**
     * The currently selected method in the editor.
     *
     * 编辑器中当前选中的方法。
     */
    private val selectedMethod by lazy {
        return@lazy ActionContext.getContext()?.findCurrentMethod()
    }

    /**
     * Checks if the given method matches the user-selected method.
     * If "selectedOnly" is enabled, only the selected method passes.
     *
     * 检查给定的方法是否与用户选中的方法匹配。
     * 如果启用了 `"selectedOnly"` 选项，则只有选中的方法能通过过滤。
     */
    override fun checkMethod(method: PsiMethod): Boolean {
        if (intelligentSettingsHelper.selectedOnly()) {
            return selectedMethod == null || selectedMethod == method
        }
        return true
    }
}

/**
 * A method filter that only allows a specific method.
 *
 * 仅允许特定方法通过的过滤器。
 */
@Singleton
class SpecialMethodFilter(
    private val specialMethod: PsiMethod
) : MethodFilter {

    /**
     * Returns `true` only if the given method matches the special method.
     *
     * 仅当方法与指定的 `specialMethod` 相同时返回 `true`。
     */
    override fun checkMethod(method: PsiMethod): Boolean {
        return specialMethod == method
    }
}

/**
 * A customizable method filter that combines multiple filters.
 *
 * 可定制的方法过滤器，可组合多个过滤条件。
 */
@Singleton
class CustomizedMethodFilter : MethodFilter {

    /**
     * List of method filters to be applied.
     *
     * 需要应用的 `MethodFilter` 列表。
     */
    private val methodFilters: MutableList<MethodFilter> = mutableListOf()

    /**
     * Adds a new filter to the list.
     *
     * 添加新的 `MethodFilter` 到列表中。
     * @param methodFilter The filter to add. 要添加的过滤器。
     */
    fun addMethodFilter(methodFilter: MethodFilter) {
        methodFilters.add(methodFilter)
    }

    /**
     * Replaces the existing filters with a new list.
     *
     * 用新的过滤器列表替换当前的列表。
     * @param methodFilters The new list of filters. 新的过滤器列表。
     */
    fun setMethodFilters(methodFilters: List<MethodFilter>) {
        this.methodFilters.clear()
        this.methodFilters.addAll(methodFilters)
    }

    /**
     * Clears all existing filters.
     *
     * 清除所有过滤器。
     */
    fun clearMethodFilters() {
        methodFilters.clear()
    }

    /**
     * Checks if the method passes all filters in the list.
     *
     * 依次应用所有过滤器，若方法通过所有过滤器则返回 `true`。
     * @param method The method to check. 需要检查的方法。
     * @return `true` if all filters allow the method. 若所有过滤器都允许该方法，则返回 `true`。
     */
    override fun checkMethod(method: PsiMethod): Boolean {
        return methodFilters.all { it.checkMethod(method) }
    }
}
