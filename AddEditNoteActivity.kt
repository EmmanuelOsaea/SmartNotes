package com.example.smartnoteapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smartnoteapp.databinding.ActivityAddEditNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditNoteBinding
    private val viewModel: NoteViewModel by viewModels()
    private var noteId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if we’re editing an existing note
        noteId = intent.getIntExtra("noteId", -1)
        if (noteId != -1) {
            viewModel.allNotes.observe(this) { notes ->
                val note = notes.find { it.id == noteId }
                note?.let {
                    binding.etTitle.setText(it.title)
                    binding.etContent.setText(it.content)
                }
            }
        }

        // Save button
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

            val note = Note(
                id = noteId ?: 0,
                title = title,
                content = content,
                timestamp = currentTime
            )

            if (noteId == -1) {
                viewModel.insert(note)
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.update(note)
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}
