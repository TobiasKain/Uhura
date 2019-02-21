package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.ManualTranslation;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.ManualTranslationService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by tobiaskain on 01/05/2017.
 */
public class AddManualTranslationController {
    @FXML
    public TextField tfCNlSentence;
    @FXML
    public TextArea taASP;

    private ManualTranslationService manualTranslationService;
    private IMainGuiService mainGuiService;
    private TranslationTabController translationTabController;

    public AddManualTranslationController() {
        try {
            manualTranslationService = new ManualTranslationService();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    public void btnAddClicked(ActionEvent actionEvent) {
        ManualTranslation manualTranslation = new ManualTranslation();
        manualTranslation.setCnlSentence(tfCNlSentence.getText());
        manualTranslation.setAspRule(taASP.getText());

        if(checkInput()){
            try {
                manualTranslationService.addManualTranslation(manualTranslation);
                mainGuiService.updateManualTranslation();
                translationTabController.translate();
                closeStage();
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkInput(){
        if(tfCNlSentence.getText().length() > 1024){
            ErrorDialog.showErrorDialog("Controlled Natural Language sentence is too long.");
            return false;
        }
        if(taASP.getText().isEmpty()){
            ErrorDialog.showErrorDialog("Controlled Natural Language sentence is empty.");
            return false;
        }
        if(!tfCNlSentence.getText().matches(".*\\.$")){
            ErrorDialog.showErrorDialog("Controlled Natural language sentence has to end with '.'.");
            return false;
        }
        if(taASP.getText().isEmpty()){
            ErrorDialog.showErrorDialog("ASP rule is empty.");
            return false;
        }
        if(taASP.getText().length() > 1024){
            ErrorDialog.showErrorDialog("ASP rule is too long.");
            return false;
        }

        return true;
    }


    private void closeStage() {
        Stage stage = (Stage) tfCNlSentence.getScene().getWindow();
        stage.close();
    }

    public void setTranslationTabController(TranslationTabController translationTabController) {
        this.translationTabController = translationTabController;
    }

    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }


}
