package at.tuwien.dao;

import at.tuwien.entity.Word;

import java.util.List;

public interface WordDAO {

    void createWord(Word word) throws DaoException;

    List<Word> readAllWords() throws DaoException;

    void deleteWord(Word word) throws DaoException;

    void update(Word word) throws DaoException;

}
