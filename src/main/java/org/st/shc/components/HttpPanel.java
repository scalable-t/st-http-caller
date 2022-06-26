package org.st.shc.components;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.st.shc.services.HttpClientRequest;
import org.st.shc.services.HttpClientService;

import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * http 请求面板视图
 *
 * @author abomb4 2022-06-26
 */
public class HttpPanel extends StackPane {

    private final GridPane grid;
    private final Label labelUrl;
    private final TextField fieldUrl;
    private final Label labelBody;
    private final TextArea fieldBody;
    private final TextArea fieldResponseBody;
    private final Button btnSend;

    private final HttpClientService httpClientService;
    private final ResourceBundle lang;

    public HttpPanel(HttpClientService httpClientService, ResourceBundle lang) {
        this.httpClientService = Objects.requireNonNull(httpClientService, "httpClientService cannot be null");
        this.lang = Objects.requireNonNull(lang, "lang cannot be null");
        this.grid = new GridPane();
        this.labelUrl = new Label(lang.getString("http.url"));
        this.fieldUrl = new TextField("https://www.baidu.com");
        this.labelBody = new Label(lang.getString("http.body"));
        this.fieldBody = new TextArea("{\n  \"aaa\": \"111\"\n}");
        this.fieldResponseBody = new TextArea("");
        this.btnSend = new Button(lang.getString("http.send"));

        this.initLayout();
        this.initBindings();
    }


    private void initLayout() {
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(200, 200, Integer.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        this.grid.getColumnConstraints().addAll(column1, column2);

        RowConstraints rowBody = new RowConstraints(100, 100, Integer.MAX_VALUE);
        rowBody.setVgrow(Priority.ALWAYS);
        RowConstraints rowResponse = new RowConstraints(100, 100, Integer.MAX_VALUE);
        rowResponse.setVgrow(Priority.ALWAYS);
        RowConstraints rowBtn = new RowConstraints();
        this.grid.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), rowBody, rowResponse, rowBtn);
        this.getChildren().add(this.grid);

        this.grid.add(this.labelUrl, 0, 0);
        this.grid.add(this.fieldUrl, 1, 0);

        this.grid.add(this.labelBody, 0, 1, 2, 1);

        this.grid.add(this.fieldBody, 0, 2, 2, 1);

        this.grid.add(this.fieldResponseBody, 0, 3, 2, 1);
        VBox v = new VBox(this.btnSend);
        v.setAlignment(Pos.BASELINE_RIGHT);
        this.grid.add(v, 0, 4, 2, 1);
    }

    private void initBindings() {
        this.btnSend.armedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                CompletableFuture<HttpResponse<String>> future =
                        httpClientService.httpCall(HttpClientRequest.builder()
                                .setUrl(this.fieldUrl.getText())
                                .build());
                future.whenComplete((stringHttpResponse, throwable) -> {
                    String content;
                    if (throwable != null) {
                        content = throwable.toString();
                    } else {
                        content = stringHttpResponse.body();
                    }
                    Platform.runLater(() -> this.fieldResponseBody.setText(content));
                });
            }
        });
    }
}
