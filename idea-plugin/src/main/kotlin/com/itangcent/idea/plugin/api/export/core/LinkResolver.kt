package com.itangcent.idea.plugin.api.export.core

import com.google.inject.ImplementedBy
import com.itangcent.idea.plugin.api.export.core.DefaultLinkResolver

/**
 * 处理@link标签的
 *
 */
@ImplementedBy(DefaultLinkResolver::class)
interface LinkResolver {

    fun linkToClass(linkClass: Any): String?

    fun linkToMethod(linkMethod: Any): String?

    fun linkToProperty(linkField: Any): String?
}