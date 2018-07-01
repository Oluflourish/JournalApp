package com.example.flourish.journalapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.flourish.journalapp.R;
import com.example.flourish.journalapp.database.DatabaseHelper;
import com.example.flourish.journalapp.database.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_EDIT;
import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_KEY;
import static com.example.flourish.journalapp.utils.Config.NOTE_ID_KEY;

public class ContentActivity2 extends AppCompatActivity {

    private DatabaseHelper db;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private TextView titleTv, noteTv;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
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

        if (noteId == -1) {
            // Return to MainActivity if no NOTE_ID is passed
            startActivity(new Intent(ContentActivity2.this, MainActivity.class));
        }

        // Get note from db
        note = db.getNote(noteId);

        titleTv.setText(note.getTitle());
        noteTv.setText(note.getNote());
    }


    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote() {
        // deleting the note from db
        db.deleteNote(note);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);
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

            case R.id.action_edit:
                // Create a new intent and add the note id
                Intent intent1 = new Intent(ContentActivity2.this, AddEditActivity.class);

                //intent.putExtra(NOTE_DETAIL_KEY, (Serializable) currentNote);
                intent1.putExtra(NOTE_ID_KEY, note.getId());
                intent1.putExtra(NOTE_ACTION_KEY, NOTE_ACTION_EDIT);

                // Send the intent to launch a new activity
                startActivity(intent1);
                finish();

                return true;

            case R.id.action_delete:
                deleteNote();

                // Return to MainActivity
                Intent intent2 = new Intent(ContentActivity2.this, MainActivity.class);

                // Send the intent to launch a new activity
                startActivity(intent2);
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
