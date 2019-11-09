package CombinePDF;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main extends Application {

    //Default local export path and name for exported file
    private String defaultDesktopLocation = desktopFinder() + "Combined.pdf";

    //Variable that contains all the paths of all files to be combined/merged
    private List<String> paths = new ArrayList<>();

    private List<String> delete = new ArrayList<>();

    private Scene scene;

    //other
    private String titleAndVersion = "Combinator-inator v1.3.2";
    private Label dropped = new Label("Waiting...");
    private Button btn = new Button("Combine");
    private Button clear = new Button("Reset");
    private Button dup = new Button("Duplicate");
    private TextField textField = new TextField();
    private ListView<String> listView = new ListView<>();
    private ProgressIndicator progressBar = new ProgressIndicator(0);
    private Label lvLabel = new Label("All files to be combined:");
    private Label lblLog = new Label("Log for " + titleAndVersion + ":\n");
    private VBox vBox = new VBox();
    private ScrollPane scrollPane;
    private int fileCounter = 1;
    private Button removeFile = new Button("Remove");
    private Button moveFile = new Button("Move");
    private String lastScreenSizeFileLocation = new File("").getAbsolutePath() + "\\src\\CombinePDF\\Screen\\screen.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Function to find the Desktop folder
     * @return path to a specified directory in the device in this case it is desktop
     */
    private String desktopFinder() {
        //TODO: add a fallback if it fails | The problem: it assumes it will be ran in a child folder of the Desktop
        String dir = "Desktop"; //The directory to find
        String path = new File("").getAbsolutePath();
        path = path.substring(0, path.indexOf(dir) + dir.length() + 1); //plus one for the forward slash /
        if (!new File(path).exists()) {
            lvLabel.setText("All files to be combined: (PLEASE CHANGE EXPORT LOCATION)");
            setLog("Please check export location!\n\"" + defaultDesktopLocation + "\" was not found!");
        }
        return path;
    }

    /**
     * When the button Combine button is pressed it will jump to this function and combine all listed PDFs
     * in the variable paths
     */
    private void btnRun() {
        File[] files = new File[paths.size()];
        for (int i = 0; i < paths.size(); i++) files[i] = new File(paths.get(i));
        merge(files);
    }

    /**
     * Consumes exit request and shows a Confirmation Box to assure that the user wants to quit the
     * application
     */
    private void closeProgram() throws FileNotFoundException, UnsupportedEncodingException {
        boolean answer = ConfirmBox.display("Close Application", "Are you sure you want to quit? :(");
        if (answer) {
            deleteTempFiles();
            Write.txt(new File(lastScreenSizeFileLocation), scene.getHeight() + "," + scene.getWidth());
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
        vBox.setStyle("-fx-background-color: rgba(200, 200, 200, 0.1);");
    }

    /**
     * @param primaryStage all styling, functionality and initial setup is in this function
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        /*Sets the icon for the application*/
        primaryStage.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));
        File tempDir = new File("TEMP");
        if (tempDir.mkdirs()) {
            lblLog.setText(lblLog.getText() + "Created " + tempDir.getAbsolutePath());
        } else {
            if (!tempDir.exists()) {
                System.err.println("Could not create temporary folder.\nYou will not be abel to convert DOCX to PDF");
                setLog("An error occurred while created temporary folder.\n" +
                        "You will not be able to convert Word Documents to PDF files...\n");
            }
        }
        System.out.println(new File("").getAbsolutePath());

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
            try {
                closeProgram();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        });

        //Place holder for list view when no files have been selected
        listView.setPlaceholder(new Label("No files have been dropped."));

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
                    for (String s : arrPath) {
                        listView.getItems().add("[" + fileCounter + "] " + s);
                        fileCounter++;
                    }
                } else {
                    //Stores the path
                    String substring = path.substring(path.length() - 4);
                    if (substring.contains("docx") || substring.contains("doc")) {
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
                    listView.getItems().add("[" + fileCounter + "] " + path);
                    fileCounter++;
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
                setLog("No files have been selected.\n");
            } else {
                btn.setDisable(true);
                btnRun();
            }
        });

        /*Duplicate button when clicked*/
        dup.setOnAction(e -> {
            if (paths.isEmpty()) {
                lvLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.\n");
            } else {
                int duplicateAmount = DuplicateBox.display("Duplicator-inator",
                        "Enter the amount of copies you want of the current file(s)" +
                                " including the currently selected file(s):");
                Object[] temp = paths.toArray();
                for (int i = 0; i < duplicateAmount - 1; i++) {
                    for (Object objPath : temp) {
                        paths.add(objPath.toString());
                        listView.getItems().add("[" + fileCounter + "] " + objPath.toString());
                        fileCounter++;
                    }
                }
            }
        });

        removeFile.setOnAction(event -> {
            if (paths.isEmpty()) {
                lvLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.\n");
            } else {
                int index = RemoveBox.display("Remove-inator", "Select the file to be removed:", paths);
                if (!(index == -1)) {
                    setLog("Removing " + paths.get(index - 1) + "\n");
                    deleteItem(index - 1);
                    setLog("Removed " + paths.get(index - 1) + "\n");
                } else {
                    setLog("Aborted remove file..." + "\n");
                }
            }
        });

        moveFile.setOnAction(event -> {
            if (paths.isEmpty() || paths.size() < 2) {
                lvLabel.setText("All files to be combined: (You must have at least two [2] files)");
                setLog("No files have been selected.\n");
            } else {
                int[] indexs = MoveBox.display("Move-inator", paths);
                if (!(indexs == null)) {
                    setLog("Moving " + paths.get(indexs[0] - 1) + "\n" +
                            "to " + paths.get(indexs[1] - 1));
                    moveItem(indexs[0] - 1, indexs[1] - 1);
                    setLog("Finished moving...\n");
                } else {
                    setLog("Aborted move file..." + "\n");
                }
            }
        });
        /*Clear button*/
        clear.setOnAction(e -> {
            if (paths.isEmpty()) {
                clear();
            } else {
                boolean answer = ConfirmBox
                        .display("Clear-inator", "This action is irreversible are you sure?");
                if (answer) clear();
            }
        });

        clear.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #FA0300");
        /*Shows the default export location can be changed*/
        textField.setText(defaultDesktopLocation);

        scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(400);
        scrollPane.setMaxHeight(400);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(lblLog);
        scrollPane.setVisible(false);
        /*When enter key is pressed when the text field is in focus it will simulate
         * a button click on the run button also checks if the log is visible or not
         * it will do the opposite of its current state*/
        textField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btn.fire();

            if (event.getCode().toString().equals("ALT")) {
                if (scrollPane.isVisible()) {
                    scrollPane.setVisible(false);
                } else {
                    scrollPane.setVisible(true);
                }
            }
        });

        //if the button is selected and the enter key is pressed it will simulate a button click
        btn.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btn.fire();
        });

        //if the clear button is selected and the enter key is pressed it will simulate a button click
        clear.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) clear.fire();
        });

        //if the duplicate button is selected and the enter key is pressed it will simulate a button click
        dup.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) dup.fire();
        });

        //if the remove button is selected and the enter key is pressed it will simulate a button click
        removeFile.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) removeFile.fire();
        });

        /*Gets the dimensions of the screen*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        /*margins for all items on screen*/
        int insectsVal = 12;
        VBox.setMargin(label, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(scrollPane, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(dropped, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(lvLabel, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(listView, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(tfLabel, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(textField, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btn, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(clear, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(dup, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(removeFile, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(moveFile, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(progressBar, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));

        /*Location for buttons*/
        HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(btn, clear, dup, removeFile, moveFile);

        ObservableList<javafx.scene.Node> list = vBox.getChildren();
        list.addAll(listView, tfLabel, textField, hb, scrollPane);

        StackPane root = new StackPane();
        root.getChildren().addAll(vBox);

        String[] sizes = Read.txt(lastScreenSizeFileLocation).split(",");

        try {
            scene = new Scene(root, Double.parseDouble(sizes[0]), Double.parseDouble(sizes[1]));
        } catch (Exception e) {
            setLog("Could not read file for last used screen size...\nReason: " + e.getMessage() + "\n");
            scene = new Scene(root, screenSize.getWidth() / 3, screenSize.getHeight() - 75);
        }

        primaryStage.setTitle(titleAndVersion);
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
                    lblLog.setText(lblLog.getText() + "\nFile overwrite collision detected change file name.");
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
            setLog("Finished!\n");
            progressBar.setProgress(max / max);
        } catch (IOException e) {
            clear();
            e.printStackTrace();
            setLog(e.getMessage() + "\n");
        }
    }

    private void setLog(String log) {
        lblLog.setText(lblLog.getText() + log);
    }

    /**
     * Clears and resets all variables to their initially given values
     */
    private void clear() {
        lvLabel.setText("All files to be combined:");
        lblLog.setText("Log:\n");
        btn.setDisable(false);
        paths.clear();
        listView.getItems().clear();
        progressBar.setProgress(0);
        fileCounter = 1;
        deleteTempFiles();
    }

    /**
     * Deletes all temporally created PDF files in the local TEMP folder
     */
    private void deleteTempFiles() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String pathToDelete : delete) {
            if (new File(pathToDelete).delete()) {
                stringBuilder.append("Deleted temporary file at ").append(pathToDelete).append("\n");
            }
        }
        setLog(stringBuilder.toString());
    }

    /**
     * Deletes a file at a given index location
     * @param index index of the file to be removed from the listView and paths variables
     */
    private void deleteItem(int index) {
        //Deletes the string at the
        paths.remove(index);

        //Sets up the new list view
        newListView();
    }

    /**
     * Given a file index the method moves that file to a new location
     *
     * @param index  index of the file to be moved
     * @param moveTo index location to move the file
     */
    private void moveItem(int index, int moveTo) {
        //Stores the file to be moved
        String temp = paths.get(index);

        //Deletes the file in paths
        paths.remove(index);

        //Places the file in its new location
        paths.add(moveTo, temp);

        //Sets up the new list view
        newListView();
    }

    /**
     * Sets up a new list view with the updated paths
     */
    private void newListView() {
        //Cleats the list view and resets the global file counter
        listView.getItems().clear();
        fileCounter = 1;

        //Sets up the new list view with the file number
        for (String path : paths) {
            listView.getItems().add("[" + fileCounter + "] " + path);
            fileCounter++;
        }
    }
}
