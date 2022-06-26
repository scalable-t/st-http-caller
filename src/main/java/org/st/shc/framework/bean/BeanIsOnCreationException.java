package org.st.shc.framework.bean;

/**
 * 获取某个 bean 时发现其正在创建，一般是循环依赖了
 *
 * @author abomb4 2022-06-25
 */
public class BeanIsOnCreationException extends Exception {
    /** 正在创建的 bean 定义 */
    final BeanDefinition<?> definition;

    /**
     * 构建异常
     *
     * @param definition 正在创建的 bean 的定义
     * @param message    异常消息
     */
    public BeanIsOnCreationException(BeanDefinition<?> definition, String message) {
        super(message);
        this.definition = definition;
    }

    /**
     * Get definition
     *
     * @return definition
     * @see #definition
     */
    public BeanDefinition<?> getDefinition() {
        return definition;
    }
}
