package at.tuwien.dao;

import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;

import java.util.List;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public interface TranslationPatternDAO {
    void createTranslationPattern(TranslationPattern translationPattern) throws DaoException;

    List<TranslationPattern> readAllTranslationPatterns() throws DaoException;

    void deleteTranslationPattern(TranslationPattern translationPattern) throws DaoException;

}
