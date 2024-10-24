package com.sina.notesmvi.ui.screens.textEditor

import com.sina.notesmvi.model.Note

sealed interface TextEditorIntent {
    data class FindNote(val id: Int) : TextEditorIntent
    data class SaveNoteChanges(val note: Note) : TextEditorIntent
}