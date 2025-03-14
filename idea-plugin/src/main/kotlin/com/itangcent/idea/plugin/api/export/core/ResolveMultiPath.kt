package com.itangcent.idea.plugin.api.export.core

import com.itangcent.common.model.URL
import com.itangcent.common.utils.longest
import com.itangcent.common.utils.shortest

/**
 * This enumeration defines strategies to resolve multiple URLs to a single URL,
 * based on different criteria like the first URL, last URL, longest URL, or shortest URL.
 * It also provides a strategy to return all URLs as-is without any resolution.
 *
 * 此枚举定义了多 URL 解析为单个 URL 的策略，
 * 解析标准包括第一个 URL、最后一个 URL、最长的 URL 或最短的 URL。
 * 另外，它还提供了一种策略，即返回所有 URL 而不进行任何解析。
 */
enum class ResolveMultiPath {

    /**
     * Selects the first URL from the list.
     *
     * 选择列表中的第一个 URL。
     */
    FIRST {
        override fun resolve(url: URL): URL = URL.of(url.urls().firstOrNull())
    },

    /**
     * Selects the last URL from the list.
     *
     * 选择列表中的最后一个 URL。
     */
    LAST {
        override fun resolve(url: URL): URL = URL.of(url.urls().lastOrNull())
    },

    /**
     * Selects the longest URL from the list.
     *
     * 选择列表中最长的 URL。
     */
    LONGEST {
        override fun resolve(url: URL): URL = URL.of(url.urls().longest())
    },

    /**
     * Selects the shortest URL from the list.
     *
     * 选择列表中最短的 URL。
     */
    SHORTEST {
        override fun resolve(url: URL): URL = URL.of(url.urls().shortest())
    },

    /**
     * Returns all URLs without any modification.
     *
     * 返回所有 URL，不做任何修改。
     */
    ALL {
        override fun resolve(url: URL): URL = url
    };

    /**
     * Resolves the given URL based on the implemented strategy.
     *
     * 根据具体的解析策略解析给定的 URL。
     *
     * @param url The input URL containing multiple possible paths.
     *            包含多个可能路径的输入 URL。
     * @return A single resolved URL based on the chosen strategy.
     *         根据所选策略解析后的单个 URL。
     */
    abstract fun resolve(url: URL): URL
}