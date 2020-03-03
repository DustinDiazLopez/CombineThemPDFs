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

class SelectRangeBox {

    private static int[] value;

    static int[] display(int totalPages, String btnText) {
        Stage window = new Stage();
        String[] numbers = new String[totalPages];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = (i + 1) + "";
        }

        ComboBox<String> indexBox = new ComboBox<>();

        indexBox.setItems(FXCollections.observableArrayList(numbers));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Insert-inator");
        window.setMinHeight(200);
        window.setMinWidth(425);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        Button yesButton = new Button(btnText);
        Button noButton = new Button("Cancel");

        yesButton.setOnAction(e -> {
            if (indexBox.getValue() == null) {
                value = null;
            } else {
                value = new int[]{
                        Integer.parseInt(indexBox.getValue()),
                        Integer.parseInt(indexBox.getValue()) + 1
                };
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
        HBox labelSection = new HBox(10);
        labelSection.getChildren().addAll(new Label("Insert after"), indexBox);
        labelSection.setAlignment(Pos.CENTER);
        layButton.getChildren().addAll(yesButton, noButton);
        layButton.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(labelSection, layButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        if (Main.styleSelected) {
            scene.getStylesheets().add(Main.THEME);
        }
        window.setScene(scene);
        window.showAndWait();

        window.setOnCloseRequest(e -> {
            e.consume();
            value = null;
            window.close();
        });

        return value;
    }
}

