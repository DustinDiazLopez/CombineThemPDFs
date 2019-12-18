package CombinePDF;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class ConfirmBox {

    private static boolean answer;

    static boolean display(String title, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinHeight(200);
        window.setMinWidth(375);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        Label label = new Label();
        label.setText(message);
        label.setMaxWidth(400);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });


        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        yesButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) yesButton.fire();

        });

        noButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) noButton.fire();
        });

        VBox layout = new VBox(10);
        HBox layButton = new HBox(10);
        layButton.getChildren().addAll(yesButton, noButton);
        layButton.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, layButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        if (Main.styleSelected) {
            scene.getStylesheets().add(Main.THEME);
        }
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
