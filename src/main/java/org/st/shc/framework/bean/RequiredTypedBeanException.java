package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class RequiredTypedBeanException extends Exception {
    private final Class<?> type;

    public RequiredTypedBeanException(Class<?> type, String message, Exception e) {
        super(message, e);
        this.type = type;
    }
}
