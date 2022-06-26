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
 * bean 定义
 *
 * @param name         bean 名称
 * @param type         bean 类型
 * @param requirements bean 构造所需依赖
 * @param creator      构造函数
 * @param initializer  初始化方法
 * @param destoryer    销毁方法
 * @author abomb4 2022-06-25
 */
public record BeanDefinition<T>(
        String name,
        Class<T> type,
        List<BeanRequirement<?>> requirements,
        Function<BeanInitializeParams, T> creator,
        ExceptionalConsumer<T> initializer,
        ExceptionalConsumer<T> destoryer
) {

    /**
     * 创建一个构造器，调用方法如
     * <pre>BeanDefinition.&lt;ThreadPoolExecutor&gt;builder()</pre>
     *
     * @param <T> 类型
     * @return 构造器
     */
    public static <T> BeanDefinitionBuilder<T> builder() {
        return new BeanDefinitionBuilder<>();
    }

    /**
     * bean 定义
     *
     * @param name         bean 名称
     * @param type         bean 类型
     * @param requirements bean 构造所需依赖
     * @param creator      构造函数
     * @param initializer  初始化方法
     * @param destoryer    销毁方法
     */
    public BeanDefinition(String name, Class<T> type, List<BeanRequirement<?>> requirements,
                          Function<BeanInitializeParams, T> creator,
                          ExceptionalConsumer<T> initializer,
                          ExceptionalConsumer<T> destoryer) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.requirements = List.copyOf(Objects.requireNonNull(requirements, "requirements cannot be null"));
        this.creator = Objects.requireNonNull(creator, "creator cannot be null");
        this.initializer = Objects.requireNonNull(initializer, "initializer cannot be null");
        this.destoryer = Objects.requireNonNull(destoryer, "destoryer cannot be null");
    }

    /**
     * bean 依赖
     *
     * @param <T> 依赖类型
     */
    public interface BeanRequirement<T> {

        /**
         * 声明一个依赖
         *
         * @param type 类型，不能为空
         * @param <T>  类型
         * @return 依赖定义
         */
        static <T> BeanRequirement<T> requireObject(Class<T> type) {
            return new ObjectRequirement<>(null, type, true);
        }

        /**
         * 声明一个依赖
         *
         * @param type 类型，不能为空
         * @param name 名称
         * @param <T>  类型
         * @return 依赖定义
         */
        static <T> BeanRequirement<T> requireObject(Class<T> type, String name) {
            return new ObjectRequirement<>(name, type, true);
        }

        /**
         * 声明一个依赖
         *
         * @param type     类型，不能为空
         * @param required 是否必须
         * @param <T>      类型
         * @return 依赖定义
         */
        static <T> BeanRequirement<T> requireObject(Class<T> type, boolean required) {
            return new ObjectRequirement<>(null, type, required);
        }

        /**
         * 声明一个依赖
         *
         * @param type     类型，不能为空
         * @param name     名称
         * @param required 是否必须
         * @param <T>      类型
         * @return 依赖定义
         */
        static <T> BeanRequirement<T> requireObject(Class<T> type, String name, boolean required) {
            return new ObjectRequirement<>(name, type, required);
        }

        /**
         * 声明一个列表依赖
         *
         * @param itemType 元素类型，不能为空
         * @param <T>      类型
         * @return 依赖定义
         */
        static <T> BeanRequirement<List<T>> requireList(Class<T> itemType) {
            return requireList(itemType, true);
        }

        /**
         * 声明一个列表依赖
         *
         * @param itemType 元素类型，不能为空
         * @param required 是否必须
         * @param <T>      类型
         * @return 依赖定义
         */
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

        /**
         * 依赖的 bean 类型，不能为空
         *
         * @return 类型
         */
        @Nonnull
        Class<?> getType();

        /**
         * 是否必须
         *
         * @return 是否必须
         */
        boolean isRequired();
    }

    /** 对象依赖 */
    @Value
    public static class ObjectRequirement<T> implements BeanRequirement<T> {
        /** 依赖的 bean 名称，允许为空 */
        String name;
        /** 依赖的 bean 类型，不能为空 */
        Class<T> type;
        /** 是否必须 */
        boolean required;

        /**
         * 声明一个依赖
         *
         * @param type     类型，不能为空
         * @param name     名称
         * @param required 是否必须
         */
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

    /** 列表依赖 */
    @Value
    public static class ListRequirement<T> implements BeanRequirement<List<T>> {
        /** 依赖的 bean 类型，不能为空 */
        Class<T> itemType;
        /** 是否必须 */
        boolean required;

        /**
         * 声明一个列表依赖
         *
         * @param itemType 元素类型，不能为空
         * @param required 是否必须
         */
        public ListRequirement(Class<T> itemType, boolean required) {
            this.itemType = Objects.requireNonNull(itemType, "type cannot be null");
            if (Collection.class.isAssignableFrom(itemType)) {
                throw new IllegalArgumentException(
                        "Collection in Collection is not supported");
            }
            this.required = required;
        }

        @Override
        public String getName() {
            return null;
        }

        @Nonnull
        @Override
        public Class<?> getType() {
            return itemType;
        }
    }

    /**
     * 构造参数接口，方便根据 BeanRequirement 实例直接获取实例。
     */
    public interface BeanInitializeParams {

        /**
         * 获取实例，如果 {@code requirement.isRequired()} ，则一定能 get 出东西；否则可能为空
         *
         * @param requirement 需求
         * @param <T>         类型
         * @return 实例
         */
        <T> T get(BeanRequirement<T> requirement);
    }
}
