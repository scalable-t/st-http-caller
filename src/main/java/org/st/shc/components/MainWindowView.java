package org.st.shc.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author abomb4 2022-06-23
 */
@Slf4j
public class MainWindowView extends GridPane {

    private final MainWindowViewModel vm;

    private boolean inited = false;
    private Text title;
    private Label usernameLabel;
    private TextField username;
    private Label passwordLabel;
    private PasswordField password;
    private Button submit;
    private Label labelSubmitting;

    public MainWindowView(MainWindowViewModel vm) {
        this.vm = Objects.requireNonNull(vm);
    }

    public void init() {

        if (!inited) {
            initComponents();
            initLayout();
            initBindings();
        }
    }

    private void initComponents() {
        title = new Text("welcome");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        usernameLabel = new Label("用户名");
        username = new TextField();
        passwordLabel = new Label("密码");
        password = new PasswordField();
        submit = new Button("登陆");
        labelSubmitting = new Label("");
    }

    private void initLayout() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(25, 25, 25, 25));
        this.setGridLinesVisible(true);

        this.add(title, 0, 0, 2, 1);
        this.add(usernameLabel, 0, 1);
        this.add(username, 1, 1);
        this.add(passwordLabel, 0, 2);
        this.add(password, 1, 2);
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.setSpacing(10);
        hBox.getChildren().add(labelSubmitting);
        hBox.getChildren().add(submit);
        this.add(hBox, 1, 3);
    }

    private void initBindings() {
        this.username.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() > 10 ? null : change));
        this.password.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() > 32 ? null : change));

        this.username.textProperty().bindBidirectional(vm.usernameProperty());
        this.password.textProperty().bindBidirectional(vm.passwordProperty());

        this.submit.armedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                vm.doSubmit();
            }
        });

        this.username.editableProperty().bind(vm.submittingProperty().not());
        vm.submittingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.labelSubmitting.textProperty().setValue("Submitting....");
            } else {
                this.labelSubmitting.textProperty().setValue("Login failed");
            }
        });
    }
}
