package at.tuwien.service.impl;

import at.tuwien.dao.DaoException;
import at.tuwien.dao.WordDAO;
import at.tuwien.dao.impl.JDBCWordDAO;
import at.tuwien.entity.Word;
import at.tuwien.service.IDirectoryService;

import java.util.List;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public class DirectoryService implements IDirectoryService {

    private WordDAO wordDAO;

    public DirectoryService() throws DaoException {
        wordDAO = new JDBCWordDAO();
    }

    @Override
    public void addWord(Word word) throws DaoException {
        wordDAO.createWord(word);
    }

    @Override
    public void deleteWord(Word word) throws DaoException {
        wordDAO.deleteWord(word);
    }

    @Override
    public List<Word> getAllWords() throws DaoException {
        return wordDAO.readAllWords();
    }

    @Override
    public void updateWord(Word word) throws DaoException {
        wordDAO.update(word);
    }
}
