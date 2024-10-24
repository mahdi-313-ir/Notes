package com.sina.notesmvi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sina.notesmvi.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDataBase: RoomDatabase() {
    abstract fun noteDao(): NotesDao
}