package com.sina.notesmvi.ui.screens.textEditor

sealed interface TextEditorUiState {
    data object Idle : TextEditorUiState
    data object Loading : TextEditorUiState
    data class OnReceiveTime(val time: String): TextEditorUiState
}