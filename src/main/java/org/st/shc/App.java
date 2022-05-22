package org.st.shc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        MainWindowViewModel vm = new MainWindowViewModel();
        vm.setUsername("aaa");
        vm.setPassword("bbb");

        MainWindowView view = new MainWindowView(vm);

        view.init();

        Scene scene = new Scene(view, 300, 270);

        stage.setTitle("the");
        stage.setScene(scene);
        stage.setMinHeight(300);
        stage.setMinWidth(250);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
