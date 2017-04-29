package at.tuwien.gui;

import at.tuwien.dlv.DLVException;
import at.tuwien.entity.asp.Translation;
import at.tuwien.service.impl.MainGuiService;
import javafx.scene.control.TextArea;
import org.fxmisc.richtext.CodeArea;

/**
 * Created by tobiaskain on 16/04/2017.
 */
public class TranslatorThread implements Runnable {

    private MainGuiService mainGuiService;

    private CodeArea caCNL;
    private TextArea taError;
    private CodeArea caASP;

    public TranslatorThread(CodeArea caCNL, TextArea taError, CodeArea caASP) {
        this.caCNL = caCNL;
        this.taError = taError;
        this.caASP = caASP;

        mainGuiService = new MainGuiService();
    }

    @Override
    public void run() {
        caASP.replaceText("");
        taError.setText("");

        Translation translation = null;
        try {
            translation = mainGuiService.translate(caCNL.getText());

            caASP.replaceText(translation.getAspCode());

            for (String error : translation.getErrors()) {
                taError.appendText(error + "\n");
            }
        } catch (DLVException e) {
            taError.appendText(e.getMessage());
        }
    }
}
