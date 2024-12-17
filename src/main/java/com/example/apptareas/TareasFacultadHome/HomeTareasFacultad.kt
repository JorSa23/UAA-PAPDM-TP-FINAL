package com.example.apptareas.TareasFacultadHome

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.models.TareasFacultad
import com.example.apptareas.repository.Resources
import com.example.apptareas.ui.theme.AppTareasTheme
import com.example.apptareas.ui.theme.cfacultad

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(
    homeViewMode: HomeViewMode?,
    onTareaFacultadClick: (id: String) -> Unit,
    navToTareaFacultadPage: () -> Unit, // Navegación directa a TareasFacultadScreen
    navToLoginPage: () -> Unit
) {
    val homeUiState = homeViewMode?.homeUiState ?: HomeUiState()
    var selectedTarea: TareasFacultad? by remember { mutableStateOf(null) }
    var openDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeViewMode?.loadTareasFacultad()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navToTareaFacultadPage() }, // Acción directa para agregar tareas
                containerColor = cfacultad
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Tarea Facultad"
                )
            }
        },
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = {
                        homeViewMode?.signOut()
                        navToLoginPage()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                title = { Text(text = "Home") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Sección de Tareas Facultad
            Text(
                text = "Tareas Facultad",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            when (val tareasList = homeUiState.tareasList) {
                is Resources.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(tareasList.data ?: emptyList()) { tarea ->
                            TareaFacultadItem(
                                tarea = tarea,
                                onClick = { onTareaFacultadClick(tarea.documentId) },
                                onLongClick = {
                                    openDialog = true
                                    selectedTarea = tarea
                                }
                            )
                        }
                    }
                }
                is Resources.Loading -> CircularProgressIndicator()
                else -> Text(
                    text = "Error al cargar tareas!",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // AlertDialog condicional sin AnimatedVisibility
            if (openDialog) {
                AlertDialog(
                    onDismissRequest = { openDialog = false },
                    title = { Text("¿Quieres borrar esta tarea?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                selectedTarea?.documentId?.let {
                                    homeViewMode?.deleteTareaFacultad(it)
                                }
                                openDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Borrar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { openDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TareaFacultadItem(
    tarea: TareasFacultad,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = cfacultad)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Tarea: ${tarea.materia}", fontWeight = FontWeight.Bold)
            Text(text = "Descripción: ${tarea.description}")
            Text(text = "Fecha: ${tarea.fecha}")
            Text(text = "Hora: ${tarea.hora}")
        }
    }
}

@Preview
@Composable
fun PreviewHome() {
    AppTareasTheme {
        Home(
            homeViewMode = null,
            onTareaFacultadClick = {},
            navToTareaFacultadPage = {},
            navToLoginPage = {}
        )
    }
}
