package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun getNoteById(id: Long): Flow<Note?> = noteDao.getNoteById(id)

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    suspend fun saveNote(note: Note): Long {
        val noteToSave = note.copy(updatedAt = System.currentTimeMillis())
        return noteDao.insertNote(noteToSave)
    }

    suspend fun updateNote(note: Note) {
        val noteToUpdate = note.copy(updatedAt = System.currentTimeMillis())
        noteDao.updateNote(noteToUpdate)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }
}
