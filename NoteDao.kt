package com.example.smartnote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isPinned = 1 ORDER BY timestamp DESC")
    fun getPinnedNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM notes WHERE isArchived = 1")
    suspend fun deleteArchivedNotes()

    @Query("SELECT * FROM notes WHERE title LIKE :query OR content LIKE :query")
    fun searchNotes(query: String): LiveData<List<Note>>
}
