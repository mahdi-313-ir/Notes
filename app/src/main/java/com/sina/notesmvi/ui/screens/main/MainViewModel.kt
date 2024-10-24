package com.sina.notesmvi.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sina.notesmvi.database.NotesDao
import com.sina.notesmvi.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val noteDao: NotesDao) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState

    init {
        loadNotes()
    }

    fun processIntent(mainIntent: MainIntent) {
        when (mainIntent) {
            is MainIntent.LoadNotes -> loadNotes()
            is MainIntent.DeleteNote -> deleteNote(mainIntent.note)
            is MainIntent.Filter -> filterForQuery(mainIntent.query)
        }
    }

    //use default dispatcher cause in test it shows less lag
    private fun filterForQuery(query: String) = viewModelScope.launch(Dispatchers.Default) {
        val notes =
            noteDao.getAllNotes().filter { it.title.contains(query) || it.data.contains(query) }
        _uiState.update { it.copy(notes = notes) }
    }

    private fun deleteNote(note: Note) = viewModelScope.launch(IO) {
        noteDao.deleteNote(note)
        loadNotes()
    }

    private fun loadNotes() = viewModelScope.launch(IO) {
        _uiState.update { it.copy(loading = true) }
        val notes = noteDao.getAllNotes()
        _uiState.update { it.copy(notes = notes, loading = false) }

    }


}