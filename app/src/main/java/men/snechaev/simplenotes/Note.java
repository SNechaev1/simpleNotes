package men.snechaev.simplenotes;

import java.io.Serializable;

public class Note implements Serializable {

    private int mId;
    private String mContent;
    private int mImportant;
    private String mDateTime;

    public Note(int id, String content, int important, String dateTime) {
        mId = id;
        mContent = content;
        mImportant = important;
        mDateTime = dateTime;
    }

    public Note(int id, String content, int important) {
        mId = id;
        mContent = content;
        mImportant = important;
    }

    public int getId() {
        return mId;
    }

    public String getContent() {
        return mContent;
    }

    public int getImportant() {
        return mImportant;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        mDateTime = dateTime;
    }
}
