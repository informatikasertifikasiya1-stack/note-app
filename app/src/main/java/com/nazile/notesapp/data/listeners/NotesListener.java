package com.nazile.notesapp.data.listeners;


import com.nazile.notesapp.data.models.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
