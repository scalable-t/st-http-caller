package org.st.shc.framework.bean;

/**
 * 尝试获取某个 bean 发现根本没有
 *
 * @author abomb4 2022-06-25
 */
public class NoSuchNamedBeanException extends Exception {

    /** bean name */
    private final String name;

    /**
     * 创建尝试获取某个 bean 发现根本没有的异常
     *
     * @param name 名
     */
    public NoSuchNamedBeanException(String name, String message) {
        super(message);
        this.name = name;
    }

    /**
     * Get name
     *
     * @return name
     * @see #name
     */
    public String getName() {
        return name;
    }
}
