package org.st.shc.components;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.extern.slf4j.Slf4j;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.concurrent.ThreadFactoryWithThreadId;
import org.st.shc.services.HttpClientRequest;
import org.st.shc.services.HttpClientService;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author abomb4 2022-06-22
 */
@Slf4j
public class MainWindowViewModel {

    private final ScheduledThreadPoolExecutor schedule = ExecutorsBuilder.newScheduledBuilder()
            .setCorePoolSize(1)
            .setThreadFactory(new ThreadFactoryWithThreadId(tid -> "view-schedule-" + tid) {
            })
            .build();

    private final SimpleStringProperty username = new SimpleStringProperty("");

    private final SimpleStringProperty password = new SimpleStringProperty("");

    private final SimpleStringProperty url = new SimpleStringProperty("");

    private final SimpleStringProperty result = new SimpleStringProperty("");

    private final SimpleBooleanProperty submitting = new SimpleBooleanProperty(false);

    private final HttpClientService httpClientService;

    public MainWindowViewModel(HttpClientService httpClientService) {
        this.httpClientService = httpClientService;

        this.username.addListener((observable, oldValue, newValue) -> {
            log.info("oldValue: {}", oldValue);
            log.info("newValue: {}", newValue);
        });
        this.password.addListener((observable, oldValue, newValue) -> {
            log.info("pwd oldValue: {}", oldValue);
            log.info("pwd newValue: {}", newValue);
        });
        this.submitting.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                final String usernameFinal = username.get();
                final String passwordFinal = password.get();
                log.info("Do login, username: {}, password: {}", usernameFinal, passwordFinal);
                schedule.schedule(() -> Platform.runLater(() -> submitting.set(false)), 3, TimeUnit.SECONDS);
            }
        });
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public boolean isSubmitting() {
        return submitting.get();
    }

    public SimpleBooleanProperty submittingProperty() {
        return submitting;
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getResult() {
        return result.get();
    }

    public SimpleStringProperty resultProperty() {
        return result;
    }

    public void setResult(String result) {
        this.result.set(result);
    }

    public void doSubmit() {
        this.submitting.set(true);
        this.result.set("");

        String u = this.url.get();
        if (!u.startsWith("http")) {
            u = "http://" + u;
        }
        CompletableFuture<HttpResponse<String>> future =
                httpClientService.httpCall(HttpClientRequest.builder()
                        .setUrl(u)
                        .build());

        future.whenComplete((resp, e) -> {
            String body;
            if (e != null) {
                body = e.toString();
            } else {
                body = resp.body();
            }
            Platform.runLater(() -> {
                this.submitting.set(false);
                this.result.set(body);
            });
        });
    }
}
