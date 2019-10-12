package bigsort.exceptions;

/**
 * BigSortException is used to handle known exceptions in a nice way
 */
public class BigSortException extends RuntimeException {
    private static final long serialVersionUID = 5552442262059282991L;

    public BigSortException(String message, Throwable throwable) {
        super(message, throwable);
    }
}