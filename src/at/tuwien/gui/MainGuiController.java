package at.tuwien.gui;

import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.MainGuiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable{

    private IMainGuiService mainGuiService;

    @FXML
    public TextArea taCNL;
    @FXML
    public TextArea taError;
    @FXML
    public TextArea taASP;
    @FXML
    public TextArea taModels;
    @FXML
    public TextField tfFilter;
    @FXML
    public Button btnSolve;
    @FXML
    public MenuItem miOpenFile;
    @FXML
    public MenuItem miSaveFile;
    @FXML
    public WebView wvSentencePatterns;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainGuiService = new MainGuiService();
        try {
            loadSentencePatterns();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void btnSolveClicked(ActionEvent actionEvent) {
        taModels.setText("");

        List<String> models = mainGuiService.solve(taASP.getText(),tfFilter.getText());

        if(models.size() == 1){
            taModels.setText(String.format("%d model found.%n%n", models.size()));
        }
        else {
            taModels.setText(String.format("%d models found.%n%n", models.size()));
        }

        int modelNumber = 1;
        for (String model: models) {
            model = model.replaceAll("\\.\n", ", ");
            if(model.contains(",")) {
                model = model.substring(0, model.lastIndexOf(", "));
            }
            taModels.appendText(String.format("Model %d: {%s}%n", modelNumber, model));
            modelNumber ++;
        }
    }

    public void tfCnlOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getText().equals(".") ||
                (taCNL.getCaretPosition() <= taCNL.getText().lastIndexOf('.') && !keyEvent.getText().equals("")) ||
                (keyEvent.getText().equals("v") && (keyEvent.isMetaDown() || keyEvent.isControlDown())) ||  // check CMD + V
                keyEvent.getCode().equals(KeyCode.BACK_SPACE) || keyEvent.getCode().equals(KeyCode.DELETE))
        {
            translate();
        }
    }

    Thread thread;

    private void translate() {

        TranslatorThread translatorThread;

        translatorThread = new TranslatorThread(taCNL,taError,taASP);

        if(thread == null)
        {
            thread = new Thread(translatorThread);
            thread.start();
        } else {
            thread.stop();
            thread = new Thread(translatorThread);
            thread.start();
        }
    }

    public void openFileClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CNL problem description");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );

        Stage stage = (Stage) btnSolve.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }

    private void openFile(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            taCNL.setText("");

            for (String line: lines) {
                taCNL.appendText(line + "\n");
            }

            translate();
        } catch (IOException e) {
            taError.appendText(e.getMessage());
        }
    }

    public void saveFileClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CNL problem description");

        Stage stage = (Stage) btnSolve.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {

            String path = file.getPath();
            if(!file.getPath().endsWith(".txt"))
            {
                path += ".txt";
            }

            try(  PrintWriter out = new PrintWriter( path) ){
                out.println( taCNL.getText() );
            } catch (FileNotFoundException e) {
                taError.appendText(e.getMessage());
            }
        }
    }

    private void loadSentencePatterns() throws URISyntaxException {

        WebEngine webEngine = wvSentencePatterns.getEngine();

        File f = new File(getClass().getResource("sentences.htm").toURI());
        webEngine.load(f.toURI().toString());
    }

    public void addWordClicked(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_word.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnSolve.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 391, 59));
            stage.setTitle("add word to dictionary");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDictionaryClicked(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("directory.fxml"));

            Stage stage = new Stage();

            /* block parent window */
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnSolve.getScene().getWindow());

            /* set the scene */
            stage.setScene(new Scene(loader.load(), 538, 400));
            stage.setTitle("Dictionary");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
