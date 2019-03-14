package men.snechaev.simplenotes.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import men.snechaev.simplenotes.R;
import men.snechaev.simplenotes.adapter.NoteAdapter;
import men.snechaev.simplenotes.adapter.NoteDbAdapter;
import men.snechaev.simplenotes.bean.Note;
import men.snechaev.simplenotes.util.DateUtil;
import men.snechaev.simplenotes.util.SharedPreferencesUtil;

public class NoteActivity extends AppCompatActivity {

    public static NoteDbAdapter mNoteDbAdapter;
    public NoteAdapter mNoteAdapter;
    private Cursor mCursor;
    private static final String TAG = "NoteActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        FloatingActionButton mFloatingActionButton = findViewById(R.id.button_add_note);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteActivity.this, NoteContentActivity.class));
            }
        });
        if (mNoteDbAdapter == null) {
            mNoteDbAdapter = new NoteDbAdapter(this);
            mNoteDbAdapter.open();
        }


        SharedPreferencesUtil shared=new SharedPreferencesUtil(NoteActivity.this,NoteDbAdapter.CONFIG);
        boolean isFirstStart = shared.getBoolean(NoteDbAdapter.IS_FIRST_START);
        if(!isFirstStart){
            mNoteDbAdapter.deleteAllNotes();
            insertSomeReminders();
            shared.putBoolean(NoteDbAdapter.IS_FIRST_START,true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecycleView();
        Log.d(TAG, "onResume()");
    }


    private void initToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.app_name);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }


    private void initRecycleView() {
        RecyclerView recyclerView = findViewById(R.id.recycle_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCursor = mNoteDbAdapter.fetchAllNotes();
        mNoteAdapter = new NoteAdapter(this, mCursor, 0);

        mNoteAdapter.setRecyclerViewOnItemClickListener(new NoteAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (mCursor == null || mCursor.isClosed()) {
                    if (mCursor == null) {
                        Log.d("NoteActivity", "newCursor is null");
                        Toast.makeText(NoteActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
                    } else if (mCursor.isClosed()){
                        Log.d("NoteActivity", "newCursor is closed");
                        Toast.makeText(NoteActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mCursor.moveToPosition(position);
                    String content = mCursor.getString(mCursor.getColumnIndex(NoteDbAdapter.COL_CONTENT));
                    int important = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_IMPORTANT));
                    int id = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_ID));
                    Log.d("NoteActivity", content + important);
                    Note clickNote = new Note(id, content, important);
                    Intent intent = new Intent();
                    intent.setClass(NoteActivity.this, NoteContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("note", clickNote);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });


        mNoteAdapter.setOnSwipeListener(new NoteAdapter.onSwipeListener() {
            @Override
            public void onDel(int pos) {
                Toast.makeText(NoteActivity.this, "delete " + (pos+1) + " item", Toast.LENGTH_SHORT).show();
                mCursor.moveToPosition(pos);
                int id = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_ID));
                mNoteDbAdapter.deleteNoteById(id);
                mCursor = mNoteDbAdapter.fetchAllNotes();
                mNoteAdapter.changeCursor(mCursor);
            }

            @Override
            public void onTop(int pos) {
                Toast.makeText(NoteActivity.this, "put " + (pos+1) + " item on top", Toast.LENGTH_SHORT).show();
                mCursor.moveToPosition(pos);
                int id = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_ID));
                Note editNote = mNoteDbAdapter.fetchNoteById(id);
                editNote.setDateTime(DateUtil.formatDateTime());
                mNoteDbAdapter.updateNote(editNote);
                mCursor = mNoteDbAdapter.fetchAllNotes();
                mNoteAdapter.changeCursor(mCursor);
            }
        });

        recyclerView.setAdapter(mNoteAdapter);
    }


    private void insertSomeReminders() {
        mNoteDbAdapter.createNote("Learn Android Studio", true,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Send Dad birthday gift", true,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("String squash racket", false,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Prepare Advanced Android syllabus", false,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Buy new office chair", true,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Call Auto-body shop", false,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Renew membership to club", false,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Buy new Galaxy Android phone", true,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Sell old Android phone - auction", false,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Buy new paddles for kayaks", true,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Call accountant about tax returns", false,DateUtil.formatDateTime());
        mNoteDbAdapter.createNote("Buy 300,000 shares of Google", false,DateUtil.formatDateTime());
    }
}
