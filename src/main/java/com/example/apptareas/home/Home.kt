package com.example.apptareas.home


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.R
import com.example.apptareas.models.Examenes
import com.example.apptareas.models.TareasFacultad
import com.example.apptareas.repository.Resources
import com.example.apptareas.ui.theme.AppTareasTheme
import com.example.apptareas.ui.theme.ccasa
import com.example.apptareas.ui.theme.ccompras
import com.example.apptareas.ui.theme.cfacultad
import com.example.apptareas.ui.theme.cexamen

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(
    homeViewMode: HomeViewMode?,
    onExamenClick: (id: String) -> Unit,
    onTareaFacultadClick: (id: String) -> Unit,
    navToExamenPage: () -> Unit,
    navToTareaFacultadPage: () -> Unit,
    navToLoginPage: () -> Unit
) {
    val homeUiState = homeViewMode?.homeUiState ?: HomeUiState()

    val scrollState = rememberScrollState()
    var isMenuExpanded by remember { mutableStateOf(false) }

    var openDialog by remember { mutableStateOf(false) }
    var selectedExamen: Examenes? by remember { mutableStateOf(null) }
    var selectedTarea: TareasFacultad? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        homeViewMode?.loadExamenes()
        homeViewMode?.loadTareasFacultad()
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = isMenuExpanded) {
                    Column(horizontalAlignment = Alignment.End) {
                        FloatingActionButton(
                            onClick = { navToExamenPage() },
                            containerColor = cexamen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_book),
                                    contentDescription = "Exámenes"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Exámenes")
                            }
                        }

                        FloatingActionButton(
                            onClick = { },
                            containerColor = ccasa,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp) // Espaciado interno opcional
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Tareas de la casa"
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto
                                Text(text = "Tareas de la casa")
                            }
                        }


                        FloatingActionButton(
                            onClick = { /* Acción para Compras */ },
                            containerColor = ccompras,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Compras"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Compras")
                            }
                        }




                        FloatingActionButton(
                            onClick = { navToTareaFacultadPage() },
                            containerColor = cfacultad,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_school),
                                    contentDescription = "Tareas Facultad"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Tareas Facultad")
                            }
                        }
                    }
                }
                FloatingActionButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                    Icon(
                        imageVector = if (isMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Abrir menú"
                    )
                }
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
            // Sección de Exámenes
            Text(
                text = "Exámenes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            when (val examenesList = homeUiState.examenesList) {
                is Resources.Success -> LazyColumn {
                    items(examenesList.data ?: emptyList()) { examen ->
                        ExamenItem(
                            examen = examen,
                            onClick = { onExamenClick(examen.documentId) },
                            onLongClick = {
                                openDialog = true
                                selectedExamen = examen
                            }
                        )
                    }
                }
                is Resources.Loading -> CircularProgressIndicator()
                else -> Text("Error al cargar exámenes", color = Color.Red)
            }

            // Sección de Tareas Facultad
            Text(
                text = "Tareas Facultad",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            when (val tareasList = homeUiState.tareasList) {
                is Resources.Success -> LazyColumn {
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
                is Resources.Loading -> CircularProgressIndicator()
                else -> Text("Error al cargar tareas", color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamenItem(examen: Examenes, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = cexamen)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Examen: ${examen.materia}", fontWeight = FontWeight.Bold)
            Text(text = "Fecha: ${examen.fecha}")
            Text(text = "Hora: ${examen.hora}")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TareaFacultadItem(tarea: TareasFacultad, onClick: () -> Unit, onLongClick: () -> Unit) {
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
            onExamenClick = {},
            onTareaFacultadClick = {},
            navToExamenPage = {},
            navToTareaFacultadPage = {},
            navToLoginPage = {}
        )
    }
}
