package com.nazile.notesapp.data.repositories

import com.nazile.notesapp.data.db.dao.NoteDao
import com.nazile.notesapp.data.db.repo.NotesRepository
import com.nazile.notesapp.data.models.Note
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class NotesRepositoryImp @Inject constructor(private val noteDao: NoteDao) : NotesRepository {
    override suspend fun getNotes(): Flow<List<Note>> {
        return noteDao.allNotes
    }

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
    override suspend fun deleteMultipleNotes(note: List<Note>) {
        noteDao.deleteNoteList(note)
    }
}