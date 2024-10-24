package com.sina.notesmvi.ui.screens.main

import com.sina.notesmvi.model.Note

sealed interface MainIntent {
    data object LoadNotes : MainIntent
    data class DeleteNote(val note: Note): MainIntent
    data class Filter(val query: String): MainIntent
}