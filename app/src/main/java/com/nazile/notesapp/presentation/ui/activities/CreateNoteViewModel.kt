package com.nazile.notesapp.presentation.ui.activities

import androidx.lifecycle.ViewModel
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.data.repositories.NotesRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    val repository: NotesRepositoryImp
) : ViewModel() {

    suspend fun saveNote(note: Note) {
        return repository.insertNote(note)

    }
}