package org.st.shc.framework.bean;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.function.ExceptionalConsumer;

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
 * @author abomb4 2022-06-25
 */
public class BeanFactory {

    /** Slf4J */
    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    public static final String DEFAULT_BEAN_FACTORY_NAME = "beanFactory";

    private final Map<Class<?>, Set<BeanDefinition<?>>> definitionTypeMap = new LinkedHashMap<>();

    private final Map<String, BeanDefinition<?>> definitionNameMap = new LinkedHashMap<>();

    private final Map<String, Object> instanceMap = new LinkedHashMap<>();

    private final Map<Class<?>, Set<Object>> instanceTypeMap = new LinkedHashMap<>();


    // region ----------- in creation -----------------
    private final Set<BeanDefinition<?>> tmpOnCreation = new HashSet<>();

    private final Deque<BeanDefinition<?>> tmpPostHooks = new LinkedList<>();

    // endregion ----------- in creation -----------------

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

    public synchronized void addBeanDefinition(BeanDefinition<?> definition)
            throws BeanDefinitionAlreadyExistedException {
        Objects.requireNonNull(definition, "definition cannot be null");
        Class<?> type = definition.getType();
        String name = definition.getName();
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized void instantEveryBeans() throws Exception {
        for (String name : this.definitionNameMap.keySet()) {
            this.getBean(name);
        }
        BeanDefinition<?> definition;

        while ((definition = tmpPostHooks.poll()) != null) {
            String name = definition.getName();
            Object instance = this.instanceMap.get(name);
            if (instance == null) {
                throw new InternalBeanFactoryException("Cannot found bean " + name + " instance");
            }
            this.internalInitializeBean(definition, instance);
            try {
                ExceptionalConsumer initializer = definition.getInitializer();
                initializer.accept(instance);
            } catch (Exception e) {
                log.error("Initialize bean {} cause exception", instance);
                throw new BeanInitializationException(definition, instance,
                        "Initialize bean " + name + " cause exception", e);
            }
        }
        initialized = true;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getBean(Class<T> beanType)
            throws NoSuchTypedBeanException, BeanCreationException, MultipleCandidatesException,
            BeanIsOnCreationException, MultipleCandidatesTypeException {
        Objects.requireNonNull(beanType, "beanType cannot be null");
        Set<Object> objects = this.instanceTypeMap.get(beanType);
        if (objects != null) {
            int size = objects.size();
            if (size > 1) {
                throw new MultipleCandidatesTypeException(beanType,
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
            return internalCreateBeanInstant(definition, this.initialized);
        } catch (Exception e) {
            throw new BeanCreationException(definition,
                    "Create bean with name " + definition.getName() + " cause exception", e);
        } finally {
            this.tmpOnCreation.remove(definition);
        }
    }

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
            return internalCreateBeanInstant(definition, this.initialized);
        } catch (Exception e) {
            throw new BeanCreationException(definition,
                    "Create bean with name " + definition.getName() + " cause exception", e);
        } finally {
            this.tmpOnCreation.remove(definition);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T internalCreateBeanInstant(BeanDefinition<?> definition, boolean doInitialize)
            throws RequiredNamedBeanException, RequiredTypedBeanException, BeanInitializationException {
        DefaultBeanInitializeParamsImpl params = new DefaultBeanInitializeParamsImpl();
        List<BeanDefinition.BeanRequirement<?>> requirements = definition.getRequirements();
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
                Objects.requireNonNull(definition.getCreator(), "definition.getCreator() cannot be null");
        Object beanInstance = creator.apply(params);
        if (doInitialize) {
            internalInitializeBean(definition, beanInstance);
        }

        this.internalPushTypeInstance(beanInstance.getClass(), beanInstance);
        this.instanceMap.put(definition.getName(), beanInstance);
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void internalInitializeBean(BeanDefinition<?> definition, Object instance)
            throws BeanInitializationException {
        ExceptionalConsumer initializer = definition.getInitializer();
        try {
            initializer.accept(instance);
        } catch (Exception e) {
            throw new BeanInitializationException(definition, instance,
                    "Initialize bean with name" + definition.getName() + " cause exception", e);
        }
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
