package webservicesapi.command.impl.browse;

/**
 * @author Ben Leov
 */
public interface HTMLParserListener {

    void onContentFound(String page, String content);

    void onFinish();
}
