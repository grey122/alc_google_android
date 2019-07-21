package edu.socialmedia.noteactivity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Test
    public void createNewNote() {
        final DataManager dm = DataManager.getInstance();
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTitle = "Text note Title";
        final String noteText = "This is the body of my text note";

        int noteIndex =dm.createNewNote();
        NoteInfo newNote = dm.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo compareNote = dm.getNotes().get(noteIndex);

        assertEquals(course, compareNote.getCourse());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText, compareNote.getText());


    }
}