package org.st.shc.services;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.concurrent.ThreadFactoryWithThreadId;

import java.net.URI;
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

    private final ExecutorService executor;

    public HttpClientService() {
        this(ExecutorsBuilder.newBuilder()
                .setCorePoolSize(32)
                .setKeepAliveTime(5)
                .setKeepAliveTimeUnit(TimeUnit.MINUTES)
                .setThreadFactory(new ThreadFactoryWithThreadId(tid -> "http-client-" + tid) {
                })
                .build());
    }

    public HttpClientService(ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor, "executor cannot be null");
        ;
    }

    @SneakyThrows
    public CompletableFuture<HttpResponse<String>> httpCall(HttpRequestModel request) {
        HttpClient client = HttpClient.newBuilder()
                .executor(this.executor)
                .build();

        HttpRequest.Builder builder = HttpRequest.newBuilder();
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            String key = entry.getKey();
            for (String s : entry.getValue()) {
                builder.header(key, s);
            }
        }

        return switch (request.getMethod()) {
            case GET -> client.sendAsync(builder
                    .GET()
                    .uri(new URI(request.getUrl()))
                    .build(), HttpResponse.BodyHandlers.ofString());
            case POST -> client.sendAsync(builder
                    .POST(HttpRequest.BodyPublishers.ofString(request.getBody()))
                    .uri(new URI(request.getUrl()))
                    .build(), HttpResponse.BodyHandlers.ofString());
            case PUT -> client.sendAsync(builder
                    .PUT(HttpRequest.BodyPublishers.ofString(request.getBody()))
                    .uri(new URI(request.getUrl()))
                    .build(), HttpResponse.BodyHandlers.ofString());
            case DELETE -> client.sendAsync(builder
                    .DELETE()
                    .uri(new URI(request.getUrl()))
                    .build(), HttpResponse.BodyHandlers.ofString());
        };
    }

    @Override
    public void close() throws Exception {
        this.executor.shutdown();
        if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
            log.warn("Cannot shutdown in 10s");
        }
    }
}
