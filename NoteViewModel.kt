package com.example.smartnote.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.smartnote.data.Note
import com.example.smartnote.data.NoteDatabase
import com.example.smartnote.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>
    val pinnedNotes: LiveData<List<Note>>

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
        pinnedNotes = repository.pinnedNotes
    }

    fun insert(note: Note) = viewModelScope.launch { repository.insert(note) }
    fun update(note: Note) = viewModelScope.launch { repository.update(note) }
    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
    fun deleteArchivedNotes() = viewModelScope.launch { repository.deleteArchivedNotes() }

    fun searchNotes(query: String): LiveData<List<Note>> = repository.searchNotes(query)
}
