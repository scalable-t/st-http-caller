package org.st.shc.framework.bean;

/**
 * 某个 bean 需要另一个类型的 bean 时发现那个类型根本无
 *
 * @author abomb4 2022-06-25
 */
public class RequiredTypedBeanException extends Exception {

    /** 缺少的类型 */
    private final Class<?> type;

    /**
     *
     * 某个 bean 需要另一个类型的 bean 时发现那个类型根本无
     *
     * @param type 缺少的类型
     * @param message exception message
     * @param e cause
     */
    public RequiredTypedBeanException(Class<?> type, String message, Exception e) {
        super(message, e);
        this.type = type;
    }

    /**
     * Get type
     *
     * @return type
     * @see #type
     */
    public Class<?> getType() {
        return type;
    }
}
