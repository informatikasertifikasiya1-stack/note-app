package com.nazile.notesapp.di

import android.content.Context
import androidx.room.Room
import com.nazile.notesapp.data.db.NotesDatabase
import com.nazile.notesapp.data.db.dao.NoteDao
import com.nazile.notesapp.data.repositories.NotesRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NotesDatabase {

        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            "notes_database"
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(
        database: NotesDatabase
    ): NoteDao {

        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDao: NoteDao
    ): NotesRepositoryImp {

        return NotesRepositoryImp(noteDao)
    }
}