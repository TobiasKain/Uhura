package at.tuwien.gui;

import at.tuwien.dlv.DLVException;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.MainGuiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by tobiaskain on 27/04/2017.
 */
public class TranslationTabController implements Initializable{

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
    public Button btnTranslate;
    @FXML
    public TabPane infoTabs;
    @FXML
    public WebView wvSentencePatterns;

    private File file;

    private IMainGuiService mainGuiService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadSentencePatterns();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }

    public void btnTranslateClicked(ActionEvent actionEvent) {
        translate();
    }

    public void btnSolveClicked(ActionEvent actionEvent) {
        taModels.setText("");


        List<String> models = null;
        try {
            models = mainGuiService.solve(taASP.getText(),tfFilter.getText());

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
        } catch (DLVException e) {
            taModels.setText(e.getMessage());
        }


    }

    public void tfCnlOnKeyPressed(KeyEvent keyEvent) {
        if(mainGuiService.getTranslationType() == TranslationType.AUTOMATIC) {
            if (keyEvent.getText().equals(".") ||
                    (taCNL.getCaretPosition() <= taCNL.getText().lastIndexOf('.') && !keyEvent.getText().equals("")) ||
                    (keyEvent.getText().equals("v") && (keyEvent.isMetaDown() || keyEvent.isControlDown())) ||  // check CMD + V
                    keyEvent.getCode().equals(KeyCode.BACK_SPACE) || keyEvent.getCode().equals(KeyCode.DELETE)) {
                translate();
            }
        }
    }

    Thread thread;

    public void translate() {

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

    private void loadSentencePatterns() throws URISyntaxException {

        WebEngine webEngine = wvSentencePatterns.getEngine();

        File f = new File(getClass().getResource("sentences.htm").toURI());
        webEngine.load(f.toURI().toString());
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
