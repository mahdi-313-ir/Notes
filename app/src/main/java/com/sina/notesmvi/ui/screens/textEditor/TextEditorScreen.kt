package com.sina.notesmvi.ui.screens.textEditor

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sina.notesmvi.R
import com.sina.notesmvi.model.Note
import com.sina.notesmvi.ui.composable.Loading

@Composable
fun TextEditorScreen(
    modifier: Modifier = Modifier,
    noteId: Int,
    navController: NavController,
    myViewModel: TextEditorViewModel = hiltViewModel(),
) {


    LaunchedEffect(Unit) {
        myViewModel.processIntent(TextEditorIntent.FindNote(noteId))
    }

    Scaffold(modifier.fillMaxSize()) { padding ->
        Main(Modifier.padding(padding), noteId, myViewModel, navController)
    }
}

@Composable
private fun Main(
    modifier: Modifier = Modifier,
    noteId: Int,
    myViewModel: TextEditorViewModel,
    navController: NavController,
) {
    val uiState by myViewModel.uiState.collectAsStateWithLifecycle()


    var titleState by remember { mutableStateOf("") }
    var bodyState by remember { mutableStateOf("") }
    var timeState by remember { mutableStateOf("") }


    val keyboardController = LocalSoftwareKeyboardController.current

    fun doSaving() {
        // at least a field must have some text
        if (titleState.isNotEmpty() || bodyState.isNotEmpty()) {
            val noteId = if (noteId == -1) null else noteId
            myViewModel.processIntent(
                TextEditorIntent.SaveNoteChanges(
                    Note(
                        noteId,
                        titleState,
                        timeState,
                        bodyState
                    )
                )
            )
        }
    }

    BackHandler {
        keyboardController?.hide()
        doSaving()
        navController.navigateUp()
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        myViewModel.uiEvent.collect {
            when (it) {
                is TextEditorEvent.OnSavedMessage -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("load", true)
                }

                is TextEditorEvent.Note -> {
                    val note = it.note
                    titleState = note.title
                    bodyState = note.data
                }
            }
        }

    }

    when (uiState) {
        is TextEditorUiState.Idle -> {}
        is TextEditorUiState.Loading -> {
            Loading()
        }

        is TextEditorUiState.OnReceiveTime -> {
            timeState = (uiState as TextEditorUiState.OnReceiveTime).time
        }
    }


    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    "back",
                    modifier = Modifier.clickable {
                        keyboardController?.hide()
                        doSaving()
                        navController.navigateUp()
                    }
                )
            }
            Spacer(Modifier.height(10.dp))

            ReusableTextField(
                text = titleState,
                onTextChange = { titleState = it },
                textStyle = MaterialTheme.typography.titleLarge,
                label = stringResource(R.string.title) + ":",
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(70.dp)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                Arrangement.Start,
                Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.modifiedTime),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    timeState,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(10.dp))
            ReusableTextField(
                text = bodyState,
                onTextChange = { bodyState = it },
                textStyle = MaterialTheme.typography.bodyMedium,
                label = stringResource(R.string.data) + ":",
                modifier = Modifier.fillMaxSize(),
            )
        }

    }
}

@Composable
private fun ReusableTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    textStyle: TextStyle,
    label: String = "",
) {
    TextField(
        text,
        { onTextChange(it) },
        textStyle = textStyle,
        maxLines = Int.MAX_VALUE,
        label = {
            Text(label)
        },
        colors = TextFieldDefaults.colors().copy(
            disabledContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = Color.Transparent
        ),
        modifier = modifier,
    )
}


@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    var titleState by remember { mutableStateOf("") }
    var bodyState by remember { mutableStateOf("") }


    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        TextField(
            titleState,
            { titleState = it },
            textStyle = MaterialTheme.typography.titleLarge,
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors().copy(
                disabledContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(100.dp)
        )
        Spacer(Modifier.height(25.dp))
        TextField(
            bodyState,
            { bodyState = it },
            textStyle = MaterialTheme.typography.bodyLarge,
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors().copy(
                disabledContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}