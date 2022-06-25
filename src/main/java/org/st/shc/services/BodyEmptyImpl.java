package org.st.shc.services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.http.HttpRequest;

/**
 * 空的体
 *
 * @author abomb4 2022-06-26
 */
public class BodyEmptyImpl implements HttpClientRequestBody {

    /** 现成实例 */
    public static final BodyEmptyImpl INSTANCE = new BodyEmptyImpl();

    @Nullable
    @Override
    public ContentType getContentType() {
        return null;
    }

    @Nonnull
    @Override
    public HttpRequest.BodyPublisher asBodyPublisher() {
        return HttpRequest.BodyPublishers.noBody();
    }
}
