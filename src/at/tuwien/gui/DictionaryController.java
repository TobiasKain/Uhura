package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.Word;
import at.tuwien.entity.WordType;
import at.tuwien.service.IDirectoryService;
import at.tuwien.service.impl.DirectoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public class DictionaryController implements Initializable {

    @FXML
    public TextField tfWord;
    @FXML
    public ChoiceBox cbWordType;

    private IDirectoryService directoryService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            directoryService = new DirectoryService();
        } catch (DaoException e) {
            e.printStackTrace();
        }

        cbWordType.getItems().setAll(WordType.values());
        cbWordType.getSelectionModel().select(0);
    }

    public void btnAddWordClicked(ActionEvent actionEvent) throws DaoException {

        Word word = new Word();
        word.setWord(tfWord.getText());
        word.setWordType((WordType)cbWordType.getValue());

        directoryService.addWord(word);

        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) tfWord.getScene().getWindow();
        stage.close();
    }
}
