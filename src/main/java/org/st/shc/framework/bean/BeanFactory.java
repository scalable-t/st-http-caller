package org.st.shc.framework.bean;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.function.ExceptionalConsumer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * 容器
 *
 * @author abomb4 2022-06-25
 */
public class BeanFactory implements AutoCloseable {

    /** Slf4J */
    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    /** 自己的名字 */
    public static final String DEFAULT_BEAN_FACTORY_NAME = "beanFactory";

    /** key 为类型，value 为定义集合 */
    private final Map<Class<?>, Set<BeanDefinition<?>>> definitionTypeMap = new LinkedHashMap<>();

    /** key 为名称，value 为定义 */
    private final Map<String, BeanDefinition<?>> definitionNameMap = new LinkedHashMap<>();

    /** key 为名称，value 为实例 */
    private final Map<String, Object> instanceMap = new LinkedHashMap<>();

    /** key 为类型，value 为实例集合 */
    private final Map<Class<?>, Set<Object>> instanceTypeMap = new LinkedHashMap<>();


    // region ----------- in creation -----------------
    /** 正在创建的放在这里，检查循环依赖 */
    private final Set<BeanDefinition<?>> tmpOnCreation = new HashSet<>();

    /** 需要进行后置初始化的队列 */
    private final Deque<BeanDefinition<?>> tmpPostHooks = new LinkedList<>();
    // endregion ----------- in creation -----------------

    /** 已经初始化后，getBean 会直接进行初始化 */
    boolean initialized = false;

    @SneakyThrows
    public BeanFactory() {
        this.addBeanDefinition(BeanDefinition.<BeanFactory>builder()
                .setName(DEFAULT_BEAN_FACTORY_NAME)
                .setCreator(p -> this)
                .setType(BeanFactory.class)
                .build());
        this.getBean(DEFAULT_BEAN_FACTORY_NAME);
    }

    /**
     * 新增定义
     *
     * @param definition 定义
     * @throws BeanDefinitionAlreadyExistedException 已存在
     */
    public synchronized void addBeanDefinition(@Nonnull BeanDefinition<?> definition)
            throws BeanDefinitionAlreadyExistedException {
        Objects.requireNonNull(definition, "definition cannot be null");
        Class<?> type = definition.type();
        String name = definition.name();
        BeanDefinition<?> put = this.definitionNameMap.put(name, definition);
        if (put != null) {
            log.error("Bean {} already defined, overriding are not supported. old definition: {}, new definition: {}",
                    name, put, definition);
            throw new BeanDefinitionAlreadyExistedException(name, put, definition,
                    "Bean '" + name + "' already defined");
        }

        // 为每个父类都塞入 map
        internalPushTypeDefinition(type, definition);
    }

    /**
     * 初始化
     *
     * @throws Exception 任何异常
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized void instantEveryBeans() throws Exception {
        log.info("Instant every beans");
        for (String name : this.definitionNameMap.keySet()) {
            this.getBean(name);
        }
        BeanDefinition<?> definition;

        while ((definition = tmpPostHooks.poll()) != null) {
            String name = definition.name();
            Object instance = this.instanceMap.get(name);
            if (instance == null) {
                throw new InternalBeanFactoryException("Cannot found bean " + name + " instance");
            }
            this.internalInitializeBean(definition, instance);
            try {
                ExceptionalConsumer initializer = definition.initializer();
                initializer.accept(instance);
            } catch (Exception e) {
                log.error("Initialize bean {} cause exception", instance);
                throw new BeanInitializationException(definition, instance,
                        "Initialize bean " + name + " cause exception", e);
            }
        }
        initialized = true;
        log.info("Instant every beans finished.");
    }

    /**
     * 根据类型获取彬
     *
     * @param beanType 彬类型
     * @param <T>      类型
     * @return 彬实例
     * @throws NoSuchTypedBeanException            没有这个类型的 bena
     * @throws BeanCreationException               bean 创建异常
     * @throws MultipleCandidatesException         有多个同类型 bean 定义
     * @throws BeanIsOnCreationException           bean 正在创建中，存在循环依赖
     * @throws MultipleCandidatesInstanceException 有多个同类 bean 实例
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T getBean(Class<T> beanType)
            throws NoSuchTypedBeanException, BeanCreationException, MultipleCandidatesException,
            BeanIsOnCreationException, MultipleCandidatesInstanceException {
        Objects.requireNonNull(beanType, "beanType cannot be null");
        Set<Object> objects = this.instanceTypeMap.get(beanType);
        if (objects != null) {
            int size = objects.size();
            if (size > 1) {
                throw new MultipleCandidatesInstanceException(beanType,
                        "Required type " + beanType + " has more than 1 candidates.");
            }
            if (size == 1) {
                return (T) objects.iterator().next();
            }
        }

        // do init
        Set<BeanDefinition<?>> definitions = this.definitionTypeMap.getOrDefault(beanType, Collections.emptySet());
        if (definitions.isEmpty()) {
            throw new NoSuchTypedBeanException(beanType, "No bean definition with type " + beanType);
        }
        if (definitions.size() > 1) {
            // 暂不支持多个同类 bean 中选取的功能
            throw new MultipleCandidatesException(beanType, definitions,
                    "Required type " + beanType + " has more than 1 candidates.");
        }
        BeanDefinition<?> definition = definitions.iterator().next();
        if (this.tmpOnCreation.contains(definition)) {
            throw new BeanIsOnCreationException(definition, "Bean with type " + beanType + " is currently on creation");
        }
        this.tmpOnCreation.add(definition);
        try {
            return internalCreateBeanInstance(definition, this.initialized);
        } catch (Exception e) {
            throw new BeanCreationException(definition,
                    "Create bean with name " + definition.name() + " cause exception", e);
        } finally {
            this.tmpOnCreation.remove(definition);
        }
    }

    /**
     * 根据 name 获取彬
     *
     * @param name 名称
     * @param <T>  类型
     * @return 彬实例
     * @throws NoSuchNamedBeanException  没有这个名字的 Bean
     * @throws BeanCreationException     Bean 创建异常
     * @throws BeanIsOnCreationException Bean 正在创建中，存在循环依赖
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T getBean(String name)
            throws NoSuchNamedBeanException, BeanCreationException, BeanIsOnCreationException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        Object o = this.instanceMap.get(name);
        if (o != null) {
            return (T) o;
        }

        BeanDefinition<?> definition = this.definitionNameMap.get(name);
        if (definition == null) {
            throw new NoSuchNamedBeanException(name, "No bean definition with name " + name);
        }
        if (this.tmpOnCreation.contains(definition)) {
            throw new BeanIsOnCreationException(definition, "Bean with name " + name + " is currently on creation");
        }
        this.tmpOnCreation.add(definition);

        try {
            return internalCreateBeanInstance(definition, this.initialized);
        } catch (Exception e) {
            throw new BeanCreationException(definition,
                    "Create bean with name " + definition.name() + " cause exception", e);
        } finally {
            this.tmpOnCreation.remove(definition);
        }
    }

    /**
     * 创建 bean 实例
     *
     * @param definition   定义
     * @param doInitialize 是否执行初始化步骤
     * @param <T>          类型
     * @return bean 实例
     * @throws RequiredNamedBeanException  寻找依赖 name 的 bean 异常
     * @throws RequiredTypedBeanException  寻找依赖 type 的 bean 异常
     * @throws BeanInitializationException 初始化异常
     */
    @SuppressWarnings("unchecked")
    private <T> T internalCreateBeanInstance(BeanDefinition<?> definition, boolean doInitialize)
            throws RequiredNamedBeanException, RequiredTypedBeanException, BeanInitializationException {
        DefaultBeanInitializeParamsImpl params = new DefaultBeanInitializeParamsImpl();
        List<BeanDefinition.BeanRequirement<?>> requirements = definition.requirements();
        for (BeanDefinition.BeanRequirement<?> requirement : requirements) {
            String requirementName = requirement.getName();
            Object instance;
            if (requirementName != null) {
                try {
                    instance = this.getBean(requirementName);
                } catch (NoSuchNamedBeanException e) {
                    if (requirement.isRequired()) {
                        throw new RequiredNamedBeanException(requirementName,
                                "Required bean with name " + requirementName + " is not exists", e);
                    }
                    continue;
                } catch (Exception e) {
                    throw new RequiredNamedBeanException(requirementName,
                            "Get required bean with name " + requirementName + " cause exception", e);
                }
            } else {
                Class<?> type = Objects.requireNonNull(requirement.getType(),
                        "requirement.getType() cannot be null");
                try {
                    instance = this.getBean(type);
                } catch (NoSuchTypedBeanException e) {
                    if (requirement.isRequired()) {
                        throw new RequiredTypedBeanException(type,
                                "Required bean with type " + type + " is not exists", e);
                    }
                    continue;
                } catch (Exception e) {
                    throw new RequiredTypedBeanException(type,
                            "Get required bean with type " + type + " cause exception", e);
                }
            }
            params.addInstance(requirement, instance);
        }

        Function<BeanDefinition.BeanInitializeParams, ?> creator =
                Objects.requireNonNull(definition.creator(), "definition.creator() cannot be null");
        Object beanInstance = creator.apply(params);
        if (doInitialize) {
            internalInitializeBean(definition, beanInstance);
        }

        this.internalPushTypeInstance(beanInstance.getClass(), beanInstance);
        this.instanceMap.put(definition.name(), beanInstance);
        return (T) beanInstance;
    }

    /**
     * 某个类型的所有父类及接口都视为有此定义
     *
     * @param type       类型
     * @param definition 定义
     */
    private void internalPushTypeDefinition(Class<?> type, BeanDefinition<?> definition) {

        Class<?> currentType = type;
        while (currentType != null && currentType != Object.class &&
                !currentType.getPackageName().startsWith("java.lang")) {
            Set<BeanDefinition<?>> list =
                    this.definitionTypeMap.computeIfAbsent(currentType, t -> new LinkedHashSet<>(32));
            list.add(definition);
            for (Class<?> anInterface : currentType.getInterfaces()) {
                this.internalPushTypeDefinition(anInterface, definition);
            }
            currentType = currentType.getSuperclass();
        }
    }

    /**
     * 某个类型的所有父类及接口都视为有实例
     *
     * @param type     类型
     * @param instance 实例
     */
    private void internalPushTypeInstance(Class<?> type, Object instance) {
        Class<?> currentType = type;
        while (currentType != null && currentType != Object.class &&
                !currentType.getPackageName().startsWith("java.lang")) {
            Set<Object> list = this.instanceTypeMap.computeIfAbsent(currentType, t -> new LinkedHashSet<>(32));
            list.add(instance);
            for (Class<?> anInterface : currentType.getInterfaces()) {
                this.internalPushTypeInstance(anInterface, instance);
            }
            currentType = currentType.getSuperclass();
        }
    }

    /**
     * 执行初始化方法
     *
     * @param definition 定义
     * @param instance   实例
     * @throws BeanInitializationException 初始化异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void internalInitializeBean(BeanDefinition<?> definition, Object instance)
            throws BeanInitializationException {
        ExceptionalConsumer initializer = definition.initializer();
        try {
            initializer.accept(instance);
        } catch (Exception e) {
            throw new BeanInitializationException(definition, instance,
                    "Initialize bean with name" + definition.name() + " cause exception", e);
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public synchronized void close() throws Exception {
        log.info("Close bean factory");
        for (Map.Entry<String, Object> entry : this.instanceMap.entrySet()) {
            String name = entry.getKey();
            Object instance = entry.getValue();
            BeanDefinition<?> definition = this.definitionNameMap.get(name);
            Class<?> type = definition.type();
            try {
                ExceptionalConsumer destoryer = definition.destoryer();
                destoryer.accept(instance);
            } catch (Exception e) {
                log.warn("Bean name {} with type {} destory causes exception, may cause memory leak",
                        name, type);
            }
        }
        log.info("Bean factory closed");
    }

    /** Get requirements by this factory */
    @SuppressWarnings("unchecked")
    private static class DefaultBeanInitializeParamsImpl implements BeanDefinition.BeanInitializeParams {

        final Map<BeanDefinition.BeanRequirement<?>, Object> map = new HashMap<>(4);

        private void addInstance(BeanDefinition.BeanRequirement<?> requirement, Object instance) {
            this.map.put(requirement, instance);
        }

        @Override
        public <T> T get(BeanDefinition.BeanRequirement<T> requirement) {
            return (T) this.map.get(requirement);
        }
    }
}
