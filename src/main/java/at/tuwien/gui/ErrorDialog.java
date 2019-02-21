package at.tuwien.gui;

import javafx.scene.control.Alert;

public class ErrorDialog {

    /**
     * Shows an Error Dialog.
     *
     * @param errorMessage message of the error dialog
     */
    public static void showErrorDialog(String errorMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }
}
