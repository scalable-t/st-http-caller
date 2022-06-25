package org.st.shc.services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * 直接用 string 当作体
 *
 * @author abomb4 2022-06-26
 */
public class BodyStringImpl implements HttpClientRequestBody {

    /** 默认编码 */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /** 字符串 */
    @Nonnull
    private final String str;
    /** 传出编码 */
    @Nonnull
    private final Charset charset;
    /** Content-Type */
    @Nullable
    private final ContentType contentType;

    /**
     * 完整构造
     *
     * @param str         字符串
     * @param charset     编码
     * @param contentType 媒体类型
     */
    public BodyStringImpl(@Nonnull String str, @Nonnull Charset charset, @Nullable ContentType contentType) {
        this.str = Objects.requireNonNull(str, "str cannot be null");
        this.charset = Objects.requireNonNull(charset, "charset cannot be null");
        this.contentType = contentType;
        if (contentType != null && contentType.charset() != null && !contentType.charset().equals(charset)) {
            throw new IllegalArgumentException("Charset by constructor " +
                    charset + " not equals to charset by ContentType " + contentType.charset());
        }
    }

    /**
     * 无 Content-Type 构造
     *
     * @param str     字符串
     * @param charset 编码
     */
    public BodyStringImpl(@Nonnull String str, @Nonnull Charset charset) {
        this(str, charset, null);
    }

    /**
     * 利用媒体类型的构造
     *
     * @param str         字符串
     * @param contentType 媒体类型
     */
    public BodyStringImpl(@Nonnull String str, @Nonnull ContentType contentType) {
        this(str, Optional.of(contentType).map(ContentType::charset).orElse(DEFAULT_CHARSET), contentType);
    }

    @Nullable
    @Override
    public ContentType getContentType() {
        return this.contentType;
    }

    @Nonnull
    @Override
    public HttpRequest.BodyPublisher asBodyPublisher() {
        return HttpRequest.BodyPublishers.ofString(this.str, this.charset);
    }

    @Override
    public String toString() {
        return "BodyStringImpl{" +
                "str='" + str + '\'' +
                ", charset=" + charset +
                ", contentType=" + contentType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BodyStringImpl that = (BodyStringImpl) o;
        return str.equals(that.str) && charset.equals(that.charset) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(str, charset, contentType);
    }
}
