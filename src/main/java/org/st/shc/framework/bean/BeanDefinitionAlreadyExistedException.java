package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class BeanDefinitionAlreadyExistedException extends Exception {
    private final String name;
    private final BeanDefinition<?> oldDefinition;
    private final BeanDefinition<?> newDefinition;

    public BeanDefinitionAlreadyExistedException(String name,
                                                 BeanDefinition<?> oldDefinition,
                                                 BeanDefinition<?> newDefinition,
                                                 String message) {
        super(message);
        this.name = name;
        this.oldDefinition = oldDefinition;
        this.newDefinition = newDefinition;
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

    /**
     * Get oldDefinition
     *
     * @return oldDefinition
     * @see #oldDefinition
     */
    public BeanDefinition<?> getOldDefinition() {
        return oldDefinition;
    }

    /**
     * Get newDefinition
     *
     * @return newDefinition
     * @see #newDefinition
     */
    public BeanDefinition<?> getNewDefinition() {
        return newDefinition;
    }
}
