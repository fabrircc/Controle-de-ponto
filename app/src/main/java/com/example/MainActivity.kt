package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.TotemScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.PontoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate950
                ) {
                    val viewModel: PontoViewModel = viewModel()
                    PontoApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PontoApp(viewModel: PontoViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState == PontoViewModel.AppScreen.ADMIN) {
                (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
            } else {
                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
            }
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            PontoViewModel.AppScreen.TOTEM -> {
                TotemScreen(viewModel = viewModel)
            }
            PontoViewModel.AppScreen.ADMIN -> {
                AdminScreen(viewModel = viewModel)
            }
        }
    }
}
