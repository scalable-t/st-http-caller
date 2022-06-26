package org.st.shc.services;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.concurrent.ThreadFactoryWithThreadId;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 客户端服务
 *
 * @author abomb4 2022-06-25
 */
public class HttpClientService implements AutoCloseable {

    /** Slf4J */
    private static final Logger log = LoggerFactory.getLogger(HttpClientService.class);

    /** 特殊的头 */
    public static final String CONTENT_TYPE = "Content-Type";

    /** 线程池 */
    private final ExecutorService executor;

    /** 线程池是不是自己造的，自己造的要自己 close */
    private final boolean selfCreatedExecutor;

    /**
     * 默认构造，使用默认创建的线程池，会跟随 HttpClientService 销毁
     */
    public HttpClientService() {
        this.executor = ExecutorsBuilder.newBuilder()
                .setCorePoolSize(32)
                .setKeepAliveTime(5)
                .setKeepAliveTimeUnit(TimeUnit.MINUTES)
                .setThreadFactory(new ThreadFactoryWithThreadId(tid -> "http-client-" + tid) {
                })
                .build();
        this.selfCreatedExecutor = true;
    }

    /**
     * 带池子构造，不负责线程池的销毁
     *
     * @param executor 线程池
     */
    public HttpClientService(ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor, "executor cannot be null");
        this.selfCreatedExecutor = false;
    }

    @SneakyThrows
    public CompletableFuture<HttpResponse<String>> httpCall(HttpClientRequest request) {
        HttpClient client = HttpClient.newBuilder()
                .executor(this.executor)
                .build();

        HttpRequest.Builder builder = HttpRequest.newBuilder();
        final HttpClientRequestBody body = request.body();
        ContentType contentType = body.getContentType();
        if (contentType != null) {
            builder.setHeader(CONTENT_TYPE, contentType.fullString());
        }
        for (Map.Entry<String, List<String>> entry : request.headers().entrySet()) {
            String key = entry.getKey();
            if (CONTENT_TYPE.equals(key)) {
                builder.setHeader(CONTENT_TYPE, entry.getValue().iterator().next());
            } else {
                for (String s : entry.getValue()) {
                    builder.header(key, s);
                }
            }
        }

        final URI uri;
        try {
            String url = request.url();
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            uri = new URI(url);
        } catch (URISyntaxException e) {
            CompletableFuture<HttpResponse<String>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        return switch (request.method()) {
            case GET -> client.sendAsync(builder
                    .GET()
                    .uri(uri)
                    .build(), HttpResponse.BodyHandlers.ofString());
            case POST -> client.sendAsync(builder
                    .POST(body.asBodyPublisher())
                    .uri(uri)
                    .build(), HttpResponse.BodyHandlers.ofString());
            case PUT -> client.sendAsync(builder
                    .PUT((body.asBodyPublisher()))
                    .uri(uri)
                    .build(), HttpResponse.BodyHandlers.ofString());
            case DELETE -> client.sendAsync(builder
                    .DELETE()
                    .uri(uri)
                    .build(), HttpResponse.BodyHandlers.ofString());
        };
    }

    @Override
    public void close() throws Exception {
        if (this.selfCreatedExecutor && !this.executor.isShutdown()) {
            this.executor.shutdown();
            if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Cannot shutdown in 10s");
            }
        }
    }
}
