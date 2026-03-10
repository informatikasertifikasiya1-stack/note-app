package com.nazile.notesapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nazile.notesapp.data.db.dao.NoteDao
import com.nazile.notesapp.data.models.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
