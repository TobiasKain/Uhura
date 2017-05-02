package at.tuwien.dao.impl;


import at.tuwien.dao.DaoException;
import at.tuwien.dao.H2Handler;
import at.tuwien.dao.ManualTranslationDAO;
import at.tuwien.dao.TranslationPatternDAO;
import at.tuwien.entity.ManualTranslation;
import at.tuwien.entity.TranslationPattern;
import sun.jvm.hotspot.opto.MachNode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCManualTranslationDAO implements ManualTranslationDAO {

    private Connection connection;

    public JDBCManualTranslationDAO() throws DaoException {
        connection = H2Handler.getConnection();
    }

    @Override
    public void createManualTranslation(ManualTranslation manualTranslation) throws DaoException {
        PreparedStatement createStatement;
        Statement sequenceStatement;

        if (manualTranslation == null) {
            throw new DaoException("Couldn't create manual translation.\nManual translation is null.");
        }

        if (manualTranslation.getCnlSentence().isEmpty())
        {
            throw new DaoException("Couldn't create manual translation.\nCNL-sentence is empty.");
        }

        if(manualTranslation.getAspRule().isEmpty())
        {
            throw new DaoException("Couldn't create manual translation.\nASP rule is empty.");
        }

        try {
            createStatement = connection.prepareStatement("INSERT INTO ManualTranslation(cnlSentence, aspRule) VALUES(?,?)");
            sequenceStatement = connection.createStatement();

            createStatement.setString(1, manualTranslation.getCnlSentence());
            createStatement.setString(2, manualTranslation.getAspRule());
            createStatement.executeUpdate();


            sequenceStatement.close();
            createStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't create manual translation in database.");
        }
    }

    @Override
    public List<ManualTranslation> readAllManualTranslations() throws DaoException {
        Statement selectAllStatement;
        List<ManualTranslation> manualTranslationList = new ArrayList<>();

        try {
            selectAllStatement = connection.createStatement();

            ResultSet result = selectAllStatement.executeQuery("SELECT * FROM ManualTranslation");

            while (result.next()) {
                ManualTranslation manualTranslation = new ManualTranslation();
                manualTranslation.setManualTranslationId(result.getLong("manualTranslationId"));
                manualTranslation.setCnlSentence(result.getString("cnlSentence"));
                manualTranslation.setAspRule(result.getString("aspRule"));

                manualTranslationList.add(manualTranslation);
            }

        } catch (SQLException e) {
            throw new DaoException("Couldn't read manual translation from database.");
        }

        return manualTranslationList;
    }

    @Override
    public void deleteManualTranslation(ManualTranslation manualTranslation) throws DaoException {
        PreparedStatement deleteStatement;

        if (manualTranslation == null) {
            throw new DaoException("Couldn't delete manual translation.\nmanual translation is null.");
        }

        try {
            deleteStatement = connection.prepareStatement("DELETE FROM ManualTranslation WHERE manualTranslationId = ?");

            deleteStatement.setLong(1, manualTranslation.getManualTranslationId());
            deleteStatement.executeUpdate();

            deleteStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't delete manual translation.");
        }
    }
}
