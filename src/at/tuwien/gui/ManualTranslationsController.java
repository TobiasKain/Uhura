package at.tuwien.gui;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.ManualTranslation;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;
import at.tuwien.service.IMainGuiService;
import at.tuwien.service.impl.DirectoryService;
import at.tuwien.service.impl.ManualTranslationService;
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
 * Created by tobiaskain on 02/05/2017.
 */
public class ManualTranslationsController implements Initializable{
    @FXML
    public TableView<ManualTranslation> tvManualTranslations;
    @FXML
    public TableColumn<ManualTranslation,Long> tcId;
    @FXML
    public TableColumn<ManualTranslation,String> tcCNlSentence;
    @FXML
    public TableColumn<ManualTranslation,String> tcAspRule;

    private ObservableList<ManualTranslation> manualTranslations = FXCollections.emptyObservableList();

    private ManualTranslationService manualTranslationService;

    private IMainGuiService mainGuiService;

    private ManualTranslationsController manualTranslationsController;

    public ManualTranslationsController() {
        manualTranslationsController = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            manualTranslationService = new ManualTranslationService();
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

        tvManualTranslations.setPlaceholder(new Label("No Manual-Translations added yet."));

        tcId.setCellValueFactory(new PropertyValueFactory<>("manualTranslationId"));
        tcCNlSentence.setCellValueFactory(new PropertyValueFactory<>("cnlSentence"));
        tcAspRule.setCellValueFactory(new PropertyValueFactory<>("aspRule"));

        MenuItem menuItemDelete = new MenuItem("delete");
        menuItemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ManualTranslation manualTranslation = manualTranslations.get(tvManualTranslations.getSelectionModel().getSelectedIndex());

                if (manualTranslation != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Delete Entry");
                    alert.setHeaderText("Delete Entry?");
                    alert.setContentText(String.format("Are you sure you want to delete the manual translation with ID %d?", manualTranslation.getManualTranslationId()));

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeYes) {
                        try {
                            manualTranslationService.deleteManualTranslation(manualTranslation);
                            mainGuiService.updateManualTranslation();
                            loadData();
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
                ManualTranslation manualTranslation = manualTranslations.get(tvManualTranslations.getSelectionModel().getSelectedIndex());

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("edit_manual_translation.fxml"));

                    Stage stage = new Stage();

                    /* block parent window */
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(tvManualTranslations.getScene().getWindow());

                    /* set the scene */
                    stage.setScene(new Scene(loader.load(), 411, 206));

                    stage.setTitle("Edit Manual Translation");

                    stage.show();

                    EditManualTranslationController editManualTranslationController = (EditManualTranslationController) loader.getController();
                    editManualTranslationController.setMainGuiService(mainGuiService);
                    editManualTranslationController.setManualTranslation(manualTranslation);
                    editManualTranslationController.setManualTranslationsController(manualTranslationsController);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tvManualTranslations.setContextMenu(new ContextMenu(menuItemDelete,menuItemEdit));

        loadData();
    }

    public void loadData(){
        try {
            manualTranslations = FXCollections.observableArrayList(manualTranslationService.getAllManualTranslations());
        } catch (DaoException e) {
            e.printStackTrace();
        }
        tvManualTranslations.setItems(manualTranslations);
    }

    public void setMainGuiService(IMainGuiService mainGuiService) {
        this.mainGuiService = mainGuiService;
    }
}
