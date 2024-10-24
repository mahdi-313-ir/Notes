package com.sina.notesmvi.ui.screens.textEditor

sealed interface TextEditorEvent {
    data object OnSavedMessage: TextEditorEvent
    data class Note(val note: com.sina.notesmvi.model.Note): TextEditorEvent
}