package at.tuwien.service;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;

import java.util.List;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public interface ITranslationPatternService {
    void addTranslationPattern(TranslationPattern translationPattern) throws DaoException;
    void deleteTranslationPattern(TranslationPattern translationPattern) throws DaoException;
    List<TranslationPattern> getAllTranslationPatterns() throws DaoException;
    void updateTranslationPattern(TranslationPattern translationPattern) throws DaoException;

}
