package org.st.shc.framework.bean;

/**
 * 某个 bean 需要另一个名字的 bean 时发现那个名字根本无
 *
 * @author abomb4 2022-06-25
 */
public class RequiredNamedBeanException extends Exception {

    /** 被需要的名字 */
    private final String requirementName;

    /**
     * 某个 bean 需要另一个名字的 bean 时发现那个名字根本无
     *
     * @param requirementName 被需要的名字
     * @param message         exception message
     * @param e               cause
     */
    public RequiredNamedBeanException(String requirementName, String message, Exception e) {
        super(message, e);
        this.requirementName = requirementName;
    }

    /**
     * Get requirementName
     *
     * @return requirementName
     * @see #requirementName
     */
    public String getRequirementName() {
        return requirementName;
    }
}
