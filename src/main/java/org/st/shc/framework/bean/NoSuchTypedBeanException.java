package org.st.shc.framework.bean;

/**
 * 尝试获取某个 bean 发现根本没有
 *
 * @author abomb4 2022-06-25
 */
public class NoSuchTypedBeanException extends Exception {

    /** bean name */
    private final Class<?> type;

    /**
     * 创建尝试获取某个 bean 发现根本没有的异常
     *
     * @param type 名
     */
    public NoSuchTypedBeanException(Class<?> type, String message) {
        super(message);
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
