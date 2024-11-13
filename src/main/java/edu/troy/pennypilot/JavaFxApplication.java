package edu.troy.pennypilot;

import edu.troy.pennypilot.ui.SpringInitTask;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws Exception {
        log.info("Starting JavaFX...");
        var task = new SpringInitTask(getParameters());
        task.setOnSucceeded(event -> {
            applicationContext = task.getValue();
            stage.toFront();
            applicationContext.publishEvent(new StageReadyEvent(new Stage()));
            stage.hide();
        });
        showSplashScreen(stage, task);
        new Thread(task).start();
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping Spring...");
        applicationContext.stop();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }

    void showSplashScreen(Stage stage, Task task) {
        ImageView splash = new ImageView(new Image(getClass().getResourceAsStream("/images/splash.png")));
        splash.setFitHeight(600);
        var loadProgress = new ProgressBar(0);
        loadProgress.setPrefWidth(800);
        loadProgress.progressProperty().bind(task.progressProperty());
        var progressText = new Label("Load the money . . .");
        progressText.textProperty().bind(task.messageProperty());
        var splashLayout = new VBox(splash, loadProgress, progressText);
        splashLayout.setAlignment(Pos.CENTER);

        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        stage.setScene(splashScene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
