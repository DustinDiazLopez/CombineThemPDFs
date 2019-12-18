package CombinePDF;

import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PreLoaderBox extends Preloader {
    ProgressBar bar;
    Stage stage;
    boolean isEmbedded = false;

    public void stop(Scene scene) {
        this.stage.setScene(scene);
    }

    public void progress(double progress) {
        handleProgressNotification(new ProgressNotification(progress));
    }

    private Scene createPreLoaderScene() {
        bar = new ProgressBar();
        BorderPane p = new BorderPane();
        p.setCenter(bar);
        return new Scene(p, 300, 150);
    }

    public void start(Stage stage) {
        //embedded stage has preset size
        isEmbedded = (stage.getWidth() > 0);

        this.stage = stage;
        stage.setScene(createPreLoaderScene());
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        if (pn.getProgress() != 1 && !stage.isShowing()) {
            stage.show();
        }
        bar.setProgress(pn.getProgress());
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (isEmbedded && stage.isShowing()) {
                //fade out, hide stage at the end of animation
                FadeTransition ft = new FadeTransition(
                        Duration.millis(1000), stage.getScene().getRoot());
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                final Stage s = stage;
                EventHandler<ActionEvent> eh = t -> s.hide();
                ft.setOnFinished(eh);
                ft.play();
            } else {
                stage.hide();
            }
        }
    }
}
