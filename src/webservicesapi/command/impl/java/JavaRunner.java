package webservicesapi.command.impl.java;

/**
 * @author Ben Leov
 */
public class JavaRunner {


    public JavaRunner() {
        // TODO: attempt to find jre

    }

    public void run(JavaConfiguration config) throws JavaRunnerException {

        if (config.isValid()) {

            // attempt to run


        } else {
            throw new JavaRunnerException("Invalid configuration.");
        }

    }

}
