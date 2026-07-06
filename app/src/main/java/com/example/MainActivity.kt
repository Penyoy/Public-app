package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CommunityViewModel
import com.example.ui.viewmodel.CommunityViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: CommunityViewModel by viewModels {
        CommunityViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsState()

                Crossfade(targetState = currentUser != null, label = "ScreenTransition") { isLoggedIn ->
                    if (isLoggedIn) {
                        MainScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    } else {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                // Transition handled automatically by currentUser state flow updates
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
