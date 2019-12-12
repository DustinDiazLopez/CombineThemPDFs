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
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
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
    private String titleAndVersion = "Combinator-inator v0.3";
    private Label dropped = new Label("Waiting...");
    private Button btnCombine = new Button("Combine");
    private Button btnPreview = new Button("Preview");
    private Button btnRefreshListView = new Button("Refresh View");
    private Button btnClear = new Button("Reset");
    private Button btnDuplicate = new Button("Duplicate");
    private TextField textFieldForExportFileLocation = new TextField();
    private ListView<String> listView = new ListView<>();
    private ProgressIndicator progressBar = new ProgressIndicator(0);
    private String lvLblDefault = "All files to be combined:";
    private Label listViewLabel = new Label(lvLblDefault);
    private Label lblLog = new Label("Log for " + titleAndVersion + ":\n");
    private VBox vBox = new VBox();
    private ScrollPane scrollPane;
    private int fileCounter = 1;
    private Button btnRemoveFile = new Button("Remove");
    private Button btnMoveFile = new Button("Move");
    private String lastScreenSizeFileLocation = new File("").getAbsolutePath();
    private int pages = 0;
    private File tempDir;
    private String last;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * When the button Combine button is pressed it will jump to this function and combine all listed PDFs
     * in the variable paths
     */
    private void btnRun() {
        File[] files = new File[paths.size()];
        for (int i = 0; i < paths.size(); i++) files[i] = new File(paths.get(i));
        merge(files, true);

    }

    /**
     * When the button Combine button is pressed it will jump to this function and combine all listed PDFs
     * in the variable paths
     */
    private void btnPreview() {
        File[] files = new File[paths.size()];
        for (int i = 0; i < paths.size(); i++) files[i] = new File(paths.get(i));
        merge(files, false);
        File tempFile = new File(last);
        openFile(tempFile);
    }

    private void openFile(File file) {
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ignored) {
                    ConfirmBox.display("Error", "No default application set for displaying PDF files!");
                }
            }
        } else {
            ConfirmBox.display("File does not exist", "Could not find specified file.");
        }
    }

    /**
     * Consumes exit request and shows a Confirmation Box to assure that the user wants to quit the
     * application
     */
    private void closeProgram() throws FileNotFoundException, UnsupportedEncodingException {
        boolean answer = ConfirmBox.display("Close-inator", "Are you sure you want to quit? :(");
        if (answer) {
            clear();
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
     * Function to find the Desktop folder
     *
     * @return path to a specified directory in the device in this case it is desktop
     */
    private String desktopFinder() {
        //TODO: add a fallback if it fails | The problem: it assumes it will be ran in a child folder of the Desktop
        String dir = "Desktop"; //The directory to find
        String path = new File("").getAbsolutePath();
        path = path.substring(0, path.indexOf(dir) + dir.length() + 1); //plus one for the forward slash /
        if (!new File(path).exists()) {
            listViewLabel.setText("All files to be combined: (PLEASE CHANGE EXPORT LOCATION)");
            setLog("Please check export location!\n\"" + defaultDesktopLocation + "\" was not found!");
        }
        return path;
    }

    /**
     * @param primaryStage all styling, functionality and initial setup is in this function
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        /*Sets the icon for the application*/
        primaryStage.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));
        tempDir = new File("TEMP");
        if (tempDir.mkdirs()) {
            lblLog.setText(lblLog.getText() + "Created " + tempDir.getAbsolutePath());
        } else {
            if (!tempDir.exists()) {
                System.err.println("Could not create temporary folder.\nYou will not be abel to convert DOCX to PDF");
                setLog("An error occurred while created temporary folder.\n" +
                        "You will not be able to convert Word Documents to PDF files...\n");
            }
        }

        /*Information labels*/
        Label label = new Label("Drag the files to me in order.\nI'm not a mind reader. :'v");
        Label tfLabel = new Label("Export Location:");

        /*Sets the spacing for the Vertical Box and sets its color*/
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        setDefaultColor();

        /*Puts the Information labels in the Vertical Box*/
        vBox.getChildren().addAll(label, progressBar, listViewLabel);

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
                            if (tempDir.getAbsolutePath().contains("/")) {
                                newName = tempDir.getAbsolutePath()
                                        + "/"
                                        + originalName
                                        + UUID.randomUUID().toString()
                                        + ".pdf";
                            } else {
                                newName = tempDir.getAbsolutePath()
                                        + "\\"
                                        + originalName
                                        + UUID.randomUUID().toString()
                                        + ".pdf";
                            }
                            delete.add(newName);
                            Convert.wordToPDF(extension, newName);
                            arrPath[i] = newName;
                        } else if (extension.contains("png") || extension.contains("jpg") || extension.contains("gif")) {
                            originalName = new File(path).getName();
                            if (tempDir.getAbsolutePath().contains("/")) {
                                newName = tempDir.getAbsolutePath()
                                        + "/"
                                        + originalName
                                        + UUID.randomUUID().toString()
                                        + ".pdf";
                            } else {
                                newName = tempDir.getAbsolutePath()
                                        + "\\"
                                        + originalName
                                        + UUID.randomUUID().toString()
                                        + ".pdf";
                            }
                            delete.add(newName);
                            Convert.imageToPDF(extension, newName);
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

                        String newName;
                        if (tempDir.getAbsolutePath().contains("/")) {
                            newName = tempDir.getAbsolutePath()
                                    + "/"
                                    + originalName
                                    + UUID.randomUUID().toString()
                                    + ".pdf";
                        } else {
                            newName = tempDir.getAbsolutePath()
                                    + "\\"
                                    + originalName
                                    + UUID.randomUUID().toString()
                                    + ".pdf";
                        }

                        delete.add(newName);
                        Convert.wordToPDF(path, newName);
                        path = newName;
                    } else if (substring.contains("png") || substring.contains("jpg") || substring.contains("gif")) {
                        String originalName = new File(path).getName();

                        String newName;
                        if (tempDir.getAbsolutePath().contains("/")) {
                            newName = tempDir.getAbsolutePath()
                                    + "/"
                                    + originalName
                                    + UUID.randomUUID().toString()
                                    + ".pdf";
                        } else {
                            newName = tempDir.getAbsolutePath()
                                    + "\\"
                                    + originalName
                                    + UUID.randomUUID().toString()
                                    + ".pdf";
                        }

                        delete.add(newName);
                        Convert.imageToPDF(path, newName);
                        path = newName;
                    }

                    paths.add(path);
                    //displays the added path
                    listView.getItems().add("[" + fileCounter + "] " + path);
                    fileCounter++;
                }
                success = true;
            }

            try {
                totalPages();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);
            event.consume();
        });

        /*Combine button when clicked*/
        btnCombine.setOnAction(e -> {
            //Checks if user has provided files to be combined.
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.\n");
            } else {
                btnCombine.setDisable(true);
                btnRun();
            }
        });

        btnPreview.setOnAction(e -> {
            //Checks if user has provided files to be combined.
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.\n");
            } else {
                btnPreview();
            }
        });

        btnRefreshListView.setOnAction(e -> {
            //Checks if user has provided files to be combined.
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Nothing to refresh...)");
                setLog("No files have been selected.\n");
            } else {
                try {
                    newListView();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        /*Duplicate button when clicked*/
        btnDuplicate.setOnAction(e -> {
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.\n");
            } else {
                int duplicateAmount = DuplicateBox.display(
                        "Enter the amount of copies you want of the current file(s)" +
                                " including the currently selected file(s):");
                if (!(duplicateAmount <= 0)) {
                    Object[] temp = paths.toArray();
                    for (int i = 0; i < duplicateAmount - 1; i++) {
                        for (Object objPath : temp) {
                            paths.add(objPath.toString());
                            listView.getItems().add("[" + fileCounter + "] " + objPath.toString());
                            fileCounter++;
                        }
                    }

                    pages *= duplicateAmount;
                    updateFileEstimationHeaderInformation();
                } else {
                    setLog("Invalid input for duplicator-inator...\n");
                }
            }
        });

        btnRemoveFile.setOnAction(event -> {
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.\n");
            } else {
                int index = RemoveBox.display(paths);

                try {
                    setLog("Removing " + paths.get(index - 1) + ".\n");
                    deleteItem(index - 1);
                    setLog("Removed.\n");
                } catch (Exception e) {
                    setLog("Aborted remove file..." + "\n" + e.getMessage() + "\n");
                }
            }
        });

        btnMoveFile.setOnAction(event -> {
            if (paths.isEmpty() || paths.size() < 2) {
                listViewLabel.setText("All files to be combined: (You must have at least two [2] files)");
                setLog("No files have been selected.\n");
            } else {
                int[] indexes = MoveBox.display(paths);
                if (!(indexes == null)) {
                    setLog("Moving " +
                            "..." + paths.get(indexes[0] - 1).substring(paths.get(indexes[0] - 1).length() / 2).trim() +
                            "\n" +
                            "to " + "" +
                            "..." + paths.get(indexes[1] - 1).substring(paths.get(indexes[1] - 1).length() / 2).trim() +
                            "\n");
                    try {
                        moveItem(indexes[0] - 1, indexes[1] - 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setLog("Finished moving...\n");
                } else {
                    setLog("Aborted move file..." + "\n");
                }
            }
        });

        /*Clear button*/
        btnClear.setOnAction(e -> {
            if (paths.isEmpty()) {
                clear();
            } else {
                boolean answer = ConfirmBox.display(
                        "Clear-inator",
                        "This action is irreversible are you sure?"
                );

                if (answer) clear();
            }
        });

        listView.setOnMouseClicked(event -> {
            if ((!event.getTarget().toString().contains("StackPane")
                    && !event.getTarget().toString().contains("ListView"))) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    String informationAboutSelectedElement = event.getPickResult().toString();
                    int charLocationOne = informationAboutSelectedElement.indexOf("text=\"[") + 7;
                    informationAboutSelectedElement = informationAboutSelectedElement.substring(charLocationOne);
                    int charLocationTwo = informationAboutSelectedElement.indexOf("]");
                    informationAboutSelectedElement = informationAboutSelectedElement.substring(0, charLocationTwo);
                    int indexOfSelected = Integer.parseInt(informationAboutSelectedElement) - 1;
                    String path = paths.get(indexOfSelected);
                    PDDocument document;

                    try {
                        document = PDDocument.load(new File(path));
                        int totalNumberOfPages = document.getNumberOfPages();
                        pages += totalNumberOfPages;
                        String answer = ChooseBox.display(indexOfSelected);

                        if (answer != null) {
                            switch (answer) {
                                case "Remove":
                                    deleteItem(indexOfSelected);
                                    break;
                                case "Move":
                                    int[] indexes = MoveBox.display(paths, indexOfSelected);

                                    if (!(indexes == null)) {
                                        setLog("Moving " +
                                                "..." + paths.get(indexes[0] - 1).substring(paths.get(indexes[0] - 1).length() / 2).trim() +
                                                "\n" +
                                                "to " + "" +
                                                "..." + paths.get(indexes[1] - 1).substring(paths.get(indexes[1] - 1).length() / 2).trim() +
                                                "\n");
                                        moveItem(indexes[0] - 1, indexes[1] - 1);
                                        setLog("Finished moving...\n");
                                    } else {
                                        setLog("Aborted move file..." + "\n");
                                    }
                                    break;
                                case "Open":
                                    openFile(new File(path));
                                    break;
                                case "Delete Page":
                                    //TODO: Remove page from file (careful not to edit the original one. Make a copy first)
                                    duplicateFile(new File(path), indexOfSelected);
                                    int pageNumber = NumberBox.display(totalNumberOfPages);
                                    removePageInFile(new File(paths.get(indexOfSelected)), --pageNumber);
                                    break;
                            }
                        }

                        document.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    String informationAboutSelectedElement = event.getPickResult().toString();
                    int charLocationOne = informationAboutSelectedElement.indexOf("text=\"[") + 7;
                    informationAboutSelectedElement = informationAboutSelectedElement.substring(charLocationOne);
                    int charLocationTwo = informationAboutSelectedElement.indexOf("]");
                    informationAboutSelectedElement = informationAboutSelectedElement.substring(0, charLocationTwo);
                    int selected = Integer.parseInt(informationAboutSelectedElement) - 1;
                    String path = paths.get(selected);
                    String name = new File(path).getName();

                    if (ConfirmBox.display("Open " + name, "Do you wish to open " + name + "?")) {
                        openFile(new File(path));
                    }
                }
            }
        });

        //Directory Chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));

        Button btnSelDirectory = new Button("Change Export Location");

        btnSelDirectory.setOnAction(e -> {
            setLog("Browsing...\n");
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                String dir = selectedDirectory.getAbsolutePath();

                if (dir.contains("/")) {
                    dir += "/Combined.pdf";
                } else {
                    dir += "\\Combined.pdf";
                }

                textFieldForExportFileLocation.setText(dir);

                setLog("Changed export location to:\n" + dir);
            } else {
                setLog("No directory was selected.");
            }
        });

        /*Shows the default export location can be changed*/
        textFieldForExportFileLocation.setText(defaultDesktopLocation);

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
        textFieldForExportFileLocation.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnCombine.fire();

            if (event.getCode().toString().equals("ALT")) {
                if (scrollPane.isVisible()) {
                    scrollPane.setVisible(false);
                } else {
                    scrollPane.setVisible(true);
                }
            }
        });

        //if the button is selected and the enter key is pressed it will simulate a button click
        btnCombine.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnCombine.fire();
        });

        btnRefreshListView.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnRefreshListView.fire();
        });

        //if the button is selected and the enter key is pressed it will simulate a button click
        btnPreview.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnPreview.fire();
        });

        //if the clear button is selected and the enter key is pressed it will simulate a button click
        btnClear.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnClear.fire();
        });

        //if the duplicate button is selected and the enter key is pressed it will simulate a button click
        btnDuplicate.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnDuplicate.fire();
        });

        //if the remove button is selected and the enter key is pressed it will simulate a button click
        btnRemoveFile.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnRemoveFile.fire();
        });

        /*Gets the dimensions of the screen*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        /*margins for all items on screen*/
        int insectsVal = 12;
        VBox.setMargin(label, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(scrollPane, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(dropped, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(listViewLabel, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(listView, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(tfLabel, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(textFieldForExportFileLocation, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnSelDirectory, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnCombine, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(btnRefreshListView, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(btnPreview, new Insets(insectsVal, insectsVal, 0, insectsVal));
        VBox.setMargin(btnClear, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnDuplicate, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnRemoveFile, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnMoveFile, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(progressBar, new Insets(insectsVal, insectsVal, insectsVal, insectsVal));

        /*Location for buttons*/
        HBox hBoxBtnModificationsLayout = new HBox();
        HBox hBoxBtnExecuteLayout = new HBox();
        VBox vBoxBtnLayout = new VBox();

        vBoxBtnLayout.setSpacing(5);
        vBoxBtnLayout.setAlignment(Pos.CENTER);
        hBoxBtnExecuteLayout.setSpacing(5);
        hBoxBtnExecuteLayout.setAlignment(Pos.CENTER);
        hBoxBtnModificationsLayout.setSpacing(5);
        hBoxBtnModificationsLayout.setAlignment(Pos.CENTER);

        hBoxBtnExecuteLayout.getChildren().addAll(btnCombine, btnClear);
        hBoxBtnModificationsLayout.getChildren().addAll(btnDuplicate, btnRemoveFile, btnMoveFile, btnRefreshListView);
        //Creating a line object
        Line line = new Line();

        //Setting the properties to a line
        line.setStartX(100.0);
        line.setStartY(150.0);
        line.setEndX(500.0);
        line.setEndY(150.0);
        line.setStyle("-fx-stroke: EFF0F1;");

        btnClear.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #cc0000");
        btnCombine.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #0095FF");

        vBoxBtnLayout.getChildren().addAll(hBoxBtnModificationsLayout, line, hBoxBtnExecuteLayout, scrollPane);

        ObservableList<javafx.scene.Node> list = vBox.getChildren();

        HBox hBoxSearchForFileLayout = new HBox();
        HBox.setHgrow(textFieldForExportFileLocation, Priority.ALWAYS);
        HBox.setHgrow(btnSelDirectory, Priority.ALWAYS);
        hBoxSearchForFileLayout.getChildren().addAll(textFieldForExportFileLocation, btnPreview, btnSelDirectory);
        hBoxSearchForFileLayout.setAlignment(Pos.CENTER);
        hBoxSearchForFileLayout.setSpacing(5);
        list.addAll(listView, tfLabel, hBoxSearchForFileLayout, vBoxBtnLayout);

        StackPane root = new StackPane();
        root.getChildren().addAll(vBox);

        //Lastly used screen sizes
        if (lastScreenSizeFileLocation.contains("/")) {
            lastScreenSizeFileLocation += "/src/CombinePDF/Screen/screen.txt";
        } else {
            lastScreenSizeFileLocation += "\\src\\CombinePDF\\Screen\\screen.txt";
        }

        String[] sizes = Read.txt(lastScreenSizeFileLocation).split(",");

        /* *
         * Tries to use the lastly used screen dimensions
         * if an error occurs while parsing the values
         * it will use the default predefined dimensions
         * */
        try {
            scene = new Scene(root, Double.parseDouble(sizes[0]), Double.parseDouble(sizes[1]));
        } catch (Exception e) {
            setLog("Could not read file for last used screen size...\nReason: " + e.getMessage() + "\n");
            scene = new Scene(root, screenSize.getWidth() / 3, screenSize.getHeight() - 75);
        }

        primaryStage.setTitle(titleAndVersion);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(567d);
        primaryStage.setMinWidth(526d);
        primaryStage.show();
    }

    /**
     * Removes a page in a PDF file
     *
     * @param file       file that will have a page removed
     * @param pageNumber number of the page that is wished to be removed
     * @throws IOException throws an exception if the file does not exist
     */
    private void removePageInFile(File file, int pageNumber) throws IOException {
        if (file.getAbsolutePath().contains(tempDir.getAbsolutePath())) {
            PDDocument document = PDDocument.load(file);
            document.removePage(pageNumber);
            document.save(file);
            document.close();
        } else {
            ConfirmBox.display("Page-remover-inator", "Attempted to edit a user file!\nFile must be in temp folder");
        }
    }

    /**
     * Duplicates a file into a TEMP folder.
     *
     * @param from path of original file
     * @throws IOException throws an exception when file is not found.
     */
    public void duplicateFile(File from, int index) throws IOException {
        String path = tempDir.getAbsolutePath();

        String newName = from.getName() + UUID.randomUUID().toString() + ".pdf";

        if (tempDir.getAbsolutePath().contains("/")) path += ("/" + newName); //mac
        else path += ("\\" + newName); //windows

        delete.add(path); //adds the file path to be erased later

        paths.remove(index);
        paths.add(index, path);

        newListView();

        FileUtils.copyFile(from, new File(path));
    }

    /**
     * Updates the total number of pages in the application
     */
    private void totalPages() throws IOException {
        pages = 0;
        PDDocument document;
        for (String path : paths) {
            if (new File(path).isFile()) {
                document = PDDocument.load(new File(path));
                pages += document.getNumberOfPages();
                document.close();
            }
        }

        updateFileEstimationHeaderInformation();
    }

    /**
     * Updates the information about how many pages there will be in the final file
     */
    private void updateFileEstimationHeaderInformation() {
        if (paths.size() != 1 && pages != 1)
            listViewLabel.setText(lvLblDefault + " " + paths.size() + " files | " + pages + " pages");
        else if (paths.size() == 1 && pages > 1)
            listViewLabel.setText(lvLblDefault + " " + paths.size() + " file | " + pages + " pages");
        else
            listViewLabel.setText(lvLblDefault + " " + paths.size() + " file | " + pages + " page");
    }

    /**
     * Where the magic happens!
     *
     * @param files to be merged/combined
     */
    private void merge(File[] files, boolean combine) {
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

            if (combine) {
                //Setting the destination file
                if (new File(textFieldForExportFileLocation.getText()).exists()) {
                    boolean answer = ConfirmBox.display("Existing File", "You are trying to overwrite an existing file!\n"
                            + textFieldForExportFileLocation.getText()
                            + "\nDo you wish to proceed?");

                    if (!answer) {
                        btnCombine.setDisable(false);
                        listViewLabel.setText("All files to be combined: (Merge aborted change file name)");
                        lblLog.setText(lblLog.getText() + "\nFile overwrite collision detected change file name.");
                        return;
                    }
                }
            }

            if (combine) {
                PDFMerger.setDestinationFileName(textFieldForExportFileLocation.getText());
            } else {
                if (tempDir.getAbsolutePath().contains("/")) {
                    last = tempDir.getAbsolutePath() + "/" + UUID.randomUUID() + ".pdf";
                } else {
                    last = tempDir.getAbsolutePath() + "\\" + UUID.randomUUID() + ".pdf";
                }

                delete.add(last);
                PDFMerger.setDestinationFileName(last);
            }

            //adding the source files
            for (File f : files) PDFMerger.addSource(f);

            progressBar.setProgress(20.0 / max);

            //Merging the two documents
            //noinspection deprecation
            PDFMerger.mergeDocuments();

            if (combine) clear();

            //Closing the documents
            for (PDDocument document : docs) document.close();

            progressBar.setProgress(25.0 / max);

            if (combine) deleteTempFiles();

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
        listViewLabel.setText("All files to be combined:");
        lblLog.setText("Log:\n");
        btnCombine.setDisable(false);
        paths.clear();
        listView.getItems().clear();
        progressBar.setProgress(0);
        fileCounter = 1;
        pages = 0;
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
    private void deleteItem(int index) throws IOException {
        //Deletes the string at the
        paths.remove(index);

        //Sets up the new list view
        newListView();

        //Sets the new page amount
        totalPages();
    }

    /**
     * Given a file index the method moves that file to a new location
     *
     * @param index  index of the file to be moved
     * @param moveTo index location to move the file
     */
    private void moveItem(int index, int moveTo) throws IOException {
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
    private void newListView() throws IOException {
        //Cleats the list view and resets the global file counter
        listView.getItems().clear();
        fileCounter = 1;

        //Sets up the new list view with the file number
        for (String path : paths) {
            listView.getItems().add("[" + fileCounter + "] " + path);
            fileCounter++;
        }

        totalPages();
    }
}
