package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.NoteViewModel
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.screens.NoteListScreen
import com.example.ui.theme.NoteDocTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            NoteDocTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val notes by viewModel.notes.collectAsStateWithLifecycle()
                    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                    val currentNote by viewModel.currentNote.collectAsStateWithLifecycle()

                    if (currentNote != null) {
                        NoteDetailScreen(
                            note = currentNote!!,
                            onNoteChange = { updated -> viewModel.updateCurrentNote(updated) },
                            onSaveNote = { viewModel.saveCurrentNote() },
                            onDeleteNote = {
                                currentNote?.let { viewModel.deleteNote(it) }
                            },
                            onBackClick = { viewModel.closeNoteDetail() }
                        )
                    } else {
                        NoteListScreen(
                            notes = notes,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.searchQuery.value = it },
                            selectedCategory = selectedCategory,
                            onCategoryChange = { viewModel.selectedCategory.value = it },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onNoteClick = { note -> viewModel.selectNote(note) },
                            onCreateNoteClick = { viewModel.startNewNote() },
                            onTogglePin = { note -> viewModel.togglePin(note) },
                            onDeleteNote = { note -> viewModel.deleteNote(note) },
                            onDuplicateNote = { note -> viewModel.duplicateNote(note) }
                        )
                    }
                }
            }
        }
    }
}
