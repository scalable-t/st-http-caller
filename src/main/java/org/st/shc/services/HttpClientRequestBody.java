package org.st.shc.services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.http.HttpRequest;

/**
 * http 请求体
 *
 * @author abomb4 2022-06-26
 */
public interface HttpClientRequestBody {

    /**
     * 指定 Content-Type ，允许为空。
     * <p>
     * 示例：{@code application/json; charset=UTF-8}。
     * <p>
     * 当 {@link HttpClientRequest#headers()} 中存在 'Content-Type' 头时，真正的请求头中会使用 headers 的而不是这里。
     *
     * @return Content-Type
     */
    @Nullable
    ContentType getContentType();

    /**
     * 体
     *
     * @return 体
     */
    @Nonnull
    HttpRequest.BodyPublisher asBodyPublisher();
}
