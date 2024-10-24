package com.sina.notesmvi.ui.screens.textEditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sina.notesmvi.database.NotesDao
import com.sina.notesmvi.model.Note
import com.sina.notesmvi.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextEditorViewModel @Inject constructor(private val notesDao: NotesDao) : ViewModel() {

    private val _uiState = MutableStateFlow<TextEditorUiState>(TextEditorUiState.Idle)

    val uiState: StateFlow<TextEditorUiState> = _uiState

    private val _uiEvent = Channel<TextEditorEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun processIntent(intent: TextEditorIntent) {
        when (intent) {
            is TextEditorIntent.FindNote -> findNote(intent.id)
            is TextEditorIntent.SaveNoteChanges -> saveNoteChanges(intent.note)
        }
    }

    private fun saveNoteChanges(note: Note) = viewModelScope.launch(IO) {
        _uiState.update { TextEditorUiState.Loading }
        if (note.uid != null) {
            val foundNote = notesDao.findNoteWithId(note.uid)
            if (foundNote != null) {
                if (foundNote.title != note.title || foundNote.data != note.data) {
                    saveNote(note = note)
                }
            } else {
                saveNote(note)
            }
        }else{
            saveNote(note)
        }
        _uiState.update { TextEditorUiState.Idle }
    }

    private fun saveNote(note: Note) = viewModelScope.launch(IO) {
        notesDao.addNote(note = note)
        _uiEvent.send(TextEditorEvent.OnSavedMessage)
    }

    private fun findNote(id: Int) = viewModelScope.launch(IO) {
        if (id != -1) {
            _uiState.update { TextEditorUiState.Loading }
            val note = notesDao.findNoteWithId(id)
            if (note != null) {
                _uiEvent.send(TextEditorEvent.Note(note))
                _uiState.update { TextEditorUiState.OnReceiveTime(note.date) }
            }
        } else {
            val currentTime = getCurrentDate()
            _uiState.update { TextEditorUiState.OnReceiveTime(currentTime) }
        }
    }

}