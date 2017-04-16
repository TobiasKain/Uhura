package at.tuwien.GUI;

import at.tuwien.CNL2ASP.Translation;
import at.tuwien.Service.MainGuiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable{

    private MainGuiService mainGuiService;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainGuiService = new MainGuiService();
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
            model = model.substring(0,model.lastIndexOf(", "));

            taModels.appendText(String.format("Model %d: {%s}%n", modelNumber, model));
            modelNumber ++;
        }
    }

    public void tfCnlOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getText().equals(".") ||
                (taCNL.getCaretPosition() <= taCNL.getText().lastIndexOf('.') && !keyEvent.getText().equals("")) ||
                (keyEvent.getText().equals("v") && (keyEvent.isMetaDown() || keyEvent.isControlDown())))    // check CMD + V
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

        /*
        taASP.setText("");
        taError.setText("");

        Translation translation = mainGuiService.translate(taCNL.getText());
        taASP.setText(translation.getAspCode());

        for (String error : translation.getErrors()) {
            taError.appendText(error + "\n");
        }*/
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
        } catch (IOException x) {
            taError.appendText(x.getMessage());
        }
    }
}
