package org.st.shc.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.bean.BeanDefinition;
import org.st.shc.framework.bean.helper.BeanDefinitionsProvider;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.concurrent.ThreadFactoryWithThreadId;
import org.st.shc.services.HttpClientService;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 声明 HttpClientService 创建方式
 *
 * @author abomb4 2022-06-25
 */
public class HttpClientServiceConfiguration implements BeanDefinitionsProvider {

    /** Slf4J */
    private static final Logger log = LoggerFactory.getLogger(HttpClientServiceConfiguration.class);

    /** 彬名称 */
    public static final String BEAN_NAME_EXECUTOR_NAME = "httpClientServiceExecutor";

    @Override
    public Collection<BeanDefinition<?>> getDefinitions() {
        return List.of(
                httpClientServiceExecutor(),
                httpClientService()
        );
    }

    /**
     * 创建 ThreadPoolExecutor Bean 定义
     *
     * @return Bean 定义
     */
    public BeanDefinition<ThreadPoolExecutor> httpClientServiceExecutor() {

        return BeanDefinition.<ThreadPoolExecutor>builder()
                .setType(ThreadPoolExecutor.class)
                .setName(BEAN_NAME_EXECUTOR_NAME)
                .setCreator(params -> ExecutorsBuilder.newBuilder()
                        .setThreadFactory(new ThreadFactoryWithThreadId(tid -> "http-client-t-" + tid) {})
                        .setCorePoolSize(16)
                        .setMaximumPoolSize(32)
                        .setWorkQueue(new ArrayBlockingQueue<>(1024))
                        .setKeepAliveTime(15)
                        .setKeepAliveTimeUnit(TimeUnit.MINUTES)
                        .build())
                .setDestoryer(instance -> {
                    instance.shutdown();
                    if (!instance.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.warn("Cannot shutdown httpClientServiceExecutor gracefully in 10s");
                    }
                })
                .build();
    }

    /**
     * 创建 HttpClientService Bean 定义
     *
     * @return Bean 定义
     */
    public BeanDefinition<HttpClientService> httpClientService() {

        BeanDefinition.BeanRequirement<ExecutorService> requireExecutor =
                BeanDefinition.BeanRequirement.requireObject(ExecutorService.class, BEAN_NAME_EXECUTOR_NAME);
        return BeanDefinition.<HttpClientService>builder()
                .setType(HttpClientService.class)
                .setName("httpClientService")
                .addRequirement(requireExecutor)
                .setCreator(params -> new HttpClientService(params.get(requireExecutor)))
                .setInitializer(instance -> {
                })
                .setDestoryer(instance -> {
                })
                .build();
    }
}
