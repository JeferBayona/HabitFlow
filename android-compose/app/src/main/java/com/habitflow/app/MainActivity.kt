package com.habitflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.app.data.HabitViewModel
import com.habitflow.app.ui.screens.HabitFlowApp
import com.habitflow.app.ui.theme.HabitFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitFlowTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: HabitViewModel = viewModel()
                    HabitFlowApp(viewModel = viewModel)
                }
            }
        }
    }
}
