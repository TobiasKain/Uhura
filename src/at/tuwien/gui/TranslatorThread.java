package at.tuwien.gui;

import at.tuwien.dlv.DLVException;
import at.tuwien.entity.asp.Translation;
import at.tuwien.service.impl.MainGuiService;

/**
 * Created by tobiaskain on 16/04/2017.
 */
public class TranslatorThread implements Runnable {

    private MainGuiService mainGuiService;
    private TranslationTabController translationTabController;
    private String cnl;

    public TranslatorThread(TranslationTabController translationTabController, String cnl) {
        this.translationTabController = translationTabController;
        this.cnl = cnl;

        mainGuiService = new MainGuiService();
    }

    @Override
    public void run() {
        translationTabController.updateCaAspAsync("");
        translationTabController.updateTaErrorAsync("");

        Translation translation = null;
        try {
            translation = mainGuiService.translate(cnl);

            translationTabController.updateCaAspAsync(translation.getAspCode());

            for (String error : translation.getErrors()) {
                translationTabController.appendTaErrorAsync(error + "\n");
            }
        } catch (DLVException e) {
            translationTabController.appendTaErrorAsync(e.getMessage());
        }

        translationTabController.endTranslationAsync();
    }
}
