package men.snechaev.simplenotes.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import men.snechaev.simplenotes.bean.Note;


public class NoteDbAdapter {

    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    static final String COL_DATETIME = "last_modified_time";

    private static final int INDEX_ID = 0;
    private static final int INDEX_CONTENT = INDEX_ID + 1;
    private static final int INDEX_IMPORTANT = INDEX_ID + 2;
    private static final int INDEX_DATETIME = INDEX_ID + 3;

    private static final String DATABASE_NAME = "dba_note";
    private static final String TABLE_NAME = "tb1_note";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "NoteDbAdapter";
    private DataBaseHelper mDataBaseHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;


    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT," +
                    COL_IMPORTANT + " INTEGER ," +
                    COL_DATETIME + " TEXT "+" );";
    private static final String UPGRADING_DATABASE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public NoteDbAdapter(Context context) {
        mContext = context;
    }


    public void open() throws SQLException {
        mDataBaseHelper = new DataBaseHelper(mContext);
        mDb = mDataBaseHelper.getWritableDatabase();
    }

    public void close(){
        if (mDataBaseHelper != null) {
            mDataBaseHelper.close();
        }
    }

    public void createNote(String name, boolean important, String dateTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CONTENT, name);
        contentValues.put(COL_IMPORTANT, important ? 1 : 0);
        contentValues.put(COL_DATETIME,dateTime);
        mDb.insert(TABLE_NAME, null, contentValues);
    }

    public Note fetchNoteById(int id) {
        try (Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT, COL_DATETIME},
                COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null)) {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            assert cursor != null;
            return new Note(
                    cursor.getInt(INDEX_ID),
                    cursor.getString(INDEX_CONTENT),
                    cursor.getInt(INDEX_IMPORTANT),
                    cursor.getString(INDEX_DATETIME));
        }
    }


    public Cursor fetchAllNotes() {
        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT,COL_DATETIME},
                null, null, null, null,"last_modified_time desc");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    public void updateNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CONTENT, note.getContent());
        contentValues.put(COL_IMPORTANT, note.getImportant());
        contentValues.put(COL_DATETIME,note.getDateTime());
        mDb.update(TABLE_NAME, contentValues,
                COL_ID + "=?", new String[]{String.valueOf(note.getId())});
    }


    public void deleteNoteById(int id) {
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }


    public void deleteAllNotes() {
        mDb.delete(TABLE_NAME, null, null);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            Log.w(TAG, DATABASE_CREATE);
            database.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            database.execSQL(UPGRADING_DATABASE);
            onCreate(database);
        }
    }

}
