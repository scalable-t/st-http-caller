package org.st.shc.framework.i18n;

import java.util.Locale;

/**
 * I18n Management interface
 *
 * @author abomb4 2022-06-27
 */
public interface I18nManageable extends I18n {

    /**
     * 重新读取语言
     *
     * @param locale 语言
     */
    void reload(Locale locale);
}
