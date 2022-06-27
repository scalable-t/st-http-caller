package org.st.shc.framework.i18n;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 默认 i18n 实现
 *
 * @author abomb4 2022-06-28
 */
@NotThreadSafe
public class DefaultI18nImpl implements I18nManageable {

    /** 默认路径 */
    private static final String DEFAULT_PATH = "i18n/lang";
    /** 默认语言 */
    private static final Locale DEFAULT_LOCALE = Locale.CHINA;

    /** 当前资源包 */
    private ResourceBundle bundle;
    /** 当前语言 */
    private Locale locale;

    /** 所有 property */
    private final Map<String, SimpleStringProperty> properties;

    /**
     * 默认构建，使用默认语言 {@link #DEFAULT_LOCALE}
     */
    public DefaultI18nImpl() {
        this(DEFAULT_PATH, DEFAULT_LOCALE);
    }

    /**
     * 指定语言来构建
     *
     * @param locale 语言信息
     */
    public DefaultI18nImpl(Locale locale) {
        this(DEFAULT_PATH, locale);
    }

    /**
     * 完整构建
     *
     * @param bundleClassPath 语言文件 Classpath 路径
     * @param locale          语言信息
     */
    public DefaultI18nImpl(String bundleClassPath, Locale locale) {
        this.bundle = ResourceBundle.getBundle(bundleClassPath, locale);
        this.locale = locale;
        Set<String> keySet = this.bundle.keySet();
        Map<String, SimpleStringProperty> hashMap = new HashMap<>(keySet.size());
        for (String key : keySet) {
            hashMap.put(key, new SimpleStringProperty(this.bundle.getString(key)));
        }
        this.properties = hashMap;
    }

    @Override
    public void reload(Locale locale) {
        this.reload(DEFAULT_PATH, locale);
    }

    /**
     * 重设为另一种语言
     *
     * @param bundleClassPath 语言文件 Classpath 路径
     * @param locale          语言
     */
    public void reload(String bundleClassPath, Locale locale) {
        ResourceBundle b = ResourceBundle.getBundle(bundleClassPath, locale);
        Platform.runLater(() -> {
            for (Map.Entry<String, SimpleStringProperty> entry : this.properties.entrySet()) {
                String key = entry.getKey();
                SimpleStringProperty property = entry.getValue();
                property.set(b.getString(key));
            }
        });
        this.bundle = b;
        this.locale = locale;
    }

    @Override
    public StringProperty r(String key) {
        return properties.get(key);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultI18nImpl that = (DefaultI18nImpl) o;
        return Objects.equals(bundle, that.bundle) && Objects.equals(locale, that.locale) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundle, locale, properties);
    }

    @Override
    public String toString() {
        return "DefaultI18nImpl{" +
                "bundle=" + bundle +
                ", locale=" + locale +
                ", properties=" + properties +
                '}';
    }
}
