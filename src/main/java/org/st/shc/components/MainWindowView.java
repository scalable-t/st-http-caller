package org.st.shc.components;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.st.shc.services.HttpClientService;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author abomb4 2022-06-23
 */
@Slf4j
public class MainWindowView extends BorderPane implements Closeable {

    private final MainWindowViewModel vm;
    private final HttpClientService httpClientService;
    private final ResourceBundle lang;

    private boolean inited = false;

    // region --------- menus ----------
    private Menu menuFile;
    private MenuItem menuItemImport;
    private Menu menuHelp;
    private MenuItem menuItemAbout;
    private MenuBar menuBar;

    private Scene aboutScene;
    private Stage aboutStage;
    // endregion --------- menus ----------

    // region --------- left panel ----------
    private TabPane leftTabPane;
    private Tab leftTabHistory;
    // endregion --------- left panel ----------

    // region --------- main panel ----------
    private Pane mainNothingPanel;
    private TabPane mainTabPane;
    private Tab mainTab;
    private HttpPanel mainHttpPane;
    // endregion --------- main panel ----------

    // region --------- layouts ----------
    private VBox mainVbox;
    private SplitPane centerSplitPane;
    private SplitPane.Divider centerSplitDivider1;
    // endregion --------- layouts ----------

    public MainWindowView(MainWindowViewModel vm, HttpClientService httpClientService, ResourceBundle lang) {
        this.vm = Objects.requireNonNull(vm);
        this.lang = Objects.requireNonNull(lang, "lang cannot be null");
        this.httpClientService = Objects.requireNonNull(httpClientService, "httpClientService cannot be null");
    }

    public void init() {
        if (!inited) {
            initComponents();
            initLayout();
            initBindings();
        }
    }

    private void initComponents() {
        this.menuItemImport = new MenuItem("Menu");
        this.menuFile = new Menu("File", null, this.menuItemImport);
        this.menuItemAbout = new MenuItem("About");
        this.menuHelp = new Menu("Help", null, this.menuItemAbout);
        this.menuBar = new MenuBar(this.menuFile, this.menuHelp);
        this.menuBar.useSystemMenuBarProperty().set(true);

        this.aboutScene = new Scene(new Label("i9oasjdifoasdjiofjsdaf"));
        this.aboutStage = new Stage(StageStyle.UTILITY);
        this.aboutStage.setScene(this.aboutScene);
        this.aboutStage.setMinWidth(300);
        this.aboutStage.setMinHeight(300);
        this.aboutStage.setResizable(false);
        this.aboutStage.setTitle("About");

        this.leftTabHistory = new Tab("History");
        this.leftTabPane = new TabPane(leftTabHistory);
        this.leftTabPane.setSide(Side.LEFT);
        this.leftTabHistory.setClosable(false);
        this.leftTabHistory.setContent(new Label("oaisjdfoiajsdoifji"));

        this.mainNothingPanel = new Pane();
        this.mainHttpPane = new HttpPanel(this.httpClientService, this.lang);
        this.mainTab = new Tab("tab1");
        this.mainTab.setClosable(false);
        this.mainTab.setContent(this.mainHttpPane);
        this.mainTabPane = new TabPane(mainTab);

        this.mainVbox = new VBox();
        this.centerSplitPane = new SplitPane(this.leftTabPane, this.mainTabPane);
        this.centerSplitPane.getDividers().stream().findFirst().ifPresent(d -> d.setPosition(0.2));
    }

    private void initLayout() {
        this.setTop(this.menuBar);
        this.setCenter(this.centerSplitPane);
    }

    private void initBindings() {
        // this.username.setTextFormatter(
        //         new TextFormatter<>(change -> change.getControlNewText().length() > 10 ? null : change));
        // this.password.setTextFormatter(
        //         new TextFormatter<>(change -> change.getControlNewText().length() > 32 ? null : change));
        //
        // this.username.textProperty().bindBidirectional(vm.usernameProperty());
        // this.password.textProperty().bindBidirectional(vm.passwordProperty());
        // this.url.textProperty().bindBidirectional(vm.urlProperty());
        //
        // this.submit.armedProperty().addListener((observable, oldValue, newValue) -> {
        //     if (newValue) {
        //         vm.doSubmit();
        //     }
        // });
        //
        // this.username.editableProperty().bind(vm.submittingProperty().not());
        // this.url.editableProperty().bind(vm.submittingProperty().not());
        // vm.submittingProperty().addListener((observable, oldValue, newValue) -> {
        //     if (newValue) {
        //         this.labelSubmitting.textProperty().setValue("Submitting....");
        //     } else {
        //         this.labelSubmitting.textProperty().setValue("Login failed");
        //     }
        // });
        //
        // this.resultLabel.textProperty().bind(vm.resultProperty());

        this.menuItemAbout.setOnAction(event -> {
            if (this.aboutStage.isShowing()) {
                this.aboutStage.requestFocus();
            } else {
                this.aboutStage.show();
            }
        });
        this.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && this.aboutStage.isShowing()) {
                this.aboutStage.close();
            }
        });
    }

    @Override
    public void close() throws IOException {
        if (this.aboutStage.isShowing()) {
            this.aboutStage.close();
        }
    }
}
