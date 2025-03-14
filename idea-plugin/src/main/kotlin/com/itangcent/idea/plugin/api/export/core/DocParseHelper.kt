package com.itangcent.idea.plugin.api.export.core

import com.google.inject.ImplementedBy
import com.intellij.psi.PsiMember

/**
 * 用来解析@link标签的
 */
@ImplementedBy(DefaultDocParseHelper::class)
interface DocParseHelper {
    fun resolveLinkInAttr(attr: String?, psiMember: PsiMember): String?
}