package at.tuwien.dao;

import at.tuwien.entity.ManualTranslation;
import at.tuwien.entity.TranslationPattern;

import java.util.List;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public interface ManualTranslationDAO {
    void createManualTranslation(ManualTranslation manualTranslation) throws DaoException;

    List<ManualTranslation> readAllManualTranslations() throws DaoException;

    void deleteManualTranslation(ManualTranslation manualTranslation) throws DaoException;

}
