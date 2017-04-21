package at.tuwien.gui;

import at.tuwien.CNL2ASP.Translation;
import at.tuwien.service.impl.MainGuiService;
import javafx.scene.control.TextArea;

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

        Translation translation = mainGuiService.translate(taCNL.getText());
        taASP.setText(translation.getAspCode());

        for (String error : translation.getErrors()) {
            taError.appendText(error + "\n");
        }
    }
}
