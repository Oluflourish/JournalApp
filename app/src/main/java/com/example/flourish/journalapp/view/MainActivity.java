package com.example.flourish.journalapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flourish.journalapp.NotesAdapter;
import com.example.flourish.journalapp.R;
import com.example.flourish.journalapp.database.DatabaseHelper;
import com.example.flourish.journalapp.database.model.Note;
import com.example.flourish.journalapp.utils.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_ADD;
import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_EDIT;
import static com.example.flourish.journalapp.utils.Config.NOTE_ACTION_KEY;
import static com.example.flourish.journalapp.utils.Config.NOTE_ID_KEY;

public class MainActivity extends AppCompatActivity {

    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private DatabaseHelper db;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private boolean toggleLayout = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showNoteDialog(false, null, -1);

                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(NOTE_ACTION_KEY, NOTE_ACTION_ADD);

                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });

        mAdapter = new NotesAdapter(this, notesList);

        // Set Recycler layout to LinearLayout or StaggeredLayout
        mSetRecyclerLayout();

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // Find the current note that was clicked on
                Note currentNote = notesList.get(position);

                // Create a new intent and add the note id
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                //intent.putExtra(NOTE_DETAIL_KEY, (Serializable) currentNote);
                intent.putExtra(NOTE_ID_KEY, currentNote.getId());

                // Send the intent to launch a new activity
                startActivity(intent);
                //finish();
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    @Override
    protected void onResume() {

        mAdapter.notifyDataSetChanged();

        super.onResume();

    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String title, String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(title, note);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String title, String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setTitle(title);
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Duplicate", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //showNoteDialog(true, notesList.get(position), position);

                    // Find the current note that was clicked on
                    Note currentNote = notesList.get(position);

                    // Create a new intent and add the note id
                    Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                    //intent.putExtra(NOTE_DETAIL_KEY, (Serializable) currentNote);
                    intent.putExtra(NOTE_ID_KEY, currentNote.getId());
                    intent.putExtra(NOTE_ACTION_KEY, NOTE_ACTION_EDIT);

                    // Send the intent to launch a new activity
                    startActivity(intent);
                    //finish();

                } else if (which == 1){
                    // Duplicate note
                    // Find the current note that was clicked on
                    Note currentNote = notesList.get(position);
                    createNote(currentNote.getTitle(), currentNote.getNote());

                } else if (which == 2) {
                    // deleteNote
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputTitle = view.findViewById(R.id.title);
        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputTitle.setText(note.getTitle());
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                String titleStr = inputTitle.getText().toString();
                String noteStr = inputNote.getText().toString();
                if (TextUtils.isEmpty(titleStr)) {
                    Toast.makeText(MainActivity.this, "Enter a title!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(noteStr)) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(titleStr, noteStr, position);
                } else {
                    // create new note
                    createNote(titleStr, noteStr);
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return true;
        }

        if (id == R.id.action_toggle_layout) {
            toggleLayout = !toggleLayout;
            mSetRecyclerLayout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void mSetRecyclerLayout() {
        // Set Recycler layout to LinearLayout or StaggeredLayout

        if (toggleLayout) {
            StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.setHasFixedSize(true);
        } else {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
            recyclerView.setAdapter(mAdapter);
        }
    }


}