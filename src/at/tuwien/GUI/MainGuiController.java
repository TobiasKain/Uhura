package at.tuwien.GUI;

import at.tuwien.Service.MainGuiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable{

    private MainGuiService mainGuiService;

    @FXML
    public TextArea taCNL;
    @FXML
    public TextArea taError;
    @FXML
    public TextArea taASP;
    @FXML
    public TextArea taModels;
    @FXML
    public TextField tfFilter;
    @FXML
    public Button btnSolve;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainGuiService = new MainGuiService();
    }

    public void btnSolveClicked(ActionEvent actionEvent) {
        taModels.setText("");

        List<String> models = mainGuiService.solve(taASP.getText(),tfFilter.getText());

        for (String model: models) {
            model = model.replaceAll("\\.\n", ", ");
            model = model.substring(0,model.lastIndexOf(", "));

            taModels.setText(taModels.getText() + String.format("{%s}%n",model));
        }
    }

    public void tfCnlOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getText().equals(".") ||
                taCNL.getCaretPosition() < taCNL.getText().lastIndexOf('.'))
        {
            taASP.setText(mainGuiService.translate(taCNL.getText()));
        }
    }
}
