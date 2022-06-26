package org.st.shc.framework.bean;

import org.st.shc.framework.function.ExceptionalConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 方便构造 BeanDefinition ，可以在 idea 里增加一个 Live Template：
 * <pre>
 * /**
 *  * 创建 $TYPE$ Bean 定义
 *  *
 *  * @return Bean 定义
 *  &#42;/
 * public BeanDefinition&lt;$TYPE$&gt; $TYPE_LOWER$() {
 *     return BeanDefinition.&lt;$TYPE$&gt;builder()
 *             .setType($TYPE$.class)
 *             .setName("$TYPE_LOWER$")
 *             .setCreator(params -> {
 *                 $TYPE$ instance = new $TYPE$();
 *                 return instance;
 *             })
 *             .setInitializer(instance -> {})
 *             .setDestoryer(instance -> {})
 *             .build();
 * }
 * </pre>
 *
 * @author abomb4 2022-06-25
 */
public class BeanDefinitionBuilder<T> {

    /** 空消费 */
    private static final ExceptionalConsumer<?> NOTHING_CONSUMER = v -> {
    };

    /** 彬名称 */
    private String name;

    /** 彬类型 */
    private Class<T> type;

    /** 所需依赖 */
    private final List<BeanDefinition.BeanRequirement<?>> requirements = new ArrayList<>(8);

    /** 构造函数 */
    private Function<BeanDefinition.BeanInitializeParams, T> creator;

    /** 初始化方法 */
    private ExceptionalConsumer<T> initializer;

    /** 关闭方法，存在常驻资源（文件、网络连接、线程池）时需要用这个来关闭 */
    private ExceptionalConsumer<T> destoryer;

    /**
     * 设置彬类型
     *
     * @param type 彬类型
     * @return this
     */
    public BeanDefinitionBuilder<T> setType(Class<T> type) {
        this.type = type;
        return this;
    }

    /**
     * 设置构造函数
     *
     * @param creator 构造函数
     * @return this
     */
    public BeanDefinitionBuilder<T> setCreator(
            Function<BeanDefinition.BeanInitializeParams, T> creator) {
        this.creator = creator;
        return this;
    }

    /**
     * 设置彬名称
     *
     * @param name 彬名称
     * @return this
     */
    public BeanDefinitionBuilder<T> setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置初始化方法
     *
     * @param initializer 初始化方法
     * @return this
     */
    public BeanDefinitionBuilder<T> setInitializer(ExceptionalConsumer<T> initializer) {
        this.initializer = initializer;
        return this;
    }

    /**
     * 设置关闭方法
     *
     * @param destoryer 关闭方法
     * @return this
     */
    public BeanDefinitionBuilder<T> setDestoryer(ExceptionalConsumer<T> destoryer) {
        this.destoryer = destoryer;
        return this;
    }

    /**
     * 增加一个所需依赖
     *
     * @param requirement 所需依赖
     * @return this
     */
    public <R> BeanDefinitionBuilder<T> addRequirement(BeanDefinition.BeanRequirement<R> requirement) {
        this.requirements.add(requirement);
        return this;
    }

    /**
     * 构造一个不可变的 bean 定义实例
     *
     * @return bean 定义
     */
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
