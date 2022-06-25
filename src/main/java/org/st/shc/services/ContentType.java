package org.st.shc.services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 内容类型
 *
 * @param mimeType   媒体格式，如 application/json
 * @param charset    编码，可以为空
 * @param fullString 完整 string 形式的 Content-Type ，因为 record 太难用了所以只能放到构造函数了
 * @author abomb4 2022-06-26
 */
public record ContentType(
        @Nonnull String mimeType,
        @Nullable Charset charset,
        String fullString
) {

    /** HTML格式 */
    public static final String MIME_TYPE_TEXT_HTML = "text/html";
    /** 纯文本格式 */
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    /** XML格式 */
    public static final String MIME_TYPE_TEXT_XML = "text/xml";
    /** gif图片格式 */
    public static final String MIME_TYPE_IMAGE_GIF = "image/gif";
    /** jpg图片格式 */
    public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
    /** png图片格式 */
    public static final String MIME_TYPE_IMAGE_PNG = "image/png";
    /** XML数据格式 */
    public static final String MIME_TYPE_APPLICATION_XML = "application/xml";
    /** JSON数据格式 */
    public static final String MIME_TYPE_APPLICATION_JSON = "application/json";
    /** pdf格式 */
    public static final String MIME_TYPE_APPLICATION_PDF = "application/pdf";
    /** Word文档格式 */
    public static final String MIME_TYPE_APPLICATION_MSWORD2003 = "application/msword";
    /** Word文档格式 */
    public static final String MIME_TYPE_APPLICATION_MSWORD2007 =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    /** Excel文档格式 */
    public static final String MIME_TYPE_APPLICATION_MSEXCEL2003 = "application/vnd.ms-excel";
    /** Excel文档格式 */
    public static final String MIME_TYPE_APPLICATION_MSEXCEL2007 =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /** PPT文档格式 */
    public static final String MIME_TYPE_APPLICATION_MSPPT2003 = "application/vnd.ms-powerpoint";
    /** PPT文档格式 */
    public static final String MIME_TYPE_APPLICATION_MSPPT2007 =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    /** 二进制流数据（如常见的文件下载） */
    public static final String MIME_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
    /** 常见 form */
    public static final String MIME_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    /** 可以携带文件的 form */
    public static final String MIME_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

    /** 常用 UTF-8 json */
    public static final ContentType CONTENT_TYPE_JSON_UTF8 =
            new ContentType(MIME_TYPE_APPLICATION_JSON, StandardCharsets.UTF_8);
    /** 常用 GBK json */
    public static final ContentType CONTENT_TYPE_JSON_GBK =
            new ContentType(MIME_TYPE_APPLICATION_JSON, Charset.forName("GBK"));

    /** 通用二进制 */
    public static final ContentType CONTENT_TYPE_OCTET_STREAM = new ContentType(MIME_TYPE_APPLICATION_OCTET_STREAM);
    /** 图片 gif */
    public static final ContentType CONTENT_TYPE_IMAGE_GIF = new ContentType(MIME_TYPE_IMAGE_GIF);
    /** 图片 png */
    public static final ContentType CONTENT_TYPE_IMAGE_PNG = new ContentType(MIME_TYPE_IMAGE_PNG);
    /** 图片 jpeg */
    public static final ContentType CONTENT_TYPE_IMAGE_JPEG = new ContentType(MIME_TYPE_IMAGE_JPEG);
    /** PDF */
    public static final ContentType CONTENT_TYPE_PDF = new ContentType(MIME_TYPE_APPLICATION_PDF);
    /** Office Word */
    public static final ContentType CONTENT_TYPE_WORD = new ContentType(MIME_TYPE_APPLICATION_MSWORD2007);
    /** Office Excel */
    public static final ContentType CONTENT_TYPE_EXCEL = new ContentType(MIME_TYPE_APPLICATION_MSEXCEL2007);
    /** Office Ppt */
    public static final ContentType CONTENT_TYPE_PPT = new ContentType(MIME_TYPE_APPLICATION_MSPPT2007);

    public ContentType {
        Objects.requireNonNull(mimeType, "mimeType cannot be null");
    }

    /**
     * 好用构造函数
     *
     * @param mimeType 媒体类型
     * @param charset  编码
     */
    public ContentType(@Nonnull String mimeType, @Nullable Charset charset) {
        this(mimeType, charset, makeFullString(mimeType, charset));
    }

    /**
     * 好用构造函数
     *
     * @param mimeType 媒体类型
     */
    public ContentType(@Nonnull String mimeType) {
        this(mimeType, null, makeFullString(mimeType, null));
    }

    /**
     * 拼装完整 Content-Type
     *
     * @param mimeType 媒体类型
     * @param charset  编码
     * @return 完整
     */
    private static String makeFullString(String mimeType, Charset charset) {
        return charset == null ? mimeType : mimeType + "; charset=" + charset.name();
    }
}

