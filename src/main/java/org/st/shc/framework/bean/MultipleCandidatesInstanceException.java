package org.st.shc.framework.bean;

/**
 * 获取指定类型的 bean 时在 BeanFactory 中存在多个同类实例
 *
 * @author abomb4 2022-06-25
 */
public class MultipleCandidatesInstanceException extends Exception {
    /** 类型 */
    final Class<?> beanType;

    /**
     * 获取指定类型的 bean 时在 BeanFactory 中存在多个同类实例
     *
     * @param beanType bean 类型
     * @param message 异常消息
     */
    public MultipleCandidatesInstanceException(Class<?> beanType, String message) {
        super(message);
        this.beanType = beanType;
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
}
