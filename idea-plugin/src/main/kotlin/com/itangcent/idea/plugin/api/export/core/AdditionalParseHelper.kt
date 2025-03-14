package com.itangcent.idea.plugin.api.export.core

import com.google.inject.ImplementedBy
import com.itangcent.common.model.Header
import com.itangcent.common.model.Param
import com.itangcent.idea.plugin.api.export.AdditionalField

/**
 * 这个作者明显是瓜皮,接口和实现放在一起杂乱无章
 *
 */
@ImplementedBy(DefaultAdditionalParseHelper::class)
interface AdditionalParseHelper {

    /**
     * 从json中解析头信息
     *
     * @param headerStr
     * @return
     */
    fun parseHeaderFromJson(headerStr: String): Header

    /**
     * 从json中解析参数
     *
     * @param paramStr
     * @return
     */
    fun parseParamFromJson(paramStr: String): Param

    /**
     * 从json中解析字段
     *
     * @param paramStr
     * @return
     */
    fun parseFieldFromJson(paramStr: String): AdditionalField
}