package at.tuwien.gui;

import at.tuwien.dlv.DLVException;
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
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.*;
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

    private Tab tab;
    private File file;
    private String initialCNLContent ="";
    private String tabLabel;

    private IMainGuiService mainGuiService;
    private TranslationTabController translationTabController;

    public TranslationTabController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        translationTabController = this;
        caCNL = new CodeArea();
        caCNL.setParagraphGraphicFactory(LineNumberFactory.get(caCNL));
        caCNL.setOnKeyReleased(this::tfCnlOnKeyReleased);

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

        spCNL.getChildren().add(0,new VirtualizedScrollPane<>(caCNL));

        caASP = new CodeArea();
        caASP.setParagraphGraphicFactory(LineNumberFactory.get(caASP));
        spASP.getChildren().add(0, new VirtualizedScrollPane<>(caASP));

        try {
            loadSentencePatterns();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void tfCnlOnKeyReleased(KeyEvent keyEvent) {

        if(!caCNL.getText().equals(initialCNLContent) &&
                (!keyEvent.isShortcutDown() || (keyEvent.getText().equals("v") && (keyEvent.isMetaDown())))){
            highlightTabLabel(true);
        } else {
            highlightTabLabel(false);
        }

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


        translationTabController.startTranslation();

        TranslatorThread translatorThread;

        translatorThread = new TranslatorThread(this,caCNL.getText());

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

    public void updateCaAspAsync(String s){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                caASP.replaceText(s);
            }
        });
    }

    public void updateTaErrorAsync(String s){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                taError.setText(s);
            }
        });
    }

    public void appendTaErrorAsync(String s){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                taError.appendText(s);
            }
        });
    }

    private void loadSentencePatterns() throws IOException {

        WebEngine webEngine = wvSentencePatterns.getEngine();

        InputStream inputStream = this.getClass().getResourceAsStream("sentences.htm");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String htmlSource ="";
        String line = "";
        while ((line = bufferedReader.readLine()) != null)
        {
            htmlSource += line;
        }

        webEngine.loadContent(htmlSource);
    }

    public void startTranslation(){
        btnTranslate.setDisable(true);
        btnTranslate.setText("Translating ...");
    }

    public void endTranslationAsync() {
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                btnTranslate.setDisable(false);
                btnTranslate.setText("Translate");
            }
        });
    }

    public void highlightTabLabel(boolean highlight){
        if(!tab.getText().equals(""))
        {
            tabLabel = tab.getText();
            tab.setText("");
        }

        tab.setGraphic(new Label(tabLabel));
        if(highlight == true) {
            tab.getGraphic().setStyle("-fx-text-fill: #0032B2;");
        }else {
            tab.getGraphic().setStyle("-fx-text-fill: black;");
        }
    }

    public boolean hasCnlContentChanged(){
        if(caCNL.getText().equals(initialCNLContent)){
            return false;
        }

        return true;
    }


    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        initialCNLContent = caCNL.getText();
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public Tab getTab() {
        return tab;
    }

    public void setInitialCNLContent(String initialCNLContent) {
        this.initialCNLContent = initialCNLContent;
    }
}
