package CombinePDF;

import CombinePDF.Databases.HistoryDatabase;
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

class HistoryBox {

    private static String value;

    static String recover(String fileName) {
        Stage window = new Stage();

        Button yesButton = new Button("Choose");
        yesButton.setDisable(true);
        Button noButton = new Button("Cancel");
        Button eraseButton = new Button("Erase");

        eraseButton.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #cc0000");

        List<History> histories = HistoryDatabase.history(fileName, "DESC");
        List<History> display = HistoryDatabase.history(fileName, "ASC");

        assert histories != null;
        String[] action = new String[histories.size()];
        int counter = action.length;
        History temp;

        for (int i = 0; i < action.length; i++) {
            temp = histories.get(i);
            action[i] = (counter--) + " - " + temp.getDate();
        }


        ComboBox<String> comboBox = new ComboBox<>();

        comboBox.setOnAction(event -> {
            if (yesButton.isDisabled()) yesButton.setDisable(false);

            yesButton.setText("Recover");

            String val = comboBox.getValue();

            int selected = Integer.parseInt(val.substring(0, val.indexOf("-")).trim()) - 1;

            assert display != null;
            System.out.println("index " + (selected + 1) + ": " + display.get(selected).getPaths());

        });

        comboBox.setItems(FXCollections.observableArrayList(action));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("History-inator");
        window.setMinHeight(200);
        window.setMinWidth(425);
        window.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));

        Label label = new Label();
        label.setText("Choose a saved state to load:");

        yesButton.setOnAction(e -> {
            if (!(comboBox.getValue() == null)) {
                String val = comboBox.getValue();
                int selected = Integer.parseInt(val.substring(0, val.indexOf("-")).trim()) - 1;

                assert display != null;
                value = display.get(selected).getPaths();
            } else {
                value = null;
            }
            window.close();
        });

        noButton.setOnAction(e -> {
            value = null;
            window.close();
        });

        eraseButton.setOnAction(e -> {
            value = "-Execute Order 66-";
            window.close();
        });

        yesButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) yesButton.fire();

        });

        noButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) noButton.fire();
        });

        eraseButton.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) eraseButton.fire();
        });

        VBox layout = new VBox(10);
        HBox layButton = new HBox(10);
        layButton.getChildren().addAll(yesButton, noButton, eraseButton);
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

