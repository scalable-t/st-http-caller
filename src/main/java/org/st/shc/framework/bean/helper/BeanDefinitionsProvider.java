package org.st.shc.framework.bean.helper;

import org.st.shc.framework.bean.BeanDefinition;

import java.util.Collection;

/**
 * 包含一堆 bean 定义的东西
 *
 * @author abomb4 2022-06-25
 */
public interface BeanDefinitionsProvider {

    /**
     * 获取定义们
     *
     * @return 定义
     */
    Collection<BeanDefinition<?>> getDefinitions();
}
