package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.Word;
import at.tuwien.entity.WordType;
import at.tuwien.service.IDirectoryService;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.DirectoryService;
import edu.stanford.nlp.process.WordToTaggedWordProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tobiaskain on 02/05/2017.
 */
public class EditWordController implements Initializable{
    @FXML
    public TextField tfWord;
    @FXML
    public ChoiceBox cbWordType;

    private IDirectoryService directoryService;
    private IMainGuiService mainGuiService;
    private Word word;
    private DictionaryController dictionaryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            directoryService = new DirectoryService();
        } catch (DaoException e) {
            e.printStackTrace();
        }

        cbWordType.getItems().setAll(WordType.values());
    }

    public void btnUpdateWordClicked(ActionEvent actionEvent) throws DaoException {

        word.setWord(tfWord.getText());
        word.setWordType((WordType)cbWordType.getValue());

        if(checkInput()) {
            directoryService.updateWord(word);

            closeStage();

            mainGuiService.updateDirectory();
            dictionaryController.loadData();
        }
    }

    private boolean checkInput(){
        if(tfWord.getText().isEmpty()){
            ErrorDialog.showErrorDialog("Word field is empty.");
            return false;
        }
        if(tfWord.getText().length() > 255){
            ErrorDialog.showErrorDialog("Word is too long.");
            return false;
        }

        return true;
    }

    private void closeStage() {
        Stage stage = (Stage) tfWord.getScene().getWindow();
        stage.close();
    }

    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }

    public void setWord(Word word) {
        this.word = word;
        tfWord.setText(word.getWord());
        cbWordType.getSelectionModel().select(word.getWordType().ordinal());
    }

    public void setDictionaryController(DictionaryController dictionaryController) {
        this.dictionaryController = dictionaryController;
    }
}
