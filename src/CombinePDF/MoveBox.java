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

import java.util.List;

class MoveBox {

    private static int[] value;

    static int[] display(String title, List<String> paths) {
        Stage window = new Stage();
        String[] numbers = new String[paths.size()];
        int counter = 1;

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = counter + "";
            counter++;
        }

        ComboBox<String> indexBox = new ComboBox<>();
        ComboBox<String> moveToBox = new ComboBox<>();

        indexBox.setItems(FXCollections.observableArrayList(numbers));
        moveToBox.setItems(FXCollections.observableArrayList(numbers));

        window.setOnCloseRequest(e -> {
            e.consume();
            window.close();
        });

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinHeight(200);
        window.setMinWidth(425);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        Button yesButton = new Button("Move");
        Button noButton = new Button("Cancel");

        yesButton.setOnAction(e -> {
            if (indexBox.getValue() == null || moveToBox.getValue() == null) {
                value = null;
            } else {
                value = new int[]{
                        Integer.parseInt(indexBox.getValue()),
                        Integer.parseInt(moveToBox.getValue())
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
        labelSection.getChildren().addAll(new Label("Move"), indexBox, new Label("to"), moveToBox);
        labelSection.setAlignment(Pos.CENTER);
        layButton.getChildren().addAll(yesButton, noButton);
        layButton.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(labelSection, layButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        //scene.getStylesheets().add(Main.THEME);
        window.setScene(scene);
        window.showAndWait();

        return value;
    }
}

