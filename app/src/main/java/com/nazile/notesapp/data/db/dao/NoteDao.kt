package com.nazile.notesapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nazile.notesapp.data.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @get:Query("SELECT * FROM notes ORDER BY id DESC")
    val allNotes: Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteList(notes: List<Note>)
}
