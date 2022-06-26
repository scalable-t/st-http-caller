package org.st.shc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.st.shc.components.MainWindowView;
import org.st.shc.components.MainWindowViewModel;
import org.st.shc.di.HttpClientServiceConfiguration;
import org.st.shc.framework.bean.BeanDefinition;
import org.st.shc.framework.bean.BeanDefinitionAlreadyExistedException;
import org.st.shc.framework.bean.BeanFactory;
import org.st.shc.services.HttpClientRequest;
import org.st.shc.services.HttpClientService;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * JavaFX App
 */
public class App extends Application {

    BeanFactory beanFactory = new BeanFactory();

    @Override
    public void start(Stage stage) throws Exception {

        initBeanDefinitions(beanFactory);
        beanFactory.instantEveryBeans();

        HttpClientService httpClientService = beanFactory.getBean(HttpClientService.class);
        HttpClientRequest m = HttpClientRequest.builder()
                .setUrl("https://www.baidu.com")
                .build();
        CompletableFuture<HttpResponse<String>> future = httpClientService.httpCall(m);
        future.whenComplete((stringHttpResponse, throwable) -> System.out.println(stringHttpResponse.body()));

        MainWindowViewModel vm = new MainWindowViewModel();

        MainWindowView view = new MainWindowView(vm, httpClientService, ResourceBundle.getBundle("i18n/lang"));

        view.init();

        Scene scene = new Scene(view, 500, 470);

        stage.setTitle("the");
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(470);
        stage.show();
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            try {
                view.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.beanFactory.close();
    }

    private void initBeanDefinitions(BeanFactory beanFactory) throws BeanDefinitionAlreadyExistedException {
        Stream<HttpClientServiceConfiguration> providers = Stream.of(
                new HttpClientServiceConfiguration()
        );
        List<BeanDefinition<?>> definitions = providers
                .flatMap(v -> v.getDefinitions().stream())
                .toList();
        for (BeanDefinition<?> definition : definitions) {
            beanFactory.addBeanDefinition(definition);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
