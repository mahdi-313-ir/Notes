package com.sina.notesmvi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sina.notesmvi.model.Note

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun addNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<Note>

    @Query("DELETE FROM notes")
    fun deleteAll()

    @Query("SELECT * FROM notes WHERE uid = :id")
    fun findNoteWithId(id: Int): Note?

}