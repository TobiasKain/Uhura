package at.tuwien.dao;

import at.tuwien.entity.Word;

import java.util.List;

public interface WordDAO {

    /**
     * Persists an invoice.
     *
     * @param invoice the invoice that should be persisted
     * @throws DaoException if invoice couldn't be persisted
     *                      - invoice is null
     *                      - invoice item list is empty
     *                      - customerName too long
     *                      - address too long
     */
    void createWord(Word word) throws DaoException;

    /**
     * Reads all stored invoices.
     *
     * @return list of all stored invoices
     * @throws DaoException if invoices couldn't be read
     */
    List<Word> readAllWords() throws DaoException;

    /**
     * Deletes a stored invoice.
     *
     * @param invoice the invoice that should be deleted
     * @throws DaoException if invoice couldn't be deleted
     *                      - invoice is null
     */
    void deleteWord(Word invoice) throws DaoException;

}
