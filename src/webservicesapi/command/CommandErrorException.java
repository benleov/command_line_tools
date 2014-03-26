package webservicesapi.command;

/**
 * Thrown if there was an error performing the command.
 *
 * @author Ben Leov
 */
public class CommandErrorException extends Exception {
    public CommandErrorException() {

    }

    public CommandErrorException(Throwable throwable) {
        super(throwable);
    }
}
