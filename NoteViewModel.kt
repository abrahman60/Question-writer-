package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val prefs = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("সব")
    val isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))

    fun toggleDarkMode() {
        val newValue = !isDarkMode.value
        isDarkMode.value = newValue
        prefs.edit().putBoolean("is_dark_mode", newValue).apply()
    }

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<Note>> = combine(searchQuery, selectedCategory) { query, cat ->
        Pair(query, cat)
    }.flatMapLatest { (query, cat) ->
        if (query.isNotEmpty()) {
            repository.searchNotes(query)
        } else {
            repository.allNotes
        }
    }.combine(selectedCategory) { list, cat ->
        if (cat == "সব") list else list.filter { it.tag == cat }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current note being edited
    val currentNote = MutableStateFlow<Note?>(null)

    fun startNewNote() {
        currentNote.value = Note(
            title = "",
            content = "",
            colorHex = "#FFFFFF",
            fontName = "Roboto",
            fontSizeSp = 16,
            tag = if (selectedCategory.value != "সব") selectedCategory.value else "সাধারণ"
        )
    }

    fun selectNote(note: Note) {
        currentNote.value = note
    }

    fun closeNoteDetail() {
        currentNote.value = null
    }

    fun updateCurrentNote(updatedNote: Note) {
        currentNote.value = updatedNote
    }

    fun saveCurrentNote(onSaved: (Long) -> Unit = {}) {
        val note = currentNote.value ?: return
        if (note.title.isBlank() && note.content.isBlank() && note.drawingDataJson == null) return

        viewModelScope.launch {
            if (note.id == 0L) {
                val newId = repository.saveNote(note)
                currentNote.value = note.copy(id = newId)
                onSaved(newId)
            } else {
                repository.updateNote(note)
                onSaved(note.id)
            }
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            if (currentNote.value?.id == note.id) {
                currentNote.value = null
            }
        }
    }

    fun duplicateNote(note: Note) {
        viewModelScope.launch {
            val copy = note.copy(
                id = 0,
                title = "${note.title} (কপি)",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveNote(copy)
        }
    }
}
