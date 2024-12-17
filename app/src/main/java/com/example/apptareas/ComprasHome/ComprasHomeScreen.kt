package com.example.apptareas.ComprasHome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.apptareas.ComprasHome.ComprasHomeViewModel
import com.example.apptareas.R
import com.example.apptareas.detail.Compras.DetailUiState
import com.example.apptareas.home.HomeUiState
import com.example.apptareas.home.HomeViewMode
import com.example.apptareas.models.Compras
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

fun ComprasHomeScreen(
    comprashomeViewModel: ComprasHomeViewModel?,
    onComprasClick: (id: String) -> Unit,
    navToComprasHomePage: () -> Unit,
    navToComprasPage: () -> Unit,
    navToExamenPage: () -> Unit,
    navToLoginPage: () -> Unit
) {
    val comprasUiState = comprashomeViewModel?.comprasUiState ?: ComprasUiState()

    var openDialog by remember { mutableStateOf(false) }
    var selectedCompras: Compras? by remember { mutableStateOf(null) }

    val scrollState = rememberScrollState()

    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        comprashomeViewModel?.loadCompras()
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
                            onClick = { navToComprasPage() },
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
                        comprashomeViewModel?.signOut()
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
            Button(
                onClick = { navToComprasPage() },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(text = "Agregar Compra")
            }

            when (val comprasList = comprasUiState.comprasList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    val sortedCompras = comprasList.data?.sortedBy { compras ->
                        compras.producto // Ordena alfabéticamente por el campo producto
                    } ?: emptyList()

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(sortedCompras) { compras ->
                            ComprasItem(
                                compras = compras,
                                onLongClick = {
                                    openDialog = true
                                    selectedCompras = compras
                                },
                            ) {
                                onComprasClick.invoke(compras.documentId)
                            }
                        }
                    }

                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = { openDialog = false },
                            title = { Text(text = "¿Quieres borrar el item?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedCompras?.documentId?.let {
                                            comprashomeViewModel?.deleteCompras(it)
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
                        text = comprasList.throwable?.localizedMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = comprashomeViewModel?.hasUser) {
        if (comprashomeViewModel?.hasUser == false) {
            navToLoginPage.invoke()
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComprasItem(
    compras: Compras,
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
            containerColor = ccompras // Color fijo para todas las tarjetas.
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Título centrado
            Text(
                text = "Lista de Compras",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() // Ocupa
            )

            // Espaciador entre el título y el contenido
            Spacer(modifier = Modifier.height(8.dp))

            // Productos
            Text(
                text = compras.producto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Marca
            Text(
                text = compras.marca,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            //Cantidad
            Text(
                text = compras.cantidad,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(4.dp)
            )

        }
    }


}

@Preview
@Composable
fun PrevHomeScreen() {
    AppTareasTheme {
        com.example.apptareas.home.Home(
            homeViewMode = null,
            onExamenClick = {},
            navToExamenPage = {},
            navToComprasPage = {},
            navToLoginPage = {}
        )
    }
}
