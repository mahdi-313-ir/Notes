package com.sina.notesmvi.ui.screens.util

import kotlinx.serialization.Serializable


sealed interface Screens {
    @Serializable
    data object MainScreen

    @Serializable
    data class TextEditorScreen(val noteId: Int = -1)
}
