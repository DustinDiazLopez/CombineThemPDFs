package CombinePDF;

import CombinePDF.Databases.HistoryDatabase;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        ListView listView = new ListView();
        listView.setPlaceholder(new Label("No saved state has been selected."));

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
            String paths = display.get(selected).getPaths();

            List<String> list = new ArrayList<>(Arrays.asList(paths.trim().substring(1, paths.length() - 1).trim().split(",")));

            AtomicInteger i = new AtomicInteger();

            listView.getItems().clear();

            list.forEach(e -> listView.getItems().add(("[" + i.incrementAndGet()) + "] " + e.trim()));

        });

        comboBox.setItems(FXCollections.observableArrayList(action));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("History-inator");
        window.setMinHeight(400);
        window.setMinWidth(800);
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

        HBox hBox = new HBox(layout, listView);

        HBox.setHgrow(layout, Priority.ALWAYS);
        HBox.setHgrow(listView, Priority.ALWAYS);

        Scene scene = new Scene(hBox, 800, 400);
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

