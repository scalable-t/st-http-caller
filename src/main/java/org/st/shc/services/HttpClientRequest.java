package org.st.shc.services;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP 请求
 *
 * @param method  HTTP Method
 * @param url     链接
 * @param headers 头
 * @param body    体
 */
public record HttpClientRequest(
        @Nonnull
        HttpMethod method,
        @Nonnull
        String url,
        @Nonnull
        Map<String, List<String>> headers,
        HttpClientRequestBody body
) {

    /**
     * 构造器
     *
     * @return 构造器
     */
    public static InternalBuilderInterface builder() {
        return new Builder();
    }

    /**
     * 构造 HTTP 请求
     *
     * @param method  HTTP Method
     * @param url     链接
     * @param headers 头
     * @param body    体
     */
    public HttpClientRequest(@Nonnull HttpMethod method,
                             @Nonnull String url,
                             @Nonnull Map<String, List<String>> headers,
                             HttpClientRequestBody body) {
        this.method = Objects.requireNonNull(method, "method cannot be null");
        this.url = Objects.requireNonNull(url, "url cannot be null");
        this.headers = Objects.requireNonNull(headers, "headers cannot be null");
        this.body = body == null ? BodyEmptyImpl.INSTANCE : body;
    }

    /**
     * 内部使用的构造接口，防止实现各类 body 时忘记写一些方法
     *
     * @author abomb4 2022-06-26
     */
    public interface InternalBuilderInterface {

        /**
         * 设置请求类型
         *
         * @param method the {@code method} to set
         * @return this
         */
        InternalBuilderInterface setMethod(@Nonnull HttpMethod method);

        /**
         * 设置 url
         *
         * @param url the {@code url} to set
         * @return this
         */
        InternalBuilderInterface setUrl(@Nonnull String url);

        /**
         * 增加一个头
         *
         * @param name  头名称
         * @param value 头值
         * @return this
         */
        InternalBuilderInterface addHeader(@Nonnull String name, @Nonnull String value);

        /**
         * 设置一个头
         *
         * @param name  头名称
         * @param value 头值
         * @return this
         */
        InternalBuilderInterface setHeader(@Nonnull String name, @Nonnull String value);

        /**
         * 利用纯粹的 String 作为 body
         *
         * @param str String
         * @return this
         */
        InternalBuilderInterface withStringBody(String str);

        /**
         * 构造 http 请求
         *
         * @return this
         */
        HttpClientRequest build();
    }

    /**
     * http 请求构造器
     */
    public static final class Builder implements InternalBuilderInterface {

        /** 头默认尺寸 */
        private static final int DEFAULT_HEADERS_ARRAY_SIZE = 4;

        /** HTTP Method */
        private HttpMethod method = HttpMethod.GET;
        /** 链接 */
        private String url;
        /** 头 */
        private Map<String, List<String>> headers = new LinkedHashMap<>(16);

        /** default constructor */
        public Builder() {
        }

        /**
         * clone constructor
         *
         * @param copy clone
         */
        public Builder(HttpClientRequest copy) {
            this.method = copy.method;
            this.url = copy.url;
            this.headers = copy.headers;
        }

        /**
         * 设置请求类型
         *
         * @param method the {@code method} to set
         * @return a reference to this Builder
         */
        @Override
        public Builder setMethod(@Nonnull HttpMethod method) {
            this.method = method;
            return this;
        }

        /**
         * 设置 url
         *
         * @param url the {@code url} to set
         * @return a reference to this Builder
         */
        @Override
        public Builder setUrl(@Nonnull String url) {
            this.url = url;
            return this;
        }

        /**
         * 增加一个头
         *
         * @param name  头名称
         * @param value 头值
         * @return a reference to this Builder
         */
        @Override
        public Builder addHeader(@Nonnull String name, @Nonnull String value) {
            this.headers.computeIfAbsent(name, k -> new ArrayList<>(DEFAULT_HEADERS_ARRAY_SIZE)).add(value);
            return this;
        }

        /**
         * 设置一个头
         *
         * @param name  头名称
         * @param value 头值
         * @return a reference to this Builder
         */
        @Override
        public Builder setHeader(@Nonnull String name, @Nonnull String value) {
            ArrayList<String> list = new ArrayList<>(DEFAULT_HEADERS_ARRAY_SIZE);
            list.add(value);
            this.headers.put(name, list);
            return this;
        }

        /**
         * 利用纯粹的 String 作为 body
         *
         * @param str String
         * @return a reference to this Builder
         */
        @Override
        public StringBodyBuilder withStringBody(String str) {
            return new StringBodyBuilder(str);
        }

        /**
         * 构造 http 请求
         *
         * @return a {@code HttpClientRequest} built with parameters of this {@code HttpClientRequest.Builder}
         */
        @Override
        public HttpClientRequest build() {
            return new HttpClientRequest(this.method, this.url, this.headers, null);
        }

        /**
         * 构造 http 请求
         *
         * @param body with body
         * @return a {@code HttpClientRequest} built with parameters of this {@code HttpClientRequest.Builder}
         */
        public HttpClientRequest build(HttpClientRequestBody body) {
            return new HttpClientRequest(this.method, this.url, this.headers, body);
        }

        /**
         * 利用 String 作为体的构造器
         *
         * @author abomb4 2022-06-26
         */
        private class StringBodyBuilder implements InternalBuilderInterface {

            /** 字符串 */
            private String str;
            /** 传出编码 */
            private Charset charset = BodyStringImpl.DEFAULT_CHARSET;
            /** Content-Type */
            private ContentType contentType;

            /**
             * private constructor
             *
             * @param str str
             */
            private StringBodyBuilder(String str) {
                this.str = str;
            }

            @Override
            public StringBodyBuilder setMethod(@Nonnull HttpMethod method) {
                Builder.this.setMethod(method);
                return this;
            }

            @Override
            public StringBodyBuilder setUrl(@Nonnull String url) {
                Builder.this.setUrl(url);
                return this;
            }

            @Override
            public StringBodyBuilder addHeader(@Nonnull String name, @Nonnull String value) {
                Builder.this.addHeader(name, value);
                return this;
            }

            @Override
            public StringBodyBuilder setHeader(@Nonnull String name, @Nonnull String value) {
                Builder.this.setHeader(name, value);
                return this;
            }

            @Override
            public StringBodyBuilder withStringBody(String str) {
                this.str = str;
                return this;
            }

            /**
             * 设置编码；如果设置了 ContentType ，则 ContentType 中的编码更加优先
             *
             * @param charset 编码
             * @return this
             */
            public StringBodyBuilder setCharset(Charset charset) {
                this.charset = charset;
                return this;
            }

            /**
             * 设置内容类型，若存在编码则优先使用
             *
             * @param contentType 内容类型
             * @return this
             */
            public StringBodyBuilder setContentType(ContentType contentType) {
                this.contentType = contentType;
                return this;
            }

            @Override
            public HttpClientRequest build() {
                Charset usingCharset = this.charset;
                if (this.contentType != null && this.contentType.charset() != null) {
                    usingCharset = this.contentType.charset();
                }
                BodyStringImpl body = new BodyStringImpl(str, usingCharset, this.contentType);
                return Builder.this.build(body);
            }
        }
    }
}
