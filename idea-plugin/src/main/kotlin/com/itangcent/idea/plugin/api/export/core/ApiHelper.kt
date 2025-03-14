package com.itangcent.idea.plugin.api.export.core

import com.google.inject.Inject
import com.google.inject.Singleton
import com.intellij.psi.PsiMethod
import com.itangcent.common.kit.headLine
import com.itangcent.common.utils.append
import com.itangcent.common.utils.notNullOrEmpty
import com.itangcent.intellij.config.rule.RuleComputer
import com.itangcent.intellij.config.rule.computer
import com.itangcent.intellij.jvm.DocHelper
import com.itangcent.intellij.jvm.element.ExplicitMethod

@Singleton
open class ApiHelper {

    /**
     * 注入docHelper
     * Doc helper
     */
    @Inject
    private val docHelper: DocHelper? = null

    /**
     * 用来处理@link这个标签的
     * Doc parse helper
     */
    @Inject
    protected val docParseHelper: DocParseHelper? = null

    /**
     * 用来处理规则计算的,可以自定义规则
     * Rule computer
     */
    @Inject
    protected val ruleComputer: RuleComputer? = null

    /**
     * 用来解析api的名字
     *
     * @param psiMethod
     * @return
     */
    fun nameOfApi(psiMethod: PsiMethod): String {

        val nameByRule = ruleComputer!!.computer(ClassExportRuleKeys.API_NAME, psiMethod)
        if (nameByRule.notNullOrEmpty()) {
            return nameByRule!!
        }

        val attrOfDocComment = docHelper!!.getAttrOfDocComment(psiMethod)
        var headLine = attrOfDocComment?.headLine()
        if (headLine.notNullOrEmpty()) return headLine!!

        val docByRule = ruleComputer.computer(ClassExportRuleKeys.METHOD_DOC, psiMethod)
        headLine = docByRule?.headLine()
        if (headLine.notNullOrEmpty()) return headLine!!

        return psiMethod.name
    }

    /**
     * 获取方法中的属性
     *
     * @param method
     * @return
     */
    protected open fun findAttrOfMethod(method: ExplicitMethod): String? {
        val attrOfDocComment = docHelper!!.getAttrOfDocComment(method.psi())

        val docByRule = ruleComputer!!.computer(ClassExportRuleKeys.METHOD_DOC, method)

        return attrOfDocComment.append(docByRule, "\n")
    }

    /**
     * 获取方法中的属性
     *
     * @param method
     * @return
     */
    protected open fun findAttrOfMethod(method: PsiMethod): String? {
        val attrOfDocComment = docHelper!!.getAttrOfDocComment(method)

        val docByRule = ruleComputer!!.computer(ClassExportRuleKeys.METHOD_DOC, method)

        return attrOfDocComment.append(docByRule, "\n")
    }

    /**
     * 获取api的名称和属性
     *
     * @param explicitMethod
     * @return
     */
    fun nameAndAttrOfApi(explicitMethod: ExplicitMethod): Pair<String?, String?> {
        var name: String? = null
        var attr: String? = null
        nameAndAttrOfApi(explicitMethod, {
            name = it
        }, {
            attr = attr.append(it, "\n")
        })
        return name to attr
    }

    /**
     * 获取api的名称和属性
     *
     * @param explicitMethod
     * @param nameHandle
     * @param attrHandle
     */
    fun nameAndAttrOfApi(
        explicitMethod: ExplicitMethod, nameHandle: (String) -> Unit,
        attrHandle: (String) -> Unit
    ) {
        var named = false
        val nameByRule = ruleComputer!!.computer(ClassExportRuleKeys.API_NAME, explicitMethod)
        if (nameByRule.notNullOrEmpty()) {
            nameHandle(nameByRule!!)
            named = true
        }

        var attrOfMethod = findAttrOfMethod(explicitMethod)

        attrOfMethod = docParseHelper!!.resolveLinkInAttr(attrOfMethod, explicitMethod.psi())?.trim()

        if (attrOfMethod.notNullOrEmpty()) {
            attrOfMethod!!
            if (named) {
                attrHandle(attrOfMethod)
            } else {
                val headLine = attrOfMethod.headLine()
                nameHandle(headLine!!)
                named = true
                attrHandle(attrOfMethod.removePrefix(headLine).trimStart())
            }
        }

        if (!named) {
            nameHandle(explicitMethod.name())
        }
    }

    /**
     * 获取api的名称和属性
     *
     * @param explicitMethod
     * @param nameHandle
     * @param attrHandle
     */
    fun nameAndAttrOfApi(
        explicitMethod: PsiMethod, nameHandle: (String) -> Unit,
        attrHandle: (String) -> Unit
    ) {
        var named = false
        val nameByRule = ruleComputer!!.computer(ClassExportRuleKeys.API_NAME, explicitMethod)
        if (nameByRule.notNullOrEmpty()) {
            nameHandle(nameByRule!!)
            named = true
        }

        var attrOfMethod = findAttrOfMethod(explicitMethod)

        attrOfMethod = docParseHelper!!.resolveLinkInAttr(attrOfMethod, explicitMethod)

        if (attrOfMethod.notNullOrEmpty()) {
            attrOfMethod!!
            if (named) {
                attrHandle(attrOfMethod)
            } else {
                val headLine = attrOfMethod.headLine()
                nameHandle(headLine!!)
                named = true
                attrHandle(attrOfMethod.removePrefix(headLine).trimStart())
            }
        }

        if (!named) {
            nameHandle(explicitMethod.name)
        }
    }
}
