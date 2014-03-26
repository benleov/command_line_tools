package webservicesapi.command.impl.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Ben Leov
 */
public class DictConnection {

    public static final String SERVER_URL = "dict.org";
    public static final int SERVER_PORT = 2628;

    public static final int CONNECTION_TIMEOUT = 10000;

    public static final String COMMAND_DEFINE = "DEFINE";
    public static final String COMMAND_QUIT = "QUIT";

    public static final String CODE_OK = "250";

    public DictConnection() {
    }

    public String define(String word) throws IOException {

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        StringBuilder definition = new StringBuilder();

        try {
            socket = new Socket(SERVER_URL, SERVER_PORT);
            socket.setSoTimeout(CONNECTION_TIMEOUT);

            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            out.write(COMMAND_DEFINE + " * " + word + "\n");
            out.flush();

            String line = in.readLine();

            while (!line.startsWith(CODE_OK)) {
                definition.append(line);
                definition.append("\n");
                line = in.readLine();
            }

            out.write(COMMAND_QUIT);
            out.flush();
        }
        finally {

            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (socket != null) {
                socket.close();
            }
        }

        return definition.toString();
    }
}


