package com.nazile.notesapp.data.db.repo

import com.nazile.notesapp.data.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun getNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deleteMultipleNotes(note: List<Note>)

}