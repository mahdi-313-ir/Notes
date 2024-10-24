package com.sina.notesmvi.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sina.notesmvi.ui.screens.main.MainScreen
import com.sina.notesmvi.ui.screens.textEditor.TextEditorScreen
import com.sina.notesmvi.ui.screens.util.Screens
import com.sina.notesmvi.ui.theme.NotesMviTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesMviTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screens.MainScreen) {
                    composable<Screens.MainScreen> {
                        MainScreen(navController = navController)
                    }

                    composable<Screens.TextEditorScreen> {
                        val screen: Screens.TextEditorScreen = it.toRoute()
                        TextEditorScreen(noteId = screen.noteId, navController = navController)
                    }


                }
            }
        }
    }
}

