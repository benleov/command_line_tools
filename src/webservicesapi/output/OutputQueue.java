package webservicesapi.output;

import java.util.HashSet;
import java.util.Set;

/**
 * Central point for notification for all Output.
 *
 * @author Ben Leov
 */
public class OutputQueue {

    private Set<OutputQueueListener> listeners;

    public OutputQueue() {
        listeners = new HashSet<OutputQueueListener>();
    }

    public void send(Output output) {
        notifyListeners(output);
    }

    private void notifyListeners(Output output) {
        for (OutputQueueListener curr : listeners) {
            curr.onOutputReceived(output);
        }
    }

    public void addListener(OutputQueueListener listener) {
        listeners.add(listener);
    }

}
