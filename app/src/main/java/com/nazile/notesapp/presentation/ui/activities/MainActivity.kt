package com.nazile.notesapp.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nazile.notesapp.R
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.databinding.ActivityMainBinding
import com.nazile.notesapp.presentation.adapters.NoteAdapter
import com.nazile.notesapp.presentation.ui.activities.createnote.CreateNoteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels()
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
        fetchAllNotes()
        mBinding.mainScreen.imageAddNoteMain.setOnClickListener {
            startActivity(Intent(this, CreateNoteActivity::class.java))
        }
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.getNotes()

        }
    }

    private fun fetchAllNotes() {
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.getNotes().collect {
                setUpRecyclerView(it)
            }
        }
    }

    private suspend fun setUpRecyclerView(notes: List<Note>) {
        withContext(Dispatchers.Main) {
            mAdapter =
                NoteAdapter(
                    this@MainActivity,
                    notes = notes as MutableList<Note>,
                    onNoteClicked = { note, pos ->

                    })
            mBinding.mainScreen.notesRecyclerView.adapter = mAdapter
        }
    }
}