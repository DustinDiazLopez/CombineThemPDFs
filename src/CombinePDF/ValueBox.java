package CombinePDF;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class ValueBox {

    private static int value;

    static int display(String title, String message) {
        Stage window = new Stage();

        window.setOnCloseRequest(e -> {
            e.consume();
            window.close();
        });

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinHeight(200);
        window.setMinWidth(425);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        TextField textField = new TextField("4");
        Label label = new Label();
        label.setText(message);

        Button yesButton = new Button("Duplicate");
        Button noButton = new Button("Cancel");

        yesButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                value = Integer.parseInt(textField.getText());
                window.close();
            }
        });

        yesButton.setOnAction(e -> {
            value = Integer.parseInt(textField.getText());
            window.close();
        });

        noButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                window.close();
            }
        });

        noButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        HBox layButton = new HBox(10);
        layButton.getChildren().addAll(yesButton, noButton);
        layButton.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, textField, layButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        //scene.getStylesheets().add(Main.THEME);
        window.setScene(scene);
        window.showAndWait();

        return value;
    }
}

