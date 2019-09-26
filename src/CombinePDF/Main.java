package CombinePDF;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main extends Application {

    //Default local export path and name for exported file
    private String defaultDesktopLocation = desktopFinder() + "Combined.pdf";

    private String[] check = {"docx", "doc"};

    //Variable that contains all the paths of all files to be combined/merged
    private List<String> paths = new ArrayList<>();

    private List<String> delete = new ArrayList<>();

    //other
    private Label dropped = new Label("Waiting...");
    private Button btn = new Button("Combine");
    private Button clear = new Button("Reset");
    private TextField textField = new TextField();
    private ListView<String> listView = new ListView<>();
    private ProgressIndicator progressBar = new ProgressIndicator(0);
    private Label lvLabel = new Label("All files to be combined:");
    private VBox vBox = new VBox();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Function to find the devices Desktop folder
     * *
     *
     * @return path to a specified directory in the device in this case it is desktop
     */
    private static String desktopFinder() {
        //TODO: add a fallback if it fails
        String dir = "Desktop"; //The directory to find
        String path = new File("").getAbsolutePath();
        return path.substring(0, path.indexOf(dir) + dir.length() + 1); //plus one for the forward slash;
    }

    /**
     * When the button Combine button is pressed it will jump to this function and combine all listed PDF's
     * in the variable paths
     */
    private void btnRun() {
        File[] files = new File[paths.size()];
        int counter = 0;

        for (String path : paths) {
            files[counter] = new File(path);
            counter++;
        }

        merge(files);
    }

    /**
     * Consumes exit request and shows a Confirmation Box to assure that the user wants to quit the
     * application
     */
    private void closeProgram() {
        boolean answer = ConfirmBox.display("Close Application", "Are you sure you want to quit? :(");
        if (answer) {
            deleteTempFiles();
            System.exit(0);
        }
    }

    /**
     * Change color when on hover with file
     * When the user hovers over the application stage it will it's colors with these functions
     */
    private void setDefaultColor() {
        vBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
    }

    private void setNewColor() {
        vBox.setStyle("-fx-background-color: rgba(240, 240, 240, 0.5);");
    }

    /**
     * @param primaryStage all styling, functionality and initial setup is in this function
     */
    @Override
    public void start(Stage primaryStage) {
        /*Sets the icon for the application*/
        primaryStage.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));
        File tempDir = new File("TEMP");
        if (tempDir.mkdirs()) {
            System.out.println(tempDir.getAbsolutePath());
        } else {
            if (!tempDir.exists())
                System.err.println("Could not create temporary folder.\nYou will not be abel to convert DOCX to PDF");
        }

        /*Information labels*/
        Label label = new Label("Drag the files to me in order.\nI'm not a mind reader. :'v");
        Label tfLabel = new Label("Export Location:");

        /*Sets the spacing for the Vertical Box and sets its color*/
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        setDefaultColor();

        /*Puts the Information labels in the Vertical Box*/
        vBox.getChildren().addAll(label, progressBar, lvLabel);

        /*Handles Close Request*/
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });


        vBox.setOnDragOver(event -> {
            /*change the color when a file is dragged over the pane*/
            setNewColor();
            if (event.getGestureSource() != vBox
                    && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        /*When the user exits or drops the file the pane goes back to its set color*/
        vBox.setOnDragExited(e -> setDefaultColor());

        /*Handles when the file is dropped*/
        vBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                //Gets the path
                String path = db.getFiles().toString();
                //Removes unnecessary characters []
                path = path.substring(1, path.length() - 1);

                /*Checks to see if multiple files were dropped at once*/
                if (path.contains(",")) {
                    //splits all the file paths into a string array
                    String[] arrPath = path.split(",");

                    //removes spaces in front and before of the string(path)
                    for (int i = 0; i < arrPath.length; i ++) arrPath[i] = arrPath[i].trim();

                    String extension;
                    String newName;
                    String originalName;
                    for (int i = 0; i < arrPath.length; i++) {
                        extension = arrPath[i];
                        if (extension.contains("docx") || arrPath[i].contains("doc")) {
                            originalName = new File(path).getName();
                            newName = tempDir.getAbsolutePath()
                                    + "\\"
                                    + originalName
                                    + UUID.randomUUID().toString()
                                    + ".pdf";
                            delete.add(newName);
                            Convert.toPDF(extension, newName);
                            arrPath[i] = newName;
                        }
                    }

                    //Turns the String array into a List and adds all its content to the paths variable
                    //stores the paths
                    paths.addAll(Arrays.asList(arrPath));

                    //Takes all strings in the string array and displays it on screen in the list view
                    for (String s : arrPath) listView.getItems().add(s);
                } else {
                    //Stores the path

                    if (path.substring(path.length() - 4).contains("docx")) {
                        String originalName = new File(path).getName();
                        String newName =
                                tempDir.getAbsolutePath()
                                        + "\\"
                                        + originalName
                                        + UUID.randomUUID().toString()
                                        + ".pdf";
                        delete.add(newName);
                        Convert.toPDF(path, newName);
                        path = newName;
                    }

                    paths.add(path);
                    //displays the added path
                    listView.getItems().add(path);
                }
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

        /*Combine button when clicked*/
        btn.setOnAction(e -> {
            //Checks if user has provided files to be combined.
            if (paths.isEmpty()) {
                lvLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
            } else {
                btn.setDisable(true);
                btnRun();
            }
        });

        /*Clear button*/
        clear.setOnAction(e -> clear());

        /*Shows the default export location can be changed*/
        textField.setText(defaultDesktopLocation);

        /*Gets the dimensions of the screen*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        /*margins for all items on screen*/
        int insectsVal = 12;
        VBox.setMargin(label, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(dropped, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(lvLabel, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(listView, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(tfLabel, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(textField, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btn, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(clear, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(progressBar, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));

        /*Location for buttons*/
        HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(btn, clear);

        ObservableList<javafx.scene.Node> list = vBox.getChildren();
        list.addAll(listView, tfLabel, textField, hb);

        StackPane root = new StackPane();
        root.getChildren().addAll(vBox);

        Scene scene = new Scene(root, screenSize.getWidth() / 3, screenSize.getHeight());


        primaryStage.setTitle("Comb-inator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Where the magic happens!
     * @param files to be merged/combined
     */
    private void merge(File[] files) {
        try {

            double max = 30;

            progressBar.setProgress(0 / max);
            //Loading an existing PDF document
            PDDocument[] docs = new PDDocument[files.length];

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) docs[i] = PDDocument.load(files[i]);
            }

            progressBar.setProgress(10.0 / max);

            //Instantiating PDFMergerUtility class
            PDFMergerUtility PDFMerger = new PDFMergerUtility();

            //Setting the destination file
            if (new File(textField.getText()).exists()) {
                boolean answer = ConfirmBox.display("Existing File", "You are trying to overwrite an existing file!\n"
                        + textField.getText()
                        + "\nDo you wish to proceed?");

                if (!answer) {
                    btn.setDisable(false);
                    lvLabel.setText("All files to be combined: (Merge aborted change file name)");
                    return;
                }

            }
            PDFMerger.setDestinationFileName(textField.getText());

            //adding the source files
            for (File f : files) PDFMerger.addSource(f);

            progressBar.setProgress(20.0 / max);

            //Merging the two documents
            //noinspection deprecation
            PDFMerger.mergeDocuments();

            clear();

            //Closing the documents
            for (PDDocument document : docs) document.close();

            progressBar.setProgress(25.0 / max);

            deleteTempFiles();

            dropped.setText("Documents merged! Check Desktop!");
            progressBar.setProgress(max / max);
        } catch (IOException e) {
            clear();
            e.printStackTrace();
        }
    }

    /**
     * Resets everything
     */
    private void clear() {
        lvLabel.setText("All files to be combined:");
        btn.setDisable(false);
        paths.clear();
        listView.getItems().clear();
        progressBar.setProgress(0);
        deleteTempFiles();
    }

    private void deleteTempFiles() {
        for (String pathToDelete : delete) {
            if (new File(pathToDelete).delete()) {
                System.out.println("Temp file deleted");
            } else {
                System.err.println("Could not delete: " + pathToDelete);
            }
        }
    }
}
