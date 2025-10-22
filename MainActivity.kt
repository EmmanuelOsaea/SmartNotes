package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteDatabase
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.ui.NoteAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter
    private val db by lazy { NoteDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NoteAdapter { note ->
            lifecycleScope.launch {
                db.noteDao().delete(note)
                loadNotes()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = db.noteDao().getAllNotes()
            adapter.submitList(notes)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}
