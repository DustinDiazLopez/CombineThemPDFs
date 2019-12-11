package CombinePDF;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class ChooseBox {

    private static String value;

    static String display(int selected) {
        Stage window = new Stage();

        Button yesButton = new Button("Choose");
        yesButton.setDisable(true);
        Button noButton = new Button("Cancel");

        String[] action = {
                "Move",
                "Remove",
                "Open"
        };

        ComboBox<String> comboBox = new ComboBox<>();

        comboBox.setOnAction(event -> {
            if (yesButton.isDisabled()) yesButton.setDisable(false);
            yesButton.setText(comboBox.getValue());
        });

        comboBox.setItems(FXCollections.observableArrayList(action));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Choose-inator");
        window.setMinHeight(200);
        window.setMinWidth(425);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        Label label = new Label();
        label.setText("Choose the action for file [" + (selected + 1) + "]:");

        yesButton.setOnAction(e -> {
            if (!(comboBox.getValue() == null)) {
                value = comboBox.getValue();
            } else {
                value = null;
            }
            window.close();
        });

        noButton.setOnAction(e -> {
            value = null;
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
        layout.getChildren().addAll(label, comboBox, layButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        //scene.getStylesheets().add(Main.THEME);
        window.setScene(scene);
        window.showAndWait();

        window.setOnCloseRequest(e -> {
            e.consume();
            noButton.fire();
            value = null;
        });

        return value;
    }
}

