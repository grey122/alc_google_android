package edu.socialmedia.noteactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "edu.socialmedia.noteactivity.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID =  "edu.socialmedia.noteactivity.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE =  "edu.socialmedia.noteactivity.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT =  "edu.socialmedia.noteactivity.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo note;
    private boolean isNewNote;
    private Spinner mspinnerCourses;
    private EditText mtextNoteTittle;
    private EditText mtextNoteText;
    private int mNotePosition;
    private boolean misCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mspinnerCourses = (Spinner) findViewById(R.id.sp_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }else {
            restoreOriginalNoteValues(savedInstanceState);

        }
        mtextNoteTittle =   (EditText) findViewById(R.id.text_note_yittle);
        mtextNoteText =   (EditText) findViewById(R.id.txt_note_text);


        if (!isNewNote) {
            displayNote(mspinnerCourses, mtextNoteTittle, mtextNoteText);

            Log.d(TAG,"onCreate");
        }

    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText =savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if(isNewNote)
            return;
        mOriginalNoteCourseId = note.getCourse().getCourseId();
        mOriginalNoteTitle = note.getTitle();
        mOriginalNoteText = note.getText();

    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTittle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(note.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTittle.setText(note.getTitle());
        textNoteText.setText(note.getText());

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = mNotePosition == POSITION_NOT_SET;

        if (isNewNote){
            createNewNote();

        }


            Log.i(TAG, "mNotePosition: " + mNotePosition);
            note = DataManager.getInstance().getNotes().get(mNotePosition);

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
       // note = dm.getNotes().get(mNotePosition);

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
        if (id == R.id.action_SEND_MAIL) {
            sendEmail();
            return true;


        }else if (id == R.id.action_cancel){
            misCancelling = true;
            finish();


        }else if (id == R.id.action_next){

            moveNext();
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNOteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNotePosition < lastNOteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
            saveNOte();
            ++mNotePosition;
            note = DataManager.getInstance().getNotes().get(mNotePosition);

            saveOriginalNoteValues();
            displayNote(mspinnerCourses, mtextNoteTittle, mtextNoteText);
            invalidateOptionsMenu();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (misCancelling){
            Log.i(TAG, "cancelling note at position " +mNotePosition);
            if(isNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            }
            else {

                storePreviousNoteValues();
            }
        }else {
            saveNOte();
        }
        Log.d(TAG, "onPause");
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        note.setCourse(course);
        note.setTitle(mOriginalNoteTitle);
        note.setText(mOriginalNoteText);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);

    }

    private void saveNOte() {
            note.setCourse((CourseInfo) mspinnerCourses.getSelectedItem());
            note.setTitle(mtextNoteTittle.getText().toString());
            note.setText(mtextNoteText.getText().toString());
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mspinnerCourses.getSelectedItem();
        String subject = mtextNoteTittle.getText().toString();
        String text = "checkout what i learnt in the pluralsight course \"" +
                course.getTitle() +"\" \n" + mtextNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);


    }
}
