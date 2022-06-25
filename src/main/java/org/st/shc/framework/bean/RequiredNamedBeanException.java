package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class RequiredNamedBeanException extends Exception {
    private final String requirementName;

    public RequiredNamedBeanException(String requirementName, String message, Exception e) {
        super(message, e);
        this.requirementName = requirementName;
    }
}
