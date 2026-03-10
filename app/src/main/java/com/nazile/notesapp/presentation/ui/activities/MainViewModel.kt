package com.nazile.notesapp.presentation.ui.activities

import androidx.lifecycle.ViewModel
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.data.repositories.NotesRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: NotesRepositoryImp
) : ViewModel() {

    suspend fun getNotes(): Flow<List<Note>> {
        return repository.getNotes()

    }
}