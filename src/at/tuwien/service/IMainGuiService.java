package at.tuwien.service;

import at.tuwien.dlv.DLVException;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.Translation;
import at.tuwien.gui.TranslationType;

import java.util.List;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public interface IMainGuiService {
    void updateDirectory();
    void updatedTranslationPatterns();
    void updateManualTranslation();

    Translation translate(String cnlSentences) throws DLVException;

    List<String> solve(String aspRules, String filter) throws DLVException;

    TranslationType getTranslationType();
    void setTranslationType(TranslationType translationType);
}
