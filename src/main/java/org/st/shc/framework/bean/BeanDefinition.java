package org.st.shc.framework.bean;

import lombok.Value;
import org.st.shc.framework.function.ExceptionalConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author abomb4 2022-06-25
 */
@Value
public class BeanDefinition<T> {

    public static <T> BeanDefinitionBuilder<T> builder() {
        return new BeanDefinitionBuilder<>();
    }

    String name;

    Class<T> type;

    List<BeanRequirement<?>> requirements;

    Function<BeanInitializeParams, T> creator;

    ExceptionalConsumer<T> initializer;

    ExceptionalConsumer<T> destoryer;

    public BeanDefinition(String name, Class<T> type, List<BeanRequirement<?>> requirements,
                          Function<BeanInitializeParams, T> creator,
                          ExceptionalConsumer<T> initializer,
                          ExceptionalConsumer<T> destoryer) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.requirements = Objects.requireNonNull(requirements, "requirements cannot be null");
        this.creator = Objects.requireNonNull(creator, "creator cannot be null");
        this.initializer = Objects.requireNonNull(initializer, "initializer cannot be null");
        this.destoryer = Objects.requireNonNull(destoryer, "destoryer cannot be null");
    }

    public interface BeanRequirement<T> {

        static <T> BeanRequirement<T> requireObject(Class<T> type) {
            return new ObjectRequirement<>(null, type, true);
        }

        static <T> BeanRequirement<T> requireObject(Class<T> type, String name) {
            return new ObjectRequirement<>(name, type, true);
        }

        static <T> BeanRequirement<T> requireObject(Class<T> type, boolean required) {
            return new ObjectRequirement<>(null, type, required);
        }

        static <T> BeanRequirement<T> requireObject(Class<T> type, String name, boolean required) {
            return new ObjectRequirement<>(name, type, required);
        }

        static <T> BeanRequirement<List<T>> requireList(Class<T> itemType) {
            return requireList(itemType, true);
        }

        static <T> BeanRequirement<List<T>> requireList(Class<T> itemType, boolean required) {
            return new ListRequirement<>(itemType, required);
        }

        /**
         * 依赖的 bean 名称，允许为空
         *
         * @return name
         */
        @Nullable
        String getName();

        @Nonnull
        Class<?> getType();

        boolean isRequired();
    }

    @Value
    public static class ObjectRequirement<T> implements BeanRequirement<T> {
        String name;
        Class<T> type;
        boolean required;

        public ObjectRequirement(String name, Class<T> type, boolean required) {
            this.name = name;
            this.type = Objects.requireNonNull(type, "type cannot be null");
            if (Collection.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(
                        "Collection requirement is not supported, please use CollectionRequirement");
            }
            this.required = required;
        }
    }

    @Value
    public static class ListRequirement<T> implements BeanRequirement<List<T>> {
        Class<T> type;
        boolean required;

        public ListRequirement(Class<T> type, boolean required) {
            this.type = Objects.requireNonNull(type, "type cannot be null");
            if (Collection.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(
                        "Collection in Collection is not supported");
            }
            this.required = required;
        }

        @Override
        public String getName() {
            return null;
        }
    }

    public interface BeanInitializeParams {

        <T> T get(BeanRequirement<T> requirement);
    }
}
