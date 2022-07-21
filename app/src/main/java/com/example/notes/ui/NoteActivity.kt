package com.example.notes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.notes.MainActivity
import com.example.notes.databinding.ActivityNoteBinding


class NoteActivity : Activity() {

    private var _binding: ActivityNoteBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let {
            binding.tvNoteTitle.text = it.getString("Title")
            binding.tvNoteBody.text = it.getString("Body")

            binding.btnBack.setOnClickListener {
                finish()
            }
        }
    }
}