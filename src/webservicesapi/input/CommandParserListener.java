package webservicesapi.input;

/**
 * @author Ben Leov
 */
public interface CommandParserListener {

    /**
     * Called when a line has been read from the command line.
     *
     * @param read The line that was read.
     */
    void onLineRead(String read);
}