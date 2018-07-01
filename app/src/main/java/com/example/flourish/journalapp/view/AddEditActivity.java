package com.example.flourish.journalapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.flourish.journalapp.R;
import com.example.flourish.journalapp.database.DatabaseHelper;
import com.example.flourish.journalapp.database.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_ADD;
import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_EDIT;
import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_KEY;
import static com.example.flourish.journalapp.utils.Config.NOTE_ID_KEY;

public class AddEditActivity extends AppCompatActivity {

    private DatabaseHelper db;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private EditText titleTv, noteTv;
    private Note note;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            //mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                //mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                //urlProfileImg = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        titleTv = findViewById(R.id.title);
        noteTv = findViewById(R.id.note);

        db = new DatabaseHelper(this);

        // Get passed note id from intent
        int noteId = getIntent().getIntExtra(NOTE_ID_KEY, -1);
        action = getIntent().getIntExtra(NOTE_ACTION_KEY, -1);

        if (noteId == -1 && action == NOTE_ACTION_ADD) {
            // Add new note
            getSupportActionBar().setTitle("New Note");

            // Clear EditText contents
            titleTv.setText("");
            noteTv.setText("");

        } else if (noteId != -1 && action == NOTE_ACTION_EDIT) {
            // Edit note
            getSupportActionBar().setTitle("Edit Note");

            // Get note from db
            note = db.getNote(noteId);

            titleTv.setText(note.getTitle());
            noteTv.setText(note.getNote());

        } else {
            // Return to MainActivity if above conditions is not satisfied
            startActivity(new Intent(AddEditActivity.this, MainActivity.class));
        }

    }


    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String title, String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(title, note);
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String title, String mNote) {
        // updating note text
        note.setTitle(title);
        note.setNote(mNote);

        // updating note in db
        db.updateNote(note);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save:

                if (action == NOTE_ACTION_ADD) {
                    createNote(titleTv.getText().toString(), noteTv.getText().toString());
                } else if (action == NOTE_ACTION_EDIT) {
                    updateNote(titleTv.getText().toString(), noteTv.getText().toString());
                }

                // Return to ContentActivity
                // Create a new intent and add the note id
                Intent intent = new Intent(AddEditActivity.this, MainActivity.class);

                // Send the intent to launch a new activity
                startActivity(intent);

                finish();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
