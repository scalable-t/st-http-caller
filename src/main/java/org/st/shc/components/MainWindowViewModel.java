package org.st.shc.components;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.extern.slf4j.Slf4j;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.concurrent.ThreadFactoryWithThreadId;

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

    private final SimpleStringProperty username = new SimpleStringProperty();

    private final SimpleStringProperty password = new SimpleStringProperty();

    private final SimpleBooleanProperty submitting = new SimpleBooleanProperty(false);

    public MainWindowViewModel() {
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

    public void doSubmit() {
        this.submitting.set(true);
    }
}
