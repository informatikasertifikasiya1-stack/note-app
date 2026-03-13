package com.nazile.notesapp.presentation.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nazile.notesapp.R
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.presentation.adapters.NoteAdapter.NoteViewHolder
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class NoteAdapter(
    private val context: Context?,
    private var notes: MutableList<Note>,
    val onNoteClicked: (Note, Int) -> Unit
) : RecyclerView.Adapter<NoteViewHolder>() {
    var isSelectionMode = false
    private var timer: Timer? = null
    private val noteSource: MutableList<Note> = notes

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView
        var textSubtitle: TextView
        var textDateTime: TextView
        var item_new: TextView
        var layoutNote: RelativeLayout
        var imageNote: AppCompatImageView


        init {
            item_new = itemView.findViewById(R.id.item_new)
            textTitle = itemView.findViewById(R.id.item_textTitlem)
            textSubtitle = itemView.findViewById(R.id.item_textSubTitle)
            textDateTime = itemView.findViewById(R.id.item_textDateTime)
            layoutNote = itemView.findViewById(R.id.item_layoutNote)
            imageNote = itemView.findViewById(R.id.item_imageNote)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes.get(position)
        // Show/Hide checkmark based on selection

        // Visual feedback for selection (optional: change alpha or background)
        holder.layoutNote.alpha = if (isSelectionMode && !note.isSelected) 0.5f else 1.0f

        holder.layoutNote.setOnClickListener {
            onNoteClicked(note, position)
        }


        holder.layoutNote.setOnClickListener {
            onNoteClicked(notes.get(position), position)
        }
        holder.layoutNote.setOnLongClickListener {
            true
        }
        var step = 1
        var final_step = 1
        for (i in 1..<position + 1) {
            if (i == position + 1) {
                final_step = step
            }
            step++
        }

        when (step) {
            1 -> holder.item_new.visibility = View.VISIBLE
            else -> holder.item_new.visibility = View.GONE
        }

        holder.textTitle.text = note.title
        if (note.subtitle!!.trim { it <= ' ' }.isEmpty()) {
            holder.textSubtitle.visibility = View.GONE
        } else {
            holder.textSubtitle.text = note.subtitle
        }
        holder.textDateTime.text = note.dateTime

        val gradientDrawable = holder.layoutNote.background as GradientDrawable
        if (note.color != null) {
            when (note.color) {
                "#333333" -> {
                    gradientDrawable.setColor(Color.parseColor("#121212"))
                    holder.textTitle.setTextColor(Color.parseColor("#DBDBDB"))
                    holder.textSubtitle.setTextColor(Color.parseColor("#E9A0A0A0"))
                    holder.textDateTime.setTextColor(Color.parseColor("#E9A0A0A0"))
                }

                else -> gradientDrawable.setColor(Color.parseColor(note.color))
            }
        } else {
            gradientDrawable.setColor(Color.parseColor("#121212"))
            holder.textTitle.setTextColor(Color.parseColor("#DBDBDB"))
            holder.textSubtitle.setTextColor(Color.parseColor("#E9A0A0A0"))
            holder.textDateTime.setTextColor(Color.parseColor("#E9A0A0A0"))
        }

        if (note.imagePath != null) {
            holder.imageNote.load(note.imagePath) {
                crossfade(true)
            }
//            holder.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
            holder.imageNote.visibility = View.VISIBLE
        } else {
            holder.imageNote.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun getSelectedNotes(): List<Note> = notes.filter { it.isSelected }


    fun searchNote(searchKeyword: String) {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                if (searchKeyword.trim { it <= ' ' }.isEmpty()) {
                    notes = noteSource
                } else {
                    val temp = ArrayList<Note>()
                    for (note in noteSource) {
                        if (note.title!!.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || note.subtitle!!.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || note.noteText!!.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            temp.add(note)
                        }
                    }
                    notes = temp
                }

                Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
            }
        }, 500)
    }

    fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }
}
