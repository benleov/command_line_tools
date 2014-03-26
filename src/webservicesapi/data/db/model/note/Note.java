package webservicesapi.data.db.model.note;

import webservicesapi.data.db.model.ModelObject;

/**
 * @author Ben Leov
 */
public class Note implements ModelObject {

    private String message;

    public Note() {

    }

    public Note(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
