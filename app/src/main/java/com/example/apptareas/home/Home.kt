package com.example.apptareas.home
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.R
import com.example.apptareas.Utils
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.models.Examenes
import com.example.apptareas.repository.Resources
import com.example.apptareas.ui.theme.AppTareasTheme
import com.example.apptareas.ui.theme.ccasa
import com.example.apptareas.ui.theme.ccompras
import com.example.apptareas.ui.theme.cexamen
import com.example.apptareas.ui.theme.cfacultad
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(
    homeViewMode: HomeViewMode?,
    onExamenClick: (id: String) -> Unit,
    navToExamenPage: () -> Unit,
    navToLoginPage: () -> Unit
) {
    val homeUiState = homeViewMode?.homeUiState ?: HomeUiState()

    var openDialog by remember { mutableStateOf(false) }
    var selectedExamen: Examenes? by remember { mutableStateOf(null) }

    val scrollState = rememberScrollState()

    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeViewMode?.loadExamenes()
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                AnimatedVisibility(visible = isMenuExpanded) {
                    Column(horizontalAlignment = Alignment.End) {
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
                            onClick = { navToExamenPage() },
                            containerColor = cexamen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_book),
                                    contentDescription = "Exámenes"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Exámenes")
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
                            onClick = { /* Acción para Tareas de la facultad */ },
                            containerColor = cfacultad,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_school),
                                    contentDescription = "Tareas de la facultad"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Facultad")
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { isMenuExpanded = !isMenuExpanded }
                ) {
                    Icon(
                        imageVector = if (isMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (isMenuExpanded) "Cerrar menú" else "Abrir menú"
                    )
                }
            }
        }
,
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = {
                        homeViewMode?.signOut()
                        navToLoginPage()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                title = { Text(text = "Home") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val examenesList = homeUiState.examenesList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    val sortedExamenes = examenesList.data?.sortedBy { examen ->
                        parseDateString(examen.fecha)
                    } ?: emptyList()

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(sortedExamenes) { examen ->
                            ExamenItem(
                                examen = examen,
                                onLongClick = {
                                    openDialog = true
                                    selectedExamen = examen
                                },
                            ) {
                                onExamenClick.invoke(examen.documentId)
                            }
                        }
                    }

                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = { openDialog = false },
                            title = { Text(text = "¿Quieres borrar el examen?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedExamen?.documentId?.let {
                                            homeViewMode?.deleteExamen(it)
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text(text = "Borrar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancelar")
                                }
                            }
                        )
                    }
                }

                else -> {
                    Text(
                        text = examenesList.throwable?.localizedMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = homeViewMode?.hasUser) {
        if (homeViewMode?.hasUser == false) {
            navToLoginPage.invoke()
        }
    }
}

fun parseDateString(dateString: String): Long {
    return try {
        // Suponiendo que la fecha está en formato "yyyy-MM-dd"
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        date?.time ?: 0L
    } catch (e: Exception) {
        0L // Si la fecha no tiene un formato válido, considerarla como la más antigua
    }
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamenItem(
    examen: Examenes,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cexamen // Color fijo para todas las tarjetas.
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Título centrado
            Text(
                text = "Examen",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() // Ocupa
            )

            // Espaciador entre el título y el contenido
            Spacer(modifier = Modifier.height(8.dp))

            // Materia
            Text(
                text = examen.materia,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción
            Text(
                text = examen.description,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Fecha
            Text(
                text = examen.fecha,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(4.dp)
            )

            // Espacio para empujar la hora hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // Hora alineada a la derecha
            Text(
                text = examen.hora,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End, // Alineación del texto a la derecha
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth() // Ocupa
                    .padding(4.dp)
            )
        }
    }


}

private fun formatData(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yyyy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

@Preview
@Composable
fun PrevHomeScreen() {
    AppTareasTheme {
        Home(
            homeViewMode = null,
            onExamenClick = {},
            navToExamenPage = {},
            navToLoginPage = {}
        )
    }
}
