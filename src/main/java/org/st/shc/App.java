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

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        BeanFactory beanFactory = new BeanFactory();
        initBeanDefinitions(beanFactory);
        beanFactory.instantEveryBeans();

        HttpClientService httpClientService = beanFactory.getBean(HttpClientService.class);
        HttpClientRequest m = HttpClientRequest.builder()
                .setUrl("https://www.baidu.com")
                .build();
        CompletableFuture<HttpResponse<String>> future = httpClientService.httpCall(m);
        future.whenComplete((stringHttpResponse, throwable) -> System.out.println(stringHttpResponse.body()));

        MainWindowViewModel vm = new MainWindowViewModel(httpClientService);
        vm.setUsername("aaa");
        vm.setPassword("bbb");
        vm.setUrl("http://www.baidu.com");

        MainWindowView view = new MainWindowView(vm);

        view.init();

        Scene scene = new Scene(view, 300, 270);

        stage.setTitle("the");
        stage.setScene(scene);
        stage.setMinHeight(300);
        stage.setMinWidth(250);
        stage.show();
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
