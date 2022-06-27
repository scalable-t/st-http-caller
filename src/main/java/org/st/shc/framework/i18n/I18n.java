package org.st.shc.framework.i18n;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.util.Locale;

/**
 * I18n interface
 *
 * @author abomb4 2022-06-27
 */
public interface I18n {

    /**
     * Resolve key to i18n value
     *
     * @param key key
     * @return value
     */
    StringProperty r(String key);

    /**
     * 获取当前语言
     *
     * @return 当前语言
     */
    Locale getLocale();

    /**
     * 绑定 MenuItem 的文字
     *
     * @param element menuItem
     * @param key     key
     */
    default void bind(MenuItem element, String key) {
        element.textProperty().bind(this.r(key));
    }

    /**
     * 绑定 Label 的文字
     *
     * @param element label
     * @param key     key
     */
    default void bind(Label element, String key) {
        element.textProperty().bind(this.r(key));
    }

    /**
     * 绑定 Button 的文字
     *
     * @param element button
     * @param key     key
     */
    default void bind(Button element, String key) {
        element.textProperty().bind(this.r(key));
    }
}
