package com.sina.notesmvi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("notes")
data class Note(
    @PrimaryKey(true)
    val uid: Int? = null,

    @ColumnInfo("title")
    val title: String,

    @ColumnInfo("date")
    val date: String,

    @ColumnInfo("data")
    val data: String,

    )
