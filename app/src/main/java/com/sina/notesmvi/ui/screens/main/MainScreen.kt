package com.sina.notesmvi.ui.screens.main

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sina.notesmvi.R
import com.sina.notesmvi.model.Note
import com.sina.notesmvi.ui.composable.Loading
import com.sina.notesmvi.ui.screens.util.Screens

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                {
                    navController.navigate(Screens.TextEditorScreen())

                }, shape = CircleShape, modifier = modifier
                    .size(60.dp)
                    .offset(y = (-20).dp)
            ) {
                Icon(Icons.Default.Create, contentDescription = null)
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier.padding(innerPadding)
        ) {

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            if (uiState.loading) {
                Loading()
            }

            LaunchedEffect(Unit) {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getStateFlow("load", false)
                    ?.collect {
                        viewModel.processIntent(MainIntent.LoadNotes)
                    }
            }

            var searchQuery by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                TextField(
                    searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.processIntent(MainIntent.Filter(searchQuery))
                    },
                    label = {
                        Text(stringResource(R.string.search))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                )
                Spacer(Modifier.height(15.dp))
                uiState.notes?.let {
                    NotesList(
                        notes = it,
                        navController = navController,
                        myViewModel = viewModel
                    )
                }
            }
        }
    }


}


@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    navController: NavController,
    myViewModel: MainViewModel,
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteNote by remember { mutableStateOf<Note?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        items(notes) { note ->
            NoteItem(note = note, navController) {
                deleteNote = note
                showDeleteDialog = true
            }
        }
    }

    if (showDeleteDialog) {
        DeleteItemDialog(onDismiss = {
            showDeleteDialog = false
        }, onDelete = {
            deleteNote?.let {
                showDeleteDialog = false
                myViewModel.processIntent(MainIntent.DeleteNote(note = deleteNote!!))
            }
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteItem(
    note: Note,
    navController: NavController,
    onDelete: (Note) -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp) // Combined padding for brevity
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .combinedClickable(
                onClick = {
                    navController.navigate(Screens.TextEditorScreen(noteId = note.uid!!)) // Safe non-null assertion for uid
                }, onLongClick = {
                    onDelete(note)
                })
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.data,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Thin,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.date,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        }
        val context = LocalContext.current
        Icon(
            painter = painterResource(R.drawable.baseline_share_24),
            contentDescription = "share",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable {
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("${context.getString(R.string.title)} : ${note.title}")
                    stringBuilder
                        .append("\n")
                        .append("\n")
                    stringBuilder.append("${context.getString(R.string.body)} : ${note.data}")

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
                    }

                    val shareIntent =
                        Intent.createChooser(intent, context.getString(R.string.shareVia))
                    context.startActivity(shareIntent)
                }

        )
    }


}

@Composable
private fun DeleteItemDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDelete()
            }) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Text(stringResource(R.string.sureToDelete))
        }
    )
}





















