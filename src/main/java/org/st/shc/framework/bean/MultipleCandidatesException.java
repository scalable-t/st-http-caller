package org.st.shc.framework.bean;

import java.util.Set;

/**
 * 获取指定类型的 bean 时在 BeanFactory 中存在多个同类定义
 *
 * @author abomb4 2022-06-25
 */
public class MultipleCandidatesException extends Exception {

    /** bean 类型 */
    final Class<?> beanType;
    /** 定义 */
    final Set<BeanDefinition<?>> definitions;

    /**
     * 获取指定类型的 bean 时在 BeanFactory 中存在多个同类定义
     *
     * @param beanType    bean 类型
     * @param definitions 定义
     * @param message     异常消息
     */
    public MultipleCandidatesException(Class<?> beanType, Set<BeanDefinition<?>> definitions, String message) {
        super(message);
        this.beanType = beanType;
        this.definitions = definitions;
    }

    /**
     * Get beanType
     *
     * @return beanType
     * @see #beanType
     */
    public Class<?> getBeanType() {
        return beanType;
    }

    /**
     * Get definitions
     *
     * @return definitions
     * @see #definitions
     */
    public Set<BeanDefinition<?>> getDefinitions() {
        return definitions;
    }
}
