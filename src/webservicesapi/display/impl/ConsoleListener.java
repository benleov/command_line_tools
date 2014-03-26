package webservicesapi.display.impl;

/**
 * @author Ben Leov
 */
public interface ConsoleListener {

    /**
     * Last typed character.
     *
     * @param ch The character that was typed.
     */
    void onKeyTyped(char ch);

    /**
     * Entire text field contents.
     *
     * @param text The text that was entered.
     */
    void onStringEntered(String text);
}
