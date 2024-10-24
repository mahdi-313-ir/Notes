package com.sina.notesmvi.di

import android.content.Context
import androidx.room.Room
import com.sina.notesmvi.database.NoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotesModule {
    @Provides
    @Singleton
    fun getNoteDataBase(@ApplicationContext context: Context): NoteDataBase {
        return Room.databaseBuilder(
            context = context,
            klass = NoteDataBase::class.java,
            "NoteDatabase"
        ).build()
    }

    @Provides
    fun getNotesDao(noteDataBase: NoteDataBase) = noteDataBase.noteDao()
}