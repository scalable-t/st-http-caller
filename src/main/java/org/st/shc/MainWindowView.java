package org.st.shc;

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

import java.util.Objects;

/**
 * @author abomb4 2022-06-23
 */
public class MainWindowView extends GridPane {

    private final MainWindowViewModel vm;

    boolean inited = false;
    Text title;
    Label usernameLabel;
    TextField username;
    Label passwordLabel;
    PasswordField password;
    Button submit;

    public MainWindowView(MainWindowViewModel vm) {
        this.vm = Objects.requireNonNull(vm);
    }

    public void init() {

        if (!inited) {
            initLayout();
            initComponents();
            initBindings();
        }
    }

    private void initLayout() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(25, 25, 25, 25));
        this.setGridLinesVisible(true);
    }

    private void initComponents() {
        {
            //
            title = new Text("welcome");
            title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            this.add(title, 0, 0, 2, 1);
        }

        {
            //
            usernameLabel = new Label("用户名");
            this.add(usernameLabel, 0, 1);
        }
        {
            username = new TextField();
            this.add(username, 1, 1);
        }

        {
            //
            passwordLabel = new Label("密码");
            this.add(passwordLabel, 0, 2);
        }

        {
            //
            password = new PasswordField();
            this.add(password, 1, 2);
        }

        {
            //
            submit = new Button("登陆");
            HBox hBox = new HBox(10);
            hBox.setAlignment(Pos.BOTTOM_RIGHT);
            hBox.getChildren().add(submit);
            this.add(hBox, 1, 3);
        }
    }

    private void initBindings() {
        this.username.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() > 10 ? null : change));
        this.password.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() > 32 ? null : change));

        this.username.textProperty().bindBidirectional(vm.usernameProperty());
        this.password.textProperty().bindBidirectional(vm.passwordProperty());
    }
}
