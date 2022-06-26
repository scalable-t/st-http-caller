package org.st.shc.framework.bean;

/**
 * bean 创建异常，即调用 {@link BeanDefinition#creator()} 异常
 *
 * @author abomb4 2022-06-25
 */
public class BeanCreationException extends Exception {
    /** 定义 */
    private final BeanDefinition<?> definition;

    /**
     * 构建异常
     *
     * @param definition 定义
     * @param message    异常信息
     * @param e          cause
     */
    public BeanCreationException(BeanDefinition<?> definition, String message, Exception e) {
        super(message, e);
        this.definition = definition;
    }

    /**
     * Get definition
     *
     * @return definition
     * @see #definition
     */
    public BeanDefinition<?> getName() {
        return definition;
    }
}
