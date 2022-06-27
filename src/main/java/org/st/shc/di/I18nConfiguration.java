package org.st.shc.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.bean.BeanDefinition;
import org.st.shc.framework.bean.helper.BeanDefinitionsProvider;
import org.st.shc.framework.i18n.DefaultI18nImpl;

import java.util.Collection;
import java.util.List;

/**
 * 声明 I18n 创建方式
 *
 * @author abomb4 2022-06-28
 */
public class I18nConfiguration implements BeanDefinitionsProvider {

    /** 彬名称 */
    public static final String BEAN_NAME_I18N = "i18n";

    @Override
    public Collection<BeanDefinition<?>> getDefinitions() {
        return List.of(
                i18n()
        );
    }

    /**
     * 创建 DefaultI18nImpl Bean 定义
     *
     * @return Bean 定义
     */
    public BeanDefinition<DefaultI18nImpl> i18n() {

        return BeanDefinition.<DefaultI18nImpl>builder()
                .setType(DefaultI18nImpl.class)
                .setName(BEAN_NAME_I18N)
                .setCreator(params -> new DefaultI18nImpl())
                .build();
    }
}
