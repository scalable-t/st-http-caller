package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class MultipleCandidatesTypeException extends Exception {
    final Class<?> beanType;

    public MultipleCandidatesTypeException(Class<?> beanType, String message) {
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
