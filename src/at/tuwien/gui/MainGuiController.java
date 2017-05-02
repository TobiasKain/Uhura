package at.tuwien.gui;

import at.tuwien.entity.asp.Translation;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.MainGuiService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable{

    @FXML
    public MenuItem miOpenFile;
    @FXML
    public MenuItem miSaveFile;
    @FXML
    public MenuItem miManualTranslations;
    @FXML
    public TabPane tabPane;


    public HashMap<Tab,TranslationTabController> tabTranslationTabControllerHashMap;

    private static int tabCount = 1;

    private IMainGuiService mainGuiService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabTranslationTabControllerHashMap = new HashMap<>();
        mainGuiService = new MainGuiService();
        mainGuiService.setTranslationType(TranslationType.MANUAL);

        createNewTab();
    }

    public void openFileClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CNL problem description");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );

        Stage stage = (Stage) tabPane.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }

    private void openFile(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            getControllerOfSelectedTab().caCNL.replaceText("");

            for (String line: lines) {
                getControllerOfSelectedTab().caCNL.appendText(line + "\n");
            }

            getSelectedTab().setText(file.getName());
            getControllerOfSelectedTab().setFile(file);

            getControllerOfSelectedTab().translate();
        } catch (IOException e) {
            getControllerOfSelectedTab().taError.appendText(e.getMessage());
        }
    }

    public void saveFileClicked(ActionEvent actionEvent) {
        if(getControllerOfSelectedTab().getFile() != null)
        {
            saveCLN(getControllerOfSelectedTab().getFile());
        }
        else {
            File file = showSaveDialog();
            saveCLN(file);
        }
    }

    public void saveAsFileClicked(ActionEvent actionEvent) {
        File file = showSaveDialog();
        saveCLN(file);
    }

    private File showSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CNL problem description");

        Stage stage = (Stage) tabPane.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);

        return file;
    }

    private void saveCLN(File file){

        if (file != null) {

            String path = file.getPath();
            if(!file.getPath().endsWith(".txt"))
            {
                path += ".txt";
            }

            try(  PrintWriter out = new PrintWriter( path) ){
                out.println( getControllerOfSelectedTab().caCNL.getText() );
            } catch (FileNotFoundException e) {
                getControllerOfSelectedTab().taError.appendText(e.getMessage());
            }

            getSelectedTab().setText(file.getName());
        }
    }

    public void addWordClicked(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_word.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 391, 59));
            stage.setTitle("add word to dictionary");

            stage.show();

            AddWordController addWordController = (AddWordController) loader.getController();
            addWordController.setMainGuiService(mainGuiService);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mainGuiService.updateDirectory();
    }

    public void openDictionaryClicked(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("directory.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 538, 400));
            stage.setTitle("Dictionary");

            stage.show();

            DictionaryController dictionaryController = (DictionaryController) loader.getController();
            dictionaryController.setMainGuiService(mainGuiService);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainGuiService.updateDirectory();

    }

    public void rmiAutomaticTranslationSelected(ActionEvent actionEvent) {
        mainGuiService.setTranslationType(TranslationType.AUTOMATIC);
    }

    public void rmiManualTranslationSelected(ActionEvent actionEvent) {
        mainGuiService.setTranslationType(TranslationType.MANUAL);
    }

    public void addTranslationPatternClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_translation_pattern.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 411, 206));
            stage.setTitle("add translation pattern");

            stage.show();

            AddTranslationPatternController addTranslationPatternController = (AddTranslationPatternController) loader.getController();
            addTranslationPatternController.setMainGuiService(mainGuiService);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mainGuiService.updatedTranslationPatterns();
    }

    public void showTranslationPatternsClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("translation_patterns.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 900, 400));
            stage.setTitle("Translation Patterns");

            stage.show();

            TranslationPatternsController translationPatternsController = (TranslationPatternsController) loader.getController();
            translationPatternsController.setMainGuiService(mainGuiService);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mainGuiService.updatedTranslationPatterns();
    }

    public void showManualTranslationsClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manual_translations.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 900, 400));
            stage.setTitle("Manual Translations");

            stage.show();

            ManualTranslationsController manualTranslationsController = (ManualTranslationsController) loader.getController();
            manualTranslationsController.setMainGuiService(mainGuiService);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportASPClicked(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export ASP program");

        Stage stage = (Stage) tabPane.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {

            String path = file.getPath();
            if(!file.getPath().endsWith(".dl"))
            {
                path += ".dl";
            }

            try(  PrintWriter out = new PrintWriter( path) ){
                out.println( getControllerOfSelectedTab().caASP.getText() );
            } catch (FileNotFoundException e) {
                getControllerOfSelectedTab().taError.appendText(e.getMessage());
            }
        }
    }


    private boolean firstStart = true;  // Workaround: addTabClicked is called before initialize => e.g. translationTabControllerList == NULL
    public void addTabClicked(Event event) {
        if(!firstStart) {
            createNewTab();
        }
        firstStart = false;
    }

    public void createNewTab(){
        try {
            Tab tab = new Tab(String.format("new Tab (%d)",tabCount++));
            FXMLLoader fxmlLoader = new FXMLLoader();
            Node n = fxmlLoader.load(getClass().getResource("translation_tab.fxml").openStream());

            TranslationTabController translationTabController = (TranslationTabController) fxmlLoader.getController();
            translationTabController.setMainGuiService(mainGuiService);
            tabTranslationTabControllerHashMap.put(tab,translationTabController);

            tab.setContent(n);
            tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
            tabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TranslationTabController getControllerOfSelectedTab(){
        return tabTranslationTabControllerHashMap.get(tabPane.getSelectionModel().getSelectedItem());
    }

    private Tab getSelectedTab(){
        return tabPane.getSelectionModel().getSelectedItem();
    }
}
