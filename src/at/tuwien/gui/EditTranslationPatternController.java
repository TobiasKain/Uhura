package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.TranslationPatternService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class EditTranslationPatternController {

    @FXML
    public TextArea taTranslation;
    @FXML
    public TextField tfNlSentence;

    private TranslationPatternService translationPatternService;
    private IMainGuiService mainGuiService;
    private TranslationPatternsController translationPatternsController;
    private TranslationPattern translationPattern;

    public EditTranslationPatternController() {
        try {
            translationPatternService = new TranslationPatternService();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    public void btnUpdateClicked(ActionEvent actionEvent) {

        translationPattern.setNlSentence(tfNlSentence.getText());
        translationPattern.setTranslation(taTranslation.getText());

        try {
            if(checkInput()) {
                translationPatternService.updateTranslationPattern(translationPattern);
                closeStage();
                mainGuiService.updatedTranslationPatterns();
                translationPatternsController.loadData();
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

    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }

    public void setTranslationPatternsController(TranslationPatternsController translationPatternsController) {
        this.translationPatternsController = translationPatternsController;
    }

    public void setTranslationPattern(TranslationPattern translationPattern) {
        this.translationPattern = translationPattern;
        tfNlSentence.setText(translationPattern.getNlSentence());
        taTranslation.setText(translationPattern.getTranslation());
    }
}
