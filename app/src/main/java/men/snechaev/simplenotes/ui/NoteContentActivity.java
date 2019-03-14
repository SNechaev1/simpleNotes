package men.snechaev.simplenotes.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import men.snechaev.simplenotes.R;
import men.snechaev.simplenotes.bean.Note;
import men.snechaev.simplenotes.util.DateUtil;


public class NoteContentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEtNoteContent;
    private ScrollView mScrollView;
    private Note mNote;
    private boolean isImportant = true;
    private Intent mIntent;
    private int mNoteID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_content);

        mEtNoteContent = findViewById(R.id.et_note_content);

        mScrollView = findViewById(R.id.scrollview_note_content);
        mIntent = this.getIntent();

        if ((mNote = (Note) mIntent.getSerializableExtra("note")) != null) {
            mNoteID = mNote.getId();
            mEtNoteContent.setText(mNote.getContent());
            mEtNoteContent.setSelection(mEtNoteContent.getText().length());
            isImportant = mNote.getImportant() == 1;
            Log.d("NoteContentActivity", mNote.getContent() + isImportant);
        }
        initToolbar(isImportant);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    private void initToolbar(boolean isImportant) {
        mToolbar = findViewById(R.id.toolbar_note_content);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_star).setChecked(isImportant);
        setItemIcon(menu.findItem(R.id.action_star), isImportant);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_star:
                if (item.isChecked()) {
                    item.setChecked(false);
                    setItemIcon(item, false);
                    Snackbar.make(mScrollView,R.string.not_important,Snackbar.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    setItemIcon(item, true);
                    Snackbar.make(mScrollView,R.string.important,Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_save:
                if (mNote != null) {
                    String content = mEtNoteContent.getText().toString();
                    Note editNote = new Note(mNote.getId(), content, isImportant ? 1 : 0,DateUtil.formatDateTime());
                    int result= NoteActivity.mNoteDbAdapter.updateNote(editNote);
                    finish();
                    break;
                } else {
                    if (TextUtils.isEmpty(mEtNoteContent.getText().toString())) {
                        Toast.makeText(NoteContentActivity.this, R.string.not_empty, Toast.LENGTH_SHORT).show();
                        Snackbar.make(mScrollView,R.string.not_empty,Snackbar.LENGTH_SHORT).show();
                        break;
                    } else {
                        String content = mEtNoteContent.getText().toString();
                        long result=NoteActivity.mNoteDbAdapter.createNote(content,isImportant, DateUtil.formatDateTime());
                        finish();
                        break;
                    }
                }


        }
        return  super.onOptionsItemSelected(item);
    }


    private void setItemIcon(MenuItem item, boolean isImportant) {
        item.setIcon(isImportant ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        this.isImportant = isImportant;
    }


}
