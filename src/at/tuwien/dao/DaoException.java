package at.tuwien.dao;

/**
 * Exception class for dao objects.
 * If something inside a method of an dao object is going
 * wrong than this exception class should be used.
 */
public class DaoException extends Exception {
    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }
}
