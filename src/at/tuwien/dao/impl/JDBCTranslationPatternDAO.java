package at.tuwien.dao.impl;


import at.tuwien.dao.DaoException;
import at.tuwien.dao.H2Handler;
import at.tuwien.dao.TranslationPatternDAO;
import at.tuwien.dao.WordDAO;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;
import at.tuwien.entity.WordType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCTranslationPatternDAO implements TranslationPatternDAO {

    private Connection connection;

    public JDBCTranslationPatternDAO() throws DaoException {
        connection = H2Handler.getConnection();
    }

    @Override
    public void createTranslationPattern(TranslationPattern translationPattern) throws DaoException {

        PreparedStatement createStatement;
        Statement sequenceStatement;

        if (translationPattern == null) {
            throw new DaoException("Couldn't create word.\nTranslation-pattern is null.");
        }

        if (translationPattern.getNlSentence().isEmpty())
        {
            throw new DaoException("Couldn't create word.\nNL-sentence is empty.");
        }

        if(translationPattern.getRegexPattern().isEmpty())
        {
            throw new DaoException("Couldn't create word.\nRegex is empty.");
        }

        if(translationPattern.getTranslation().isEmpty())
        {
            throw new DaoException("Couldn't create word.\nTranslation is empty.");
        }

        try {
            createStatement = connection.prepareStatement("INSERT INTO TranslationPattern(nlSentence, regex, translation) VALUES(?,?,?)");
            sequenceStatement = connection.createStatement();

            createStatement.setString(1, translationPattern.getNlSentence());
            createStatement.setString(2, translationPattern.getRegexPattern());
            createStatement.setString(3, translationPattern.getTranslation());
            createStatement.executeUpdate();


            sequenceStatement.close();
            createStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't create translation-pattern in database.");
        }
    }

    @Override
    public List<TranslationPattern> readAllTranslationPatterns() throws DaoException {
        Statement selectAllStatement;
        List<TranslationPattern> translationPatternList = new ArrayList<>();

        try {
            selectAllStatement = connection.createStatement();

            ResultSet result = selectAllStatement.executeQuery("SELECT * FROM TranslationPattern");

            while (result.next()) {
                TranslationPattern translationPattern = new TranslationPattern();
                translationPattern.setTranslationPatternId(result.getLong("translationPatternId"));
                translationPattern.setNlSentence(result.getString("nlSentence"));
                translationPattern.setRegexPattern(result.getString("regex"));
                translationPattern.setTranslation(result.getString("translation"));

                translationPatternList.add(translationPattern);
            }

        } catch (SQLException e) {
            throw new DaoException("Couldn't read translation-pattern from database.");
        }

        return translationPatternList;
    }

    @Override
    public void deleteTranslationPattern(TranslationPattern translationPattern) throws DaoException {
        PreparedStatement deleteStatement;

        if (translationPattern == null) {
            throw new DaoException("Couldn't delete word.\nTranslation-pattern is null.");
        }

        try {
            deleteStatement = connection.prepareStatement("DELETE FROM TranslationPattern WHERE translationPatternId = ?");

            deleteStatement.setLong(1, translationPattern.getTranslationPatternId());
            deleteStatement.executeUpdate();

            deleteStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't delete translation-pattern.");
        }
    }

    @Override
    public void update(TranslationPattern translationPattern) throws DaoException {
        PreparedStatement updateStatement;

        if (translationPattern == null) {
            throw new DaoException("Couldn't update translation-pattern.\nTranslation-pattern is null.");
        }

        try {
            updateStatement = connection.prepareStatement("UPDATE TranslationPattern SET nlSentence = ?, regex = ?, translation = ? WHERE translationPatternId = ?");

            updateStatement.setString(1, translationPattern.getNlSentence());
            updateStatement.setString(2, translationPattern.getRegexPattern());
            updateStatement.setString(3, translationPattern.getTranslation());
            updateStatement.setLong(4, translationPattern.getTranslationPatternId());

            updateStatement.executeUpdate();

            updateStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't update translation-pattern in database.");
        }
    }
}
