package webservicesapi.sound;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

/**
 * Used for notifcations to the user.
 */
public class SoundPlayer {


    public void play() throws IOException {
        //** add this into your application code as appropriate
// Open an input stream  to the audio file.

// Use the static class member "player" from class AudioPlayer to play
// clip.


        // NOTE: doesn't work with other applications

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    File in = new File("res/mid.mid");

                    Sequence song = MidiSystem.getSequence(in);
                    Sequencer midiPlayer = MidiSystem.getSequencer();

                    midiPlayer.open();
                    midiPlayer.setSequence(song);
                    midiPlayer.setLoopCount(0);
                    midiPlayer.start();
                }
                catch (MidiUnavailableException e) {
                    e.printStackTrace();
                }
                catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
