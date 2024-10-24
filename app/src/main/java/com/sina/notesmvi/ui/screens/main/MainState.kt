package com.sina.notesmvi.ui.screens.main

import com.sina.notesmvi.model.Note

data class MainState(
    val loading: Boolean = false,
    val notes: List<Note>? = null,
)