package org.st.shc.framework.bean;

/**
 * 调用 bean 的初始化方法异常（即 {@link BeanDefinition#initializer()}）
 *
 * @author abomb4 2022-06-25
 */
public class BeanInitializationException extends Exception {

    /** bean 定义 */
    private final BeanDefinition<?> beanDefinition;
    /** 仅创建，未初始化的 bean 实例 */
    private final Object instance;

    /**
     * 构建异常
     *
     * @param beanDefinition bean 定义
     * @param instance       仅创建，未初始化的 bean 实例
     * @param message        异常消息
     * @param e              cause
     */
    public BeanInitializationException(BeanDefinition<?> beanDefinition, Object instance, String message, Exception e) {
        super(message, e);
        this.beanDefinition = beanDefinition;
        this.instance = instance;
    }

    /**
     * Get instance
     *
     * @return instance
     * @see #instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Get beanDefinition
     *
     * @return beanDefinition
     * @see #beanDefinition
     */
    public BeanDefinition<?> getBeanDefinition() {
        return beanDefinition;
    }
}
