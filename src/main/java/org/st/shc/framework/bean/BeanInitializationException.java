package org.st.shc.framework.bean;

/**
 * @author abomb4 2022-06-25
 */
public class BeanInitializationException extends Exception {

    private final BeanDefinition<?> beanDefinition;
    private final Object instance;

    public BeanInitializationException(BeanDefinition<?> beanDefinition, Object instance, String message, Exception e) {
        super(message, e);
        this.beanDefinition = beanDefinition;
        this.instance = instance;
    }

    /**
     * Get instance
     *
     * @return instance
     * @see #instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Get beanDefinition
     *
     * @return beanDefinition
     * @see #beanDefinition
     */
    public BeanDefinition<?> getBeanDefinition() {
        return beanDefinition;
    }
}
