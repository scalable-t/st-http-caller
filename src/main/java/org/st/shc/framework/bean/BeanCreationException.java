package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class BeanCreationException extends Exception {
    private final BeanDefinition<?> definition;

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
