package org.st.shc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.st.shc.components.MainWindowView;
import org.st.shc.components.MainWindowViewModel;
import org.st.shc.di.HttpClientServiceConfiguration;
import org.st.shc.di.I18nConfiguration;
import org.st.shc.framework.bean.BeanDefinition;
import org.st.shc.framework.bean.BeanDefinitionAlreadyExistedException;
import org.st.shc.framework.bean.BeanFactory;
import org.st.shc.framework.bean.helper.BeanDefinitionsProvider;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.i18n.I18n;
import org.st.shc.framework.i18n.I18nManageable;
import org.st.shc.services.HttpClientRequest;
import org.st.shc.services.HttpClientService;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * JavaFX App
 */
public class App extends Application {

    /** Bean Factory */
    BeanFactory beanFactory = new BeanFactory();

    /** bean 提供方 */
    Stream<BeanDefinitionsProvider> providers = Stream.of(
            new HttpClientServiceConfiguration(),
            new I18nConfiguration()
    );

    @Override
    public void start(Stage stage) throws Exception {

        initBeanDefinitions(beanFactory);
        beanFactory.instantEveryBeans();

        MainWindowViewModel vm = new MainWindowViewModel();

        MainWindowView view = new MainWindowView(
                vm,
                beanFactory.getBean(HttpClientService.class),
                beanFactory.getBean(I18nManageable.class));

        view.init();

        Scene scene = new Scene(view, 500, 470);

        stage.setTitle("the");
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(470);
        stage.show();
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue) {
                    view.close();
                }
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
