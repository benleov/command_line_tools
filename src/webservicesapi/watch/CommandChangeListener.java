package webservicesapi.watch;

import webservicesapi.output.Output;

/**
 * @author Ben Leov
 */
public interface CommandChangeListener {

    /**
     * @param output
     */
    void onCommandChanged(Output output);

    void onStop();

}
