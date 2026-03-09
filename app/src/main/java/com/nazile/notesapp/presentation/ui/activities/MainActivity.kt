package com.nazile.notesapp.presentation.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nazile.notesapp.R
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.databinding.ActivityMainBinding
import com.nazile.notesapp.presentation.adapters.NoteAdapter

class MainActivity : AppCompatActivity() {
    private val mBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var noteList: MutableList<Note?>? = null
    private var mAdapter: NoteAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mAdapter = NoteAdapter(this, notes = noteList, onNoteClicked = { note, pos ->

        })
        mBinding.mainScreen.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.mainScreen.notesRecyclerView.adapter = mAdapter
    }
}