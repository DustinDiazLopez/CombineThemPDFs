package CombinePDF;

import CombinePDF.databases.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;

public class Main extends Application {

    //Default local export path and name for exported file
    private String defaultDesktopLocation = desktopFinder() + "Combined.pdf";

    //Variable that contains all the paths of all files to be combined/merged
    private static List<String> paths = new ArrayList<>();

    //List that contains all the files to be stored in the delete database for them to be deleted when history is cleared
    public static List<String> delete = new ArrayList<>();

    //JavaFX variables
    private Scene scene;
    static boolean fistTimeLaunch = false;
    private static Label dropped = new Label("Waiting...");
    private static Button btnCombine = new Button("Combine");
    private Button btnPreview = new Button("Preview");
    private Button btnRefreshListView = new Button("Refresh View");
    private Button btnClear = new Button("Reset");
    private Button btnDuplicate = new Button("Duplicate");
    private Button btnHistory = new Button("History");
    private static TextField textFieldForExportFileLocation = new TextField();
    private static ListView<String> listView = new ListView<>();
    private static String lvLblDefault = "All files to be combined:";
    private static Label listViewLabel = new Label(lvLblDefault);
    private static Label lblLog = new Label("Log:\n");
    private VBox vBox = new VBox();
    private ScrollPane scrollPane;
    private static int fileCounter = 1;
    private Button btnRemoveFile = new Button("Remove");
    private Button btnMoveFile = new Button("Move");
    private static int pages = 0;
    public static File tempDir;
    public static String last;
    public static String THEME = "/css/dark-theme.css";
    static boolean styleSelected = false; //false = light and true = dark
    private MenuBar menuBar = new MenuBar();
    private static String titleAndVersion = "Combinator-inator v0.5.5";
    private static PreLoaderBox preLoaderBox = new PreLoaderBox();
    Stage stage;

    //Supported extensions
    private String[] supported = "pdf,doc*,png,jpg,ppt".split(",");

    //Database variables
    private static String SCREEN = "screen";
    private static String HISTORY = "history";
    private static String DELETE = "delete";
    private static String EXPORT_LOCATION = "export";
    private static String LAST_FILE_LOCATION = "last";
    public static String DATA = new File("").getAbsolutePath() + (new File("").getAbsolutePath().contains("\\") ? "\\src\\data\\" : "/src/data/");

    private static Thread thread = new Thread(() -> {
        Database.createDatabase(EXPORT_LOCATION);
        ExportLocationDatabase.createExportLocationTable(EXPORT_LOCATION);

        Database.createDatabase(LAST_FILE_LOCATION);
        LastFileLocationDatabase.createLastFileLocationTable(LAST_FILE_LOCATION);

        Database.createDatabase(SCREEN);
        ScreenDatabase.createScreenTable(SCREEN);

        Database.createDatabase(HISTORY);
        HistoryDatabase.createHistoryTable(HISTORY);

        Database.createDatabase(DELETE);
        DeleteFileDatabase.createTable(DELETE);
    });


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (new File(DATA).mkdir()) {
            fistTimeLaunch = true;
            setLog("Generated \"" + DATA + "\" folder for databases.");
        }

        thread.start();

        launch(args);
    }


    private List<String> stringToList(String paths) {
        return new ArrayList<>(Arrays.asList(paths.trim().substring(1, paths.length() - 1).trim().split(",")));
    }

    /**
     * When the button Combine button is pressed it will jump to this function and combine all listed PDFs
     * in the variable paths
     */
    private void btnRun() throws IOException {
        HistoryDatabase.insert(HISTORY, paths.toString());
        File[] files = new File[paths.size()];
        for (int i = 0; i < paths.size(); i++) files[i] = new File(paths.get(i));
        merge(files, true);
    }

    /**
     * When the button Combine button is pressed it will jump to this function and combine all listed PDFs
     * in the variable paths
     */
    private void btnPreview() throws IOException {
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
    private void closeProgram() throws IOException {
        boolean answer = ConfirmBox.display("Close-inator", "Are you sure you want to quit? :(");
        if (answer) {
            clear();
            storeTempFiles();
            ScreenDatabase.insert(SCREEN, scene.getWidth(), scene.getHeight());
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
        String dir = "Desktop"; //The directory to find
        String path = new File("").getAbsolutePath();
        path = path.substring(0, path.indexOf(dir) + dir.length() + 1); //plus one for the forward slash /

        //Fallback if application is ran in another folder that is not child to (or is) Desktop
        if (!new File(path).exists()) {
            //ConfirmBox.display("Set Export Location", "Application could not automatically set export location.");
            String adjusted = new File("").getAbsolutePath();
            if (adjusted.contains("/")) adjusted += "/Combined.pdf";
            else adjusted += "\\Combined.pdf";
            path = adjusted.replace("Combined.pdf", "");
        }
        return path;
    }

    public static void setLog(String log) {
        lblLog.setText(lblLog.getText() + log + "\n");
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
            newListView();
        } else {
            ConfirmBox.display("Page-remover-inator", "Attempted to edit a user file!\nFile must be in temp folder");
        }
    }

    private void removePagesInFile(File file, int start, int end) throws IOException {
        if (file.getAbsolutePath().contains(tempDir.getAbsolutePath())) {
            if (start > end) {
                ConfirmBox.display("Page-remover-inator", "Start must be smaller than end.");
                return;
            }

            int diff = (end - start) + 1;
            setLog("Remove amount: " + diff + " pages");
            PDDocument document = PDDocument.load(file);

            do {
                document.removePage(end);
                setLog("Removed page #" + (end + 1));
                end--;
            } while (end >= start);

            document.save(file);
            document.close();
            newListView();
        } else {
            ConfirmBox.display("Page-remover-inator", "Attempted to edit a user file!\nFile must be in temp folder");
        }
    }

    private void insertInBetween(int selectedIdx, int child, int firstIdx, int secondIdx) throws IOException {
        if (new File(paths.get(selectedIdx)).getAbsolutePath().startsWith(tempDir.getAbsolutePath())) {
            File parent = new File(paths.get(selectedIdx));
            File insert = new File(paths.get(child));

            //new names
            File head = new File(parent.getAbsolutePath() + "HEAD.pdf");
            File tail = new File(parent.getAbsolutePath() + "TAIL.pdf");

            //adds paths to be deleted
            delete.add(head.getAbsolutePath());
            delete.add(tail.getAbsolutePath());

            //duplicates the file twice
            FileUtils.copyFile(parent, head);
            FileUtils.copyFile(parent, tail);

            PDDocument document = PDDocument.load(parent);
            int maxIdx = document.getNumberOfPages() - 1;
            document.close();

            removePagesInFile(head, 0, firstIdx);
            removePagesInFile(tail, secondIdx, maxIdx);

            File[] files = new File[]{
                    tail,
                    insert,
                    head
            };

            merge(files, false);

            setLog(Main.last);

            paths.remove(selectedIdx);
            paths.add(selectedIdx, Main.last);

            newListView();

        } else {
            ConfirmBox.display("Insert-inator", "Cannot edit files outside TEMP folder.");
        }
    }

    private void keepPagesInFile(File file, int start, int end) throws IOException {
        if (file.getAbsolutePath().contains(tempDir.getAbsolutePath())) {
            if (start > end) {
                ConfirmBox.display("Page-remover-inator", "Start must be smaller than end.");
                return;
            }

            PDDocument document = PDDocument.load(file);
            int ending = document.getNumberOfPages() - 1;
            int starting = start - 1;
            System.out.println(starting);

            if (starting == -1) {
                document.close();
                setLog("[" + (end + 1) + ", " + (document.getNumberOfPages() + 1) + "]");
                removePagesInFile(file, end + 1, document.getNumberOfPages() - 1);
                return;
            } else if (end + 1 == document.getNumberOfPages()) {
                document.close();
                setLog("[" + (end + 1) + ", " + (document.getNumberOfPages() + 1) + "]");
                removePagesInFile(file, 0, start - 1);
                return;
            }

            do {
                document.removePage(ending);
                setLog("Removed page #" + (ending + 1));
                ending--;
            } while (ending != end);

            do {
                document.removePage(starting);
                setLog("Removed page #" + (starting + 1));
                starting--;
            } while (starting >= 0);
            document.save(file);
            document.close();
            newListView();
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
    private static void totalPages() throws IOException {
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
    private static void updateFileEstimationHeaderInformation() {
        if (paths.size() != 1 && pages != 1)
            listViewLabel.setText(lvLblDefault + " " + paths.size() + " files | " + pages + " pages");
        else if (paths.size() == 1 && pages > 1)
            listViewLabel.setText(lvLblDefault + " " + paths.size() + " file | " + pages + " pages");
        else
            listViewLabel.setText(lvLblDefault + " " + paths.size() + " file | " + pages + " page");
    }

    /**
     * @param primaryStage all styling, functionality and initial setup is in this function
     */
    @Override
    public void start(Stage primaryStage) {
        if (fistTimeLaunch) {
            try {
                Desktop.getDesktop().browse(
                        new URI("file:///" + new File("LICENSE").getAbsolutePath().replace("\\", "/")
                        )
                );
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        /*Sets the icon for the application*/
        primaryStage.getIcons().add(new Image("CombinePDF/img/android-chrome-512x512.png"));
        tempDir = new File("TEMP");
        if (tempDir.mkdirs()) {
            lblLog.setText(lblLog.getText() + "Created " + tempDir.getAbsolutePath());
        } else {
            if (!tempDir.exists()) {
                System.err.println("Could not create temporary folder.\nYou will not be abel to convert DOCX to PDF");
                setLog("An error occurred while created temporary folder.\n" +
                        "You will not be able to convert Word Documents to PDF files...");
            }
        }

        /*Information labels*/
        Label tfLabel = new Label("Export Location:");

        /*Sets the spacing for the Vertical Box and sets its color*/
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        setDefaultColor();

        /*Puts the Information labels in the Vertical Box*/
        vBox.getChildren().addAll(menuBar, listViewLabel);

        /*Handles Close Request*/
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            try {
                closeProgram();
            } catch (IOException ex) {
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
                updateListOfFiles(path);
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
                setLog("No files have been selected.");
            } else {
                btnCombine.setDisable(true);
                try {
                    btnRun();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnPreview.setOnAction(e -> {
            //Checks if user has provided files to be combined.
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.");
            } else {
                try {
                    btnPreview();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnRefreshListView.setOnAction(e -> {
            //Checks if user has provided files to be combined.
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Nothing to refresh...)");
                setLog("No files have been selected.");
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
                setLog("No files have been selected.");
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
                    setLog("Invalid input for duplicator-inator...");
                }
            }
        });

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        btnHistory.setOnAction(e -> {
            List<History> histories = HistoryDatabase.history(HISTORY, "DESC");
            if (histories == null) {
                setLog("No history.");
            } else {
                String content = HistoryBox.recover(HISTORY);
                if (content != null) {
                    if (content.equals("-Execute Order 66-")) {
                        eraseHistoryAndFiles();
                    } else {
                        List<String> files = stringToList(content);
                        if (paths.isEmpty()) {
                            files.forEach(file -> paths.add(file.trim()));
                        } else {
                            if (ConfirmBox.display("Information-inator", "Do you want to add this saved state to your current files?")) {
                                files.forEach(file -> paths.add(file.trim()));
                            }
                        }

                        try {
                            newListView();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        setLog(files + "");

                    }
                }
            }
        });

        btnRemoveFile.setOnAction(event -> {
            if (paths.isEmpty()) {
                listViewLabel.setText("All files to be combined: (Well I'm gonna need something to work with...)");
                setLog("No files have been selected.");
            } else {
                int index = RemoveBox.display(paths);

                try {
                    setLog("Removing " + paths.get(index - 1) + ".");
                    deleteItem(index - 1);
                    setLog("Removed.\n");
                } catch (Exception e) {
                    setLog("Aborted remove file..." + "\n" + e.getMessage() + "");
                }
            }
        });

        btnMoveFile.setOnAction(event -> {
            if (paths.isEmpty() || paths.size() < 2) {
                listViewLabel.setText("All files to be combined: (You must have at least two [2] files)");
                setLog("No files have been selected.");
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
                    setLog("Finished moving...");
                } else {
                    setLog("Aborted move file..." + "");
                }
            }
        });

        /*Clear button*/
        btnClear.setOnAction(e -> {
            try {
                if (paths.isEmpty()) {
                    clear();
                } else {
                    boolean answer = ConfirmBox.display("Clear-inator", "Are you sure?");
                    if (answer) clear();
                }
            } catch (IOException ignored) {
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
                                case "Remove From List":
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
                                                "");
                                        moveItem(indexes[0] - 1, indexes[1] - 1);
                                        setLog("Finished moving...");
                                    } else {
                                        setLog("Aborted move file...");
                                    }
                                    break;
                                case "Open File":
                                    openFile(new File(path));
                                    break;
                                case "Remove a Page":
                                    duplicateFile(new File(path), indexOfSelected);
                                    int pageNumber = NumberBox.display(totalNumberOfPages);

                                    if (totalNumberOfPages == 1 && pageNumber == 1) {
                                        deleteItem(indexOfSelected);
                                        break;
                                    }

                                    if (pageNumber != -1) {
                                        removePageInFile(new File(paths.get(indexOfSelected)), --pageNumber); //decrements for index of the page
                                    }

                                    break;
                                case "Remove a Range of Pages":
                                    duplicateFile(new File(path), indexOfSelected);
                                    int[] range = RangeBox.display(totalNumberOfPages, "Remove Range");

                                    if (range != null) {
                                        removePagesInFile(new File(paths.get(indexOfSelected)), --range[0], --range[1]);
                                    }

                                    break;
                                case "Keep Range of Pages":
                                    duplicateFile(new File(path), indexOfSelected);
                                    int[] keep = RangeBox.display(totalNumberOfPages, "Keep Range");

                                    if (keep != null) {
                                        keepPagesInFile(new File(paths.get(indexOfSelected)), --keep[0], --keep[1]);
                                    }

                                    break;
                                case "Insert In-Between":

                                    if (paths.size() == 1) {
                                        setLog("You must have at least two files for insertion.");
                                    }

                                    duplicateFile(new File(path), indexOfSelected);

                                    //return selected file and range
                                    int selected = SelectorBox.display(paths, indexOfSelected) - 1;

                                    if (selected != -1) {
                                        int[] sandwich = SelectRangeBox.display(totalNumberOfPages, "Select");

                                        //validates the input
                                        if (sandwich == null) {
                                            setLog("No page was selected for insertion.");
                                            break;
                                        } else if (sandwich[1] - sandwich[0] != 1) {
                                            ConfirmBox.display("Error-inator", "File must be between two pages.");
                                            setLog("Error while inserting page. File must be between two pages.");
                                            break;
                                        } else {
                                            setLog("Validation for insertion passed!");
                                        }

                                        //duplicate selected file if not temp
                                        if (!paths.get(selected).startsWith(tempDir.getAbsolutePath()))
                                            duplicateFile(new File(paths.get(selected)), selected);

                                        //insert
                                        insertInBetween(indexOfSelected, selected, --sandwich[0], --sandwich[1]);

                                        //removes the inserted file from list
                                        paths.remove(selected);

                                        //updates the list view
                                        newListView();
                                    } else {
                                        setLog("No file was selected for insertion.");
                                    }

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

        Button btnSelDirectory = new Button("Change Export Location");

        btnSelDirectory.setOnAction(e -> {
            String exportLocation = ExportLocationDatabase.location(EXPORT_LOCATION);
            if (exportLocation != null && !exportLocation.isEmpty()) {
                File el = new File(exportLocation);
                exportLocation = el.getAbsolutePath().replace(el.getName(), "");
                directoryChooser.setInitialDirectory(new File(exportLocation));
            } else {
                directoryChooser.setInitialDirectory(new File("src"));
            }
            setLog("Browsing for Directories");
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                String dir = selectedDirectory.getAbsolutePath();

                if (dir.contains("/")) {
                    dir += "/Combined.pdf";
                } else {
                    dir += "\\Combined.pdf";
                }

                textFieldForExportFileLocation.setText(dir);
                ExportLocationDatabase.insert(EXPORT_LOCATION, dir);

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

        btnHistory.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnHistory.fire();
        });

        //if the remove button is selected and the enter key is pressed it will simulate a button click
        btnRemoveFile.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) btnRemoveFile.fire();
        });

        /*Gets the dimensions of the screen*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        /*margins for all items on screen*/
        int insectsVal = 12;
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
        VBox.setMargin(btnHistory, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnRemoveFile, new Insets(0, insectsVal, insectsVal, insectsVal));
        VBox.setMargin(btnMoveFile, new Insets(0, insectsVal, insectsVal, insectsVal));

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
        hBoxBtnModificationsLayout.getChildren().addAll(btnDuplicate, btnHistory, btnPreview);
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
        hBoxSearchForFileLayout.getChildren().addAll(textFieldForExportFileLocation, btnSelDirectory);
        hBoxSearchForFileLayout.setAlignment(Pos.CENTER);
        hBoxSearchForFileLayout.setSpacing(5);
        list.addAll(listView, tfLabel, hBoxSearchForFileLayout, vBoxBtnLayout);

        StackPane root = new StackPane();
        root.getChildren().addAll(vBox);

        String[] sizes = ScreenDatabase.screen(SCREEN);

        /* *
         * Gets the last used screen size from database
         * */
        try {
            scene = new Scene(root, Double.parseDouble(sizes[0]), Double.parseDouble(sizes[1]));
        } catch (Exception e) {
            setLog("Database was empty using default values for window size.");
            scene = new Scene(root, screenSize.getWidth() / 3, screenSize.getHeight() - 100);
        }

        String exportLocation = ExportLocationDatabase.location(EXPORT_LOCATION);
        if (exportLocation != null && !exportLocation.isEmpty()) {
            textFieldForExportFileLocation.setText(exportLocation);
        } else {
            textFieldForExportFileLocation.setText(defaultDesktopLocation);
        }


        Menu fileMenu = new Menu("File");
        CheckMenuItem logFileMenuItem = new CheckMenuItem("Show Log");
        MenuItem exitFileMenuItem = new MenuItem("Exit");
        MenuItem previewFileMenuItem = new MenuItem("Preview");
        MenuItem resetFileMenuItem = new MenuItem("Reset");
        MenuItem combineFileMenuItem = new MenuItem("Combine");
        MenuItem importFileMenuItem = new MenuItem("Import");
        Menu styleFileMenuItem = new Menu("Style");
        fileMenu.getItems().addAll(
                importFileMenuItem,
                new SeparatorMenuItem(), previewFileMenuItem, combineFileMenuItem, resetFileMenuItem,
                new SeparatorMenuItem(), styleFileMenuItem,
                new SeparatorMenuItem(), logFileMenuItem,
                new SeparatorMenuItem(), exitFileMenuItem
        );

        Menu editMenu = new Menu("Edit");
        MenuItem historyEditMenuItem = new MenuItem("History");
        MenuItem removeEditMenuItem = new MenuItem("Remove");
        MenuItem moveEditMenuItem = new MenuItem("Move");
        MenuItem duplicateEditMenuItem = new MenuItem("Duplicate");
        MenuItem changeExportLocationEditMenuItem = new MenuItem("Change Export Location");
        MenuItem refreshViewEditMenuItem = new MenuItem("Refresh View");
        MenuItem[] editMenuItems = {
                moveEditMenuItem, removeEditMenuItem, duplicateEditMenuItem,
                new SeparatorMenuItem(), historyEditMenuItem,
                new SeparatorMenuItem(), refreshViewEditMenuItem, changeExportLocationEditMenuItem
        };

        for (MenuItem item : editMenuItems) editMenu.getItems().add(item);

        RadioMenuItem choice1Item = new RadioMenuItem("Light");
        RadioMenuItem choice2Item = new RadioMenuItem("Dark");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(choice1Item);
        toggleGroup.getToggles().add(choice2Item);
        styleFileMenuItem.getItems().addAll(choice1Item, choice2Item);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutHelpMenuItem = new MenuItem("How to...");
        helpMenu.getItems().add(aboutHelpMenuItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        //EventHandler<ActionEvent> menuEvent = e -> setLog(((MenuItem)e.getSource()).getText() + " selected.");

        FileChooser fileChooser = new FileChooser();

        importFileMenuItem.setOnAction(e -> {
            String lastKnownLocation = LastFileLocationDatabase.location(LAST_FILE_LOCATION);

            if (lastKnownLocation != null && !lastKnownLocation.isEmpty()) {
                fileChooser.setInitialDirectory(new File(lastKnownLocation));
            } else {
                fileChooser.setInitialDirectory(new File("src"));
            }

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                updateListOfFiles("[" + selectedFile + "]");

                String saveLastLocation = selectedFile.getAbsolutePath().replace(selectedFile.getName(), "");
                LastFileLocationDatabase.insert(LAST_FILE_LOCATION, saveLastLocation);

            } else {
                setLog("No file was selected.");
            }
        });

        logFileMenuItem.setOnAction(event -> {
            if (scrollPane.isVisible()) {
                scrollPane.setVisible(false);
            } else {
                scrollPane.setVisible(true);
            }
        });

        exitFileMenuItem.setOnAction(e -> {
            try {
                closeProgram();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        previewFileMenuItem.setOnAction(e -> btnPreview.fire());
        resetFileMenuItem.setOnAction(e -> btnClear.fire());
        combineFileMenuItem.setOnAction(e -> btnCombine.fire());

        styleFileMenuItem.setOnAction(e -> {
            Object[] array = toggleGroup.getToggles().toArray();
            if (array[0].toString().contains("selected")) {
                scene.getStylesheets().clear();
                styleSelected = false;
                setLog("Changed theme to 'Light'");
            } else if (array[1].toString().contains("selected")) {
                scene.getStylesheets().add(THEME);
                styleSelected = true;
                setLog("Changed theme to 'Dark'");
            }
        });

        historyEditMenuItem.setOnAction(e -> btnHistory.fire());
        removeEditMenuItem.setOnAction(e -> btnRemoveFile.fire());
        moveEditMenuItem.setOnAction(e -> btnMoveFile.fire());
        duplicateEditMenuItem.setOnAction(e -> btnDuplicate.fire());
        changeExportLocationEditMenuItem.setOnAction(e -> btnSelDirectory.fire());
        refreshViewEditMenuItem.setOnAction(e -> btnRefreshListView.fire());

        String help = new File("").getAbsolutePath() +
                (new File("").getAbsolutePath().contains("\\")
                        ? "\\src\\CombinePDF\\help\\index.html"
                        : "/src/CombinePDF/help/index.html");

        aboutHelpMenuItem.setOnAction(e -> {
            setLog("Opening file: \"" + help + "\"");
            openFile(new File(help));
        });

        if (styleSelected) {
            scene.getStylesheets().add(THEME);
        }

        primaryStage.setTitle(titleAndVersion);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(567d);
        primaryStage.setMinWidth(526d);
        primaryStage.show();
        this.stage = primaryStage;
    }

    private void updateListOfFiles(String path) {
        //Removes unnecessary characters []
        path = path.substring(1, path.length() - 1);

        /*Checks to see if multiple files were dropped at once*/
        if (path.contains(",")) {
            //splits all the file paths into a string array
            String[] arrPath = path.split(",");

            //removes spaces in front and before of the string(path)
            for (int i = 0; i < arrPath.length; i++) arrPath[i] = arrPath[i].trim();

            String extension;
            String newName;
            String originalName;
            for (int i = 0; i < arrPath.length; i++) {
                extension = arrPath[i];
                if (extension.contains("docx") || arrPath[i].contains("doc")) {
                    originalName = new File(extension).getName();
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
                    originalName = new File(extension).getName();
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
                } else if (extension.contains("ppt")) {
                    try {
                        newName = Convert.PPTtoPDF(extension);
                        delete.add(newName);
                        arrPath[i] = newName;
                    } catch (IOException e) {
                        e.printStackTrace();
                        setLog("Error converting ppt to PDF");
                    }
                }
            }

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
            } else if (substring.contains("ppt")) {
                try {
                    path = Convert.PPTtoPDF(path);
                    delete.add(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    setLog("Error converting ppt to PDF");
                }
            }

            if (path.substring(path.length() - 4).contains("pdf")) {
                paths.add(path);
                fileCounter++;
            } else {
                ConfirmBox.display("Not-Supported-inator",
                        "Sorry but " + new File(path).getName() +
                                " is not supported and was ignored.\n" +
                                "List of supported files " + Arrays.toString(supported) + "\n* Not on Mac/Linux");
            }
        }

        try {
            newListView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Where the magic happens!
     *
     * @param files to be merged/combined
     */
    public static void merge(File[] files, boolean combine) throws IOException {
        try {
            //Loading an existing PDF document
            PDDocument[] docs = new PDDocument[files.length];

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) docs[i] = PDDocument.load(files[i]);
            }

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

            //Merging the two documents
            //noinspection deprecation
            PDFMerger.mergeDocuments();

            if (combine) clear();

            //Closing the documents
            for (PDDocument document : docs) document.close();


            if (combine) storeTempFiles();

            dropped.setText("Documents merged!");
            setLog("Finished!");
        } catch (IOException e) {
            clear();
            e.printStackTrace();
            setLog(e.getMessage());
        }
    }

    /**
     * Clears and resets all variables to their initially given values
     */
    private static void clear() throws IOException {
        listViewLabel.setText("All files to be combined:");
        lblLog.setText("Log:\n");
        btnCombine.setDisable(false);
        paths.clear();
        listView.getItems().clear();
        fileCounter = 1;
        pages = 0;
        storeTempFiles();
        newListView();
    }

    /**
     * Stores all temporally created PDF files in the local TEMP folder
     */
    private static void storeTempFiles() {
        delete.forEach(e -> DeleteFileDatabase.insert(DELETE, e));
    }


    /**
     * Deletes a file.
     * @param pathToDelete the path to the file
     * @return returns if the file was deleted
     */
    public static boolean deleteFile(String pathToDelete) {
        if (pathToDelete.contains(tempDir.getAbsolutePath()) || pathToDelete.contains(HistoryDatabase.path)) {
            return new File(pathToDelete).delete();
        } else {
            ConfirmBox.display("Uh-Oh", "Your files are SAFE.\nThe application almost deleted one of your personal files.\n" +
                    "This shouldn't have happened...\n" + pathToDelete);
            return false;
        }
    }

    /**
     * Deletes all temp files and resets the history and delete databases
     */
    private void eraseHistoryAndFiles() {
        HashSet<String> filesToDelete = DeleteFileDatabase.files(DELETE);

        if (filesToDelete != null) {
            filesToDelete.forEach(e -> {
                if (deleteFile(e)) setLog(e + " was deleted.");
            });
        } else {
            setLog("No temporary files were found.");
        }

        resetDatabases();

        List<String> tempFiles = DirectoryFiles.listFiles(tempDir.getAbsolutePath());

        if (tempFiles != null) {
            if (!tempFiles.isEmpty()) {
                tempFiles.forEach(e -> {
                    String name = new File(e).getName();
                    if (new File(e).delete()) {
                        setLog(name + " was deleted.");
                    }
                });
            }
        }
    }

    private void resetDatabases() {
        DeleteFileDatabase.deleteTable(DELETE);
        HistoryDatabase.deleteHistoryTable(HISTORY);
        deleteFile(HistoryDatabase.path + HISTORY);
        deleteFile(DeleteFileDatabase.path + DELETE);

        Database.createDatabase(HISTORY);
        HistoryDatabase.createHistoryTable(HISTORY);

        Database.createDatabase(DELETE);
        DeleteFileDatabase.createTable(DELETE);
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
    private static void newListView() throws IOException {
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
