package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.Word;
import at.tuwien.entity.WordType;
import at.tuwien.service.IDirectoryService;
import at.tuwien.service.impl.DirectoryService;
import com.sun.javafx.scene.control.TableColumnSortTypeWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public class DictionaryController implements Initializable {

    @FXML
    public TableView<Word> tvWords;
    @FXML
    public TableColumn<Word,Long> tcId;
    @FXML
    public TableColumn<Word,String> tcWord;
    @FXML
    public TableColumn<Word,WordType> tcWordType;

    private ObservableList<Word> words = FXCollections.emptyObservableList();

    private IDirectoryService directoryService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            directoryService = new DirectoryService();
        } catch (DaoException e) {
            e.printStackTrace();
        }

        try {
            initializeTableView();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private void initializeTableView() throws DaoException {

        tvWords.setPlaceholder(new Label("Directory has no entries."));

        tcId.setCellValueFactory(new PropertyValueFactory<>("wordId"));
        tcWord.setCellValueFactory(new PropertyValueFactory<>("word"));
        tcWordType.setCellValueFactory(new PropertyValueFactory<>("wordType"));

        MenuItem menuItemDelete = new MenuItem("delete");
        menuItemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Word word = words.get(tvWords.getSelectionModel().getSelectedIndex());

                if (word != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Delete Entry");
                    alert.setHeaderText("Delete Entry?");
                    alert.setContentText(String.format("Are you sure you want to delete entry (%s,%s)?", word.getWord(), word.getWordType()));

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeYes) {
                        try {
                            directoryService.deleteWord(word);
                            loadData();
                        } catch (DaoException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        tvWords.setContextMenu(new ContextMenu(menuItemDelete));

        loadData();
    }

    private void loadData(){
        try {
            words = FXCollections.observableArrayList(directoryService.getAllWords());
        } catch (DaoException e) {
            e.printStackTrace();
        }
        tvWords.setItems(words);
    }


    private void closeStage() {
        Stage stage = (Stage) tvWords.getScene().getWindow();
        stage.close();
    }
}
