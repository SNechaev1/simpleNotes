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

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import men.snechaev.simplenotes.R;
import men.snechaev.simplenotes.adapter.NoteDbAdapter;
import men.snechaev.simplenotes.bean.Note;
import men.snechaev.simplenotes.util.DateUtil;


public class NoteContentActivity extends AppCompatActivity {

    private EditText mEditNoteContent;
    private ScrollView mScrollView;
    private Note mNote;
    private boolean isImportant = true;
    public NoteDbAdapter mNoteDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_content);

        mEditNoteContent = findViewById(R.id.et_note_content);
        mScrollView = findViewById(R.id.scrollview_note_content);
        Intent intent = this.getIntent();

        if ((mNote = (Note) intent.getSerializableExtra("note")) != null) {
            mEditNoteContent.setText(mNote.getContent());
            mEditNoteContent.setSelection(mEditNoteContent.getText().length());
            isImportant = mNote.getImportant() == 1;
            Log.d("NoteContentActivity", mNote.getContent() + isImportant);
        }
        initToolbar(isImportant);

        if (mNoteDbAdapter == null) {
            mNoteDbAdapter = new NoteDbAdapter(this);
            mNoteDbAdapter.open();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    private void initToolbar(boolean isImportant) {
        Toolbar toolbar = findViewById(R.id.toolbar_note_content);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
                    String content = mEditNoteContent.getText().toString();
                    Note editNote = new Note(mNote.getId(), content, isImportant ? 1 : 0,DateUtil.formatDateTime());

                    mNoteDbAdapter.updateNote(editNote);
                    finish();
                    break;
                } else {
                    if (TextUtils.isEmpty(mEditNoteContent.getText().toString())) {
                        Toast.makeText(NoteContentActivity.this, R.string.not_empty, Toast.LENGTH_SHORT).show();
                        Snackbar.make(mScrollView,R.string.not_empty,Snackbar.LENGTH_SHORT).show();
                        break;
                    } else {
                        String content = mEditNoteContent.getText().toString();
                        mNoteDbAdapter.createNote(content,isImportant, DateUtil.formatDateTime());
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
