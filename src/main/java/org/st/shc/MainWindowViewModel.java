package org.st.shc;

import javafx.beans.property.SimpleStringProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author abomb4 2022-06-22
 */
@Slf4j
public class MainWindowViewModel {

    private final SimpleStringProperty username = new SimpleStringProperty();

    private final SimpleStringProperty password = new SimpleStringProperty();

    public MainWindowViewModel() {
        this.username.addListener((observable, oldValue, newValue) -> {
            log.info("oldValue: {}", oldValue);
            log.info("newValue: {}", newValue);
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
}
