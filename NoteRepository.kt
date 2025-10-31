package com.example.smartnote.repository

import androidx.lifecycle.LiveData
import com.example.smartnote.data.Note
import com.example.smartnote.data.NoteDao

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()
    val pinnedNotes: LiveData<List<Note>> = noteDao.getPinnedNotes()

    suspend fun insert(note: Note) = noteDao.insert(note)
    suspend fun update(note: Note) = noteDao.update(note)
    suspend fun delete(note: Note) = noteDao.delete(note)
    suspend fun deleteArchivedNotes() = noteDao.deleteArchivedNotes()

    fun searchNotes(query: String): LiveData<List<Note>> =
        noteDao.searchNotes("%$query%")
}
