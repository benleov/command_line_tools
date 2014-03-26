package webservicesapi.command;

/**
 * Thrown if the command that was attempted to be performed was invalid (i.e
 * the required parameters were not found).
 *
 * @author Ben Leov
 */
public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super();
    }

    public InvalidCommandException(String message) {
        super(message);
    }


    public InvalidCommandException(Throwable throwable) {
        super(throwable);
    }

}
