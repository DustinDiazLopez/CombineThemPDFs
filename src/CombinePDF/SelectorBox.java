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

import java.util.ArrayList;
import java.util.List;

class SelectorBox {

    private static int value;

    static int display(List<String> paths, int exclude) {
        Stage window = new Stage();
        List<String> numbers = new ArrayList<>();

        for (int i = 0; i < paths.size(); i++) {
            if (i == exclude) continue;
            numbers.add((i + 1) + "");
        }

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(numbers));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Insert-inator");
        window.setMinHeight(200);
        window.setMinWidth(425);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        Label label = new Label();
        label.setText("Select the file to be inserted:");

        Button yesButton = new Button("Select");
        Button noButton = new Button("Cancel");

        yesButton.setOnAction(e -> {
            if (!(comboBox.getValue() == null)) {
                value = Integer.parseInt(comboBox.getValue());
            } else {
                value = -1;
            }
            window.close();
        });

        noButton.setOnAction(e -> {
            value = -1;
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
        if (Main.styleSelected) {
            scene.getStylesheets().add(Main.THEME);
        }
        window.setScene(scene);
        window.showAndWait();

        window.setOnCloseRequest(e -> {
            e.consume();
            value = -1;
            window.close();
        });

        return value;
    }
}

