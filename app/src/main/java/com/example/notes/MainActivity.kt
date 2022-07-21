package com.example.notes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.databinding.PopupAddNoteBinding
import com.example.notes.databinding.PopupUpdateNoteBinding
import com.example.notes.model.Note
import com.example.notes.ui.NoteActivity
import com.example.notes.ui.NotesAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var _popBinding: PopupAddNoteBinding? = null
    private val popBinding get() = _popBinding!!

    private var _popBindingUpdate: PopupUpdateNoteBinding? = null
    private val popBindingUpdate get() = _popBindingUpdate!!

    private lateinit var database: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val noteAdapter = NotesAdapter()
        noteAdapter.onItemClickListener = { note ->
            val noteIntent = Intent(this, NoteActivity::class.java)
            noteIntent.putExtra("Title", note.title)
            noteIntent.putExtra("Body", note.body)
            startActivity(noteIntent)
        }
        noteAdapter.onItemLongClickListener = { note ->
            openDialogUpdate(note.title, note.body, note.id!!)
        }
        binding.rvNotes.adapter = noteAdapter

        setContentView(binding.root)

        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        database = FirebaseDatabase.getInstance()
        mRef = database.getReference("Notes")

        binding.fabAddNote.setOnClickListener {
            openDialogAdd()
        }
    }

    override fun onStart() {
        super.onStart()

        mRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notes = dataSnapshot.children.mapNotNull { it.getValue(Note::class.java) }

                val adapter = binding.rvNotes.adapter as NotesAdapter
                adapter.submitList(notes)
                binding.rvNotes.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun openDialogAdd() {
        val builder =
            AlertDialog.Builder(
                this, R.style.CustomAlertDialog
            ).create()

        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_add_note, null)

        _popBinding = PopupAddNoteBinding.bind(view)

        builder.setView(view)
        popBinding.btnAddNote.setOnClickListener {
            val title = popBinding.etTitle.text.toString()
            val noteBody = popBinding.etNote.text.toString()

            if (title.isEmpty()) {
                popBinding.etTitle.error = "Please enter a title"
            } else if (noteBody.isEmpty()) {
                popBinding.etNote.error = "Please enter a note"
            } else {
                val key = mRef.push().key
                val newNote =
                    Note(
                        id = key!!,
                        title = title,
                        body = noteBody,
                        date = getCurrentDate()
                    )
                mRef.child(key).setValue(newNote)
                builder.dismiss()
            }
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun openDialogUpdate(_title: String?, _body: String?, _id: String) {
        val builder =
            AlertDialog.Builder(
                this, R.style.CustomAlertDialog
            ).create()

        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_update_note, null)

        _popBindingUpdate = PopupUpdateNoteBinding.bind(view)
        builder.setView(view)

        popBindingUpdate.etTitle.setText(_title)
        popBindingUpdate.etNote.setText(_body)
        popBindingUpdate.btnSave.setOnClickListener {
            val title = popBindingUpdate.etTitle.text.toString()
            val noteBody = popBindingUpdate.etNote.text.toString()

            if (title.isEmpty()) {
                popBindingUpdate.etTitle.error = "Please enter a title"
            } else if (noteBody.isEmpty()) {
                popBindingUpdate.etNote.error = "Please enter a note"
            } else {
                val childRef = mRef.child(_id)
                val newNote =
                    Note(
                        id = _id,
                        title = title,
                        body = noteBody,
                        date = getCurrentDate()
                    )
                childRef.setValue(newNote)
                builder.dismiss()
            }
        }
        popBindingUpdate.btnDelete.setOnClickListener {
            val childRef = mRef.child(_id)
            childRef.removeValue()
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    //fun to get Current Date
    private fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
        return df.format(c.time)
    }
}