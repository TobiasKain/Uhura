package at.tuwien.service;

import at.tuwien.dao.DaoException;
import at.tuwien.entity.Word;

import java.util.List;

public interface IDirectoryService {

    void addWord(Word word) throws DaoException;
    void deleteWord(Word word) throws DaoException;
    List<Word> getAllWords() throws DaoException;
    void updateWord(Word word) throws DaoException;
}
