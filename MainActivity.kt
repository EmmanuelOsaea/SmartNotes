package com.example.smartnoteapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartnoteapp.databinding.ActivityMainBinding
import androidx.lifecycle.Observer
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        adapter = NoteAdapter(listOf()) { note ->
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            startActivity(intent)
        }

        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = adapter

        // Observe database changes
        viewModel.allNotes.observe(this) { notes ->
            adapter.updateNotes(notes)
        }

        // Add new note
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivity(intent)
        }
    }
}



private fun addNoteDialog() {
        val dialog = NoteDialog(this) { title, content ->
            val newNote = Note(title = title, content = content)
            viewModel.insert(newNote)
        }
        dialog.show()
    }

    private fun editNoteDialog(note: Note) {
        val dialog = NoteDialog(this, note) { title, content ->
            val updatedNote = note.copy(title = title, content = content)
            viewModel.update(updatedNote)
        }
        dialog.show()
    }
}
