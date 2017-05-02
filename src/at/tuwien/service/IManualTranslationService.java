package at.tuwien.service;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.ManualTranslation;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;

import java.util.List;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public interface IManualTranslationService {
    void addManualTranslation(ManualTranslation manualTranslation) throws DaoException;
    void deleteManualTranslation(ManualTranslation manualTranslation) throws DaoException;
    List<ManualTranslation> getAllManualTranslations() throws DaoException;
    void updateManualTranslation(ManualTranslation manualTranslation) throws DaoException;

}
