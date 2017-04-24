package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.service.impl.TranslationPatternService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class AddTranslationPatternController {

    @FXML
    public TextArea taTranslation;
    @FXML
    public TextField tfNlSentence;

    private TranslationPatternService translationPatternService;

    public AddTranslationPatternController() {
        try {
            translationPatternService = new TranslationPatternService();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    public void btnAddClicked(ActionEvent actionEvent) {
        TranslationPattern translationPattern = new TranslationPattern();

        translationPattern.setNlSentence(tfNlSentence.getText());
        translationPattern.setTranslation(taTranslation.getText());

        try {
            if(checkInput()) {
                translationPatternService.addTranslationPattern(translationPattern);
                closeStage();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private boolean checkInput(){
        if(tfNlSentence.getText().length() > 1024){
            ErrorDialog.showErrorDialog("Natural language sentence is too long.");
            return false;
        }
        if(!tfNlSentence.getText().matches(".*\\.$")){
            ErrorDialog.showErrorDialog("Natural language sentence has to end with '.'.");
            return false;
        }
        if(taTranslation.getText().length() > 1024){
            ErrorDialog.showErrorDialog("Translation is too long.");
            return false;
        }

        return true;
    }

    private void closeStage() {
        Stage stage = (Stage) tfNlSentence.getScene().getWindow();
        stage.close();
    }
}
