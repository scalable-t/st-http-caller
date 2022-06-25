package org.st.shc.framework.bean;

import java.util.Set;

/**
 * @author abomb4 2022-06-25
 */
public class MultipleCandidatesException extends Exception {
    final Class<?> beanType;
    final Set<BeanDefinition<?>> definitions;

    public MultipleCandidatesException(Class<?> beanType, Set<BeanDefinition<?>> definitions, String message) {
        super(message);
        this.beanType = beanType;
        this.definitions = definitions;
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

    /**
     * Get definitions
     *
     * @return definitions
     * @see #definitions
     */
    public Set<BeanDefinition<?>> getDefinitions() {
        return definitions;
    }
}
