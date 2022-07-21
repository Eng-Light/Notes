package com.example.notes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.RvNoteBinding
import com.example.notes.model.Note

class NotesAdapter : ListAdapter<Note,
        NotesAdapter.ReviewsViewHolder>(DiffCallback) {

    var onItemClickListener: ((Note) -> Unit)? = null
    var onItemLongClickListener: ((Note) -> Unit)? = null

    inner class ReviewsViewHolder(
        private var binding: RvNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.tvNoteTitle.text = note.title
            binding.tvDate.text = note.date
            //binding.executePendingBindings()

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(note)
            }
            binding.root.setOnLongClickListener {
                onItemLongClickListener?.invoke(note)
                true
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem.body == newItem.body
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        return ReviewsViewHolder(
            RvNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }
}
