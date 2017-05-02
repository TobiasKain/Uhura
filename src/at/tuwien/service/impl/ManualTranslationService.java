package at.tuwien.service.impl;

import at.tuwien.dao.DaoException;
import at.tuwien.dao.ManualTranslationDAO;
import at.tuwien.dao.impl.JDBCManualTranslationDAO;
import at.tuwien.entity.ManualTranslation;
import at.tuwien.service.IManualTranslationService;

import java.util.List;

/**
 * Created by tobiaskain on 01/05/2017.
 */
public class ManualTranslationService implements IManualTranslationService{

    private ManualTranslationDAO manualTranslationDAO;

    public ManualTranslationService() throws DaoException {
        this.manualTranslationDAO = new JDBCManualTranslationDAO();
    }

    @Override
    public void addManualTranslation(ManualTranslation manualTranslation) throws DaoException {
        manualTranslationDAO.createManualTranslation(manualTranslation);
    }

    @Override
    public void deleteManualTranslation(ManualTranslation manualTranslation) throws DaoException {
        manualTranslationDAO.deleteManualTranslation(manualTranslation);
    }

    @Override
    public List<ManualTranslation> getAllManualTranslations() throws DaoException {
        return manualTranslationDAO.readAllManualTranslations();
    }
}
