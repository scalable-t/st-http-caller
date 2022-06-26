package org.st.shc.framework.bean;

/**
 * 内部未知异常，抛出这个多半是代码 bug
 *
 * @author abomb4 2022-06-25
 */
public class InternalBeanFactoryException extends RuntimeException {

    /**
     * 构造一个 内部未知异常
     *
     * @param message 异常消息
     */
    public InternalBeanFactoryException(String message) {
        super(message);
    }
}
