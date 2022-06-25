package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class BeanIsOnCreationException extends Exception {
    final BeanDefinition<?> definition;

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
