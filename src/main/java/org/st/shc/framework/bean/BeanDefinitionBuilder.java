package org.st.shc.framework.bean;

import org.st.shc.framework.function.ExceptionalConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author abomb4 2022-06-25
 */
public class BeanDefinitionBuilder<T> {

    private static final ExceptionalConsumer<?> NOTHING_CONSUMER = v -> {
    };

    private String name;

    private Class<T> type;

    private final List<BeanDefinition.BeanRequirement<?>> requirements = new ArrayList<>(8);

    private Function<BeanDefinition.BeanInitializeParams, T> creator;

    private ExceptionalConsumer<T> initializer;

    private ExceptionalConsumer<T> destoryer;

    public BeanDefinitionBuilder<T> setType(Class<T> type) {
        this.type = type;
        return this;
    }

    public BeanDefinitionBuilder<T> setCreator(
            Function<BeanDefinition.BeanInitializeParams, T> creator) {
        this.creator = creator;
        return this;
    }

    public BeanDefinitionBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }

    public BeanDefinitionBuilder<T> setInitializer(ExceptionalConsumer<T> initializer) {
        this.initializer = initializer;
        return this;
    }

    public BeanDefinitionBuilder<T> setDestoryer(ExceptionalConsumer<T> destoryer) {
        this.destoryer = destoryer;
        return this;
    }

    public <R> BeanDefinitionBuilder<T> addRequirement(BeanDefinition.BeanRequirement<R> requirement) {
        this.requirements.add(requirement);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BeanDefinition<T> build() {
        if (this.type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (this.creator == null) {
            throw new IllegalArgumentException("creator cannot be null");
        }
        String name = this.name;
        if (this.name == null || this.name.isBlank()) {
            name = this.type.getName();
        }
        ExceptionalConsumer<T> initializer = this.initializer;
        if (this.initializer == null) {
            initializer = (ExceptionalConsumer<T>) NOTHING_CONSUMER;
        }
        ExceptionalConsumer<T> destoryer = this.destoryer;
        if (this.destoryer == null) {
            destoryer = (ExceptionalConsumer<T>) NOTHING_CONSUMER;
        }
        return new BeanDefinition<>(name, type, requirements, creator, initializer, destoryer);
    }
}
