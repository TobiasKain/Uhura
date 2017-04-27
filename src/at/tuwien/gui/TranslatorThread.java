package at.tuwien.gui;

import at.tuwien.dlv.DLVException;
import at.tuwien.entity.asp.Translation;
import at.tuwien.service.impl.MainGuiService;
import javafx.scene.control.TextArea;

import java.util.List;

/**
 * Created by tobiaskain on 16/04/2017.
 */
public class TranslatorThread implements Runnable {

    private MainGuiService mainGuiService;

    private TextArea taCNL;
    private TextArea taError;
    private TextArea taASP;

    public TranslatorThread(TextArea taCNL, TextArea taError, TextArea taASP) {
        this.taCNL = taCNL;
        this.taError = taError;
        this.taASP = taASP;

        mainGuiService = new MainGuiService();
    }

    @Override
    public void run() {
        taASP.setText("");
        taError.setText("");

        Translation translation = null;
        try {
            translation = mainGuiService.translate(taCNL.getText());

            taASP.setText(translation.getAspCode());

            for (String error : translation.getErrors()) {
                taError.appendText(error + "\n");
            }
        } catch (DLVException e) {
            taError.appendText(e.getMessage());
        }
    }
}
