package at.tuwien.dao.impl;


import at.tuwien.dao.DaoException;
import at.tuwien.dao.H2Handler;
import at.tuwien.dao.WordDAO;
import at.tuwien.entity.Word;
import at.tuwien.entity.WordType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCWordDAO implements WordDAO {

    private Connection connection;

    public JDBCWordDAO() throws DaoException {
        connection = H2Handler.getConnection();
    }

    @Override
    public void createWord(Word word) throws DaoException {

        PreparedStatement createStatement;
        Statement sequenceStatement;

        if (word == null) {
            throw new DaoException("Couldn't create word.\nWord is null.");
        }

        if (word.getWord().isEmpty())
        {
            throw new DaoException("Couldn't create word.\nWord is empty.");
        }

        if(word.getWordType() == null)
        {
            throw new DaoException("Couldn't create word.\nWord type is null.");
        }

        try {

            createStatement = connection.prepareStatement("INSERT INTO Word(word, wordType) VALUES(?,?)");
            sequenceStatement = connection.createStatement();

            createStatement.setString(1, word.getWord());
            createStatement.setString(2, word.getWordType().name());
            createStatement.executeUpdate();

            ResultSet generatedKey = createStatement.getGeneratedKeys();

            sequenceStatement.close();
            createStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't create word in database.");
        }

    }

    @Override
    public void deleteWord(Word word) throws DaoException {

        PreparedStatement deleteStatement;

        if (word == null) {
            throw new DaoException("Couldn't delete word.\nWord is null.");
        }

        try {
            deleteStatement = connection.prepareStatement("DELETE FROM Word WHERE wordId = ?");

            deleteStatement.setLong(1, word.getWordId());
            deleteStatement.executeUpdate();

            deleteStatement.close();

        } catch (SQLException e) {
            throw new DaoException("Couldn't delete word.");
        }
    }

    @Override
    public List<Word> readAllWords() throws DaoException {

        Statement selectAllStatement;
        List<Word> wordList = new ArrayList<>();

        try {
            selectAllStatement = connection.createStatement();

            ResultSet resultSetInvoices = selectAllStatement.executeQuery("SELECT * FROM Word");

            while (resultSetInvoices.next()) {
                Word word = new Word();
                word.setWordId(resultSetInvoices.getLong("wordId"));
                word.setWord(resultSetInvoices.getString("word"));
                word.setWordType(WordType.valueOf(resultSetInvoices.getString("wordType")));

                wordList.add(word);
            }

        } catch (SQLException e) {
            throw new DaoException("Couldn't read words from database.");
        }

        return wordList;
    }
}
