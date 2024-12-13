package com.example.apptareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptareas.detail.Examenes.ExamenViewModel
import com.example.apptareas.home.HomeViewMode
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.navigation.Navigation
import com.example.apptareas.ui.theme.AppTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val loginViewModel = viewModel(modelClass = LoginViewModel :: class.java )
            val homeViewMode = viewModel(modelClass = HomeViewMode :: class.java )
            val examenViewModel = viewModel(modelClass = ExamenViewModel :: class.java )
            AppTareasTheme {

                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background){
                    Navigation(
                        loginViewModel = loginViewModel,
                        examenViewModel = examenViewModel,
                        homeViewMode = homeViewMode)
                }
            }
        }
    }
}

