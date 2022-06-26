package org.st.shc.components;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.st.shc.framework.concurrent.ExecutorsBuilder;
import org.st.shc.framework.concurrent.ThreadFactoryWithThreadId;
import org.st.shc.services.HttpClientRequest;
import org.st.shc.services.HttpClientService;
import org.st.shc.services.HttpMethod;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author abomb4 2022-06-22
 */
public class MainWindowViewModel {

    /** Slf4J */
    private static final Logger log = LoggerFactory.getLogger(MainWindowViewModel.class);

    private final ScheduledThreadPoolExecutor schedule = ExecutorsBuilder.newScheduledBuilder()
            .setCorePoolSize(1)
            .setThreadFactory(new ThreadFactoryWithThreadId(tid -> "view-schedule-" + tid) {
            })
            .build();

    private final SimpleListProperty<TabViewModel> tabs = new SimpleListProperty<>();

    public static class TabViewModel {
        /** Slf4J */
        private static final Logger log = LoggerFactory.getLogger(TabViewModel.class);

        private final SimpleStringProperty url = new SimpleStringProperty();
        private final SimpleObjectProperty<HttpMethod> method = new SimpleObjectProperty<>(HttpMethod.GET);
    }
}
