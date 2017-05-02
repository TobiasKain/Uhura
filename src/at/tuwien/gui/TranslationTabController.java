package at.tuwien.gui;

import at.tuwien.dlv.DLVException;
import at.tuwien.entity.asp.Translation;
import at.tuwien.service.IMainGuiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by tobiaskain on 27/04/2017.
 */
public class TranslationTabController implements Initializable{

    @FXML
    public TextArea taError;
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
    @FXML
    public StackPane spCNL;
    @FXML
    public StackPane spASP;

    public CodeArea caCNL;
    public CodeArea caASP;

    private File file;

    private IMainGuiService mainGuiService;
    private TranslationTabController translationTabController;

    public TranslationTabController() {
        translationTabController = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        caCNL = new CodeArea();
        caCNL.setParagraphGraphicFactory(LineNumberFactory.get(caCNL));
        caCNL.setOnKeyPressed(this::tfCnlOnKeyPressed);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem miTranslate = new MenuItem("manually translate sentence");
        miTranslate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String sentence = caCNL.getSelectedText();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("add_manual_translation.fxml"));

                    Stage stage = new Stage();

                    /* block parent window */
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(btnSolve.getScene().getWindow());

                    /* set the scene */
                    stage.setScene(new Scene(loader.load(), 411, 206));

                    stage.setTitle("Add Manual Translation");

                    AddManualTranslationController addManualTranslationController = (AddManualTranslationController) loader.getController();
                    addManualTranslationController.tfCNlSentence.setText(sentence);
                    addManualTranslationController.setMainGuiService(mainGuiService);
                    addManualTranslationController.setTranslationTabController(translationTabController);

                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenu.getItems().add(miTranslate);
        caCNL.setContextMenu(contextMenu);

        spCNL.getChildren().add(new VirtualizedScrollPane<>(caCNL));

        caASP = new CodeArea();
        caASP.setParagraphGraphicFactory(LineNumberFactory.get(caASP));
        spASP.getChildren().add(new VirtualizedScrollPane<>(caASP));

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
            models = mainGuiService.solve(caASP.getText(),tfFilter.getText());

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
                    (caCNL.getCaretPosition() <= caCNL.getText().lastIndexOf('.') && !keyEvent.getText().equals("")) ||
                    (keyEvent.getText().equals("v") && (keyEvent.isMetaDown() || keyEvent.isControlDown())) ||  // check CMD + V
                    keyEvent.getCode().equals(KeyCode.BACK_SPACE) || keyEvent.getCode().equals(KeyCode.DELETE)) {
                translate();
            }
        }
    }

    Thread thread;

    public void translate() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                caASP.replaceText("");
                taError.setText("");

                Translation translation = null;
                try {
                    translation = mainGuiService.translate(caCNL.getText());

                    caASP.replaceText(translation.getAspCode());

                    for (String error : translation.getErrors()) {
                        taError.appendText(error + "\n");
                    }
                } catch (DLVException e) {
                    taError.appendText(e.getMessage());
                }
            }
        });

       /* TranslatorThread translatorThread;

        translatorThread = new TranslatorThread(caCNL,taError, caASP);

        if(thread == null)
        {
            thread = new Thread(translatorThread);
            thread.start();
        } else {
            thread.stop();
            thread = new Thread(translatorThread);
            thread.start();
        }*/
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
