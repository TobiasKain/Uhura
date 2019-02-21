package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.ITranslationPatternService;
import at.tuwien.service.impl.TranslationPatternService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class TranslationPatternsController implements Initializable{

    @FXML
    public TableView<TranslationPattern> tvTranslationPatterns;
    @FXML
    public TableColumn<TranslationPattern,Long> tcId;
    @FXML
    public TableColumn<TranslationPattern,String> tcNlSentence;
    @FXML
    public TableColumn<TranslationPattern,String> tcRegexPattern;
    @FXML
    public TableColumn<TranslationPattern,String> tcTranslation;

    private ObservableList<TranslationPattern> translationPatterns = FXCollections.emptyObservableList();

    private ITranslationPatternService translationPatternService;
    private IMainGuiService mainGuiService;
    private TranslationPatternsController translationPatternsController;

    public TranslationPatternsController() {
        translationPatternsController = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            translationPatternService = new TranslationPatternService();
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

        tvTranslationPatterns.setPlaceholder(new Label("No Translation-Patterns added yet."));

        tcId.setCellValueFactory(new PropertyValueFactory<>("translationPatternId"));
        tcNlSentence.setCellValueFactory(new PropertyValueFactory<>("nlSentence"));
        tcRegexPattern.setCellValueFactory(new PropertyValueFactory<>("regexPattern"));
        tcTranslation.setCellValueFactory(new PropertyValueFactory<>("translation"));

        MenuItem menuItemDelete = new MenuItem("delete");
        menuItemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TranslationPattern translationPattern = translationPatterns.get(tvTranslationPatterns.getSelectionModel().getSelectedIndex());

                if (translationPattern != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Delete Entry");
                    alert.setHeaderText("Delete Entry?");
                    alert.setContentText(String.format("Are you sure you want to delete the translation-pattern with ID %d?", translationPattern.getTranslationPatternId()));

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeYes) {
                        try {
                            translationPatternService.deleteTranslationPattern(translationPattern);
                            loadData();
                            mainGuiService.updatedTranslationPatterns();
                        } catch (DaoException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        MenuItem menuItemEdit = new MenuItem("edit");
        menuItemEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TranslationPattern translationPattern = translationPatterns.get(tvTranslationPatterns.getSelectionModel().getSelectedIndex());

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_translation_pattern.fxml"));

                    Stage stage = new Stage();

                    /* block parent window */
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(tvTranslationPatterns.getScene().getWindow());

                    /* set the scene */
                    stage.setScene(new Scene(loader.load(), 411, 206));
                    stage.setTitle("Edit Entry");

                    stage.show();

                    EditTranslationPatternController editTranslationPatternController = (EditTranslationPatternController) loader.getController();
                    editTranslationPatternController.setMainGuiService(mainGuiService);
                    editTranslationPatternController.setTranslationPattern(translationPattern);
                    editTranslationPatternController.setTranslationPatternsController(translationPatternsController);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tvTranslationPatterns.setContextMenu(new ContextMenu(menuItemDelete,menuItemEdit));

        loadData();
    }

    public void loadData(){
        try {
            translationPatterns = FXCollections.observableArrayList(translationPatternService.getAllTranslationPatterns());
        } catch (DaoException e) {
            e.printStackTrace();
        }
        tvTranslationPatterns.setItems(translationPatterns);
    }


    private void closeStage() {
        Stage stage = (Stage) tvTranslationPatterns.getScene().getWindow();
        stage.close();
    }

    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }
}
