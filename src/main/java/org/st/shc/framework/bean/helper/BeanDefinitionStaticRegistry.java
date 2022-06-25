package org.st.shc.framework.bean.helper;

import org.st.shc.framework.bean.BeanDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author abomb4 2022-06-25
 */
public final class BeanDefinitionStaticRegistry {

    public static final BeanDefinitionStaticRegistry INSTANCE = new BeanDefinitionStaticRegistry();

    private BeanDefinitionStaticRegistry() {
    }

    private final Set<BeanDefinition<?>> definitions = new LinkedHashSet<>();

    public void addDefinition(BeanDefinition<?> definition) {
        this.definitions.add(definition);
    }
}
