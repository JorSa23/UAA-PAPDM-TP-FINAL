package com.example.apptareas.detail.Compras

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.apptareas.detail.Compras.DetailUiState
import com.example.apptareas.detail.Compras.ComprasViewModel
import com.example.apptareas.ui.theme.cexamen
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ComprasScreen(
    comprasViewModel: ComprasViewModel?,
    comprasId: String,
    onNavigate: () -> Unit
) {
    val detailUiState = comprasViewModel?.detailUiState ?: DetailUiState()
    val isFormsNotBlank = detailUiState.producto.isNotBlank() &&
            detailUiState.marca.isNotBlank() &&
            detailUiState.cantidad.isNotBlank()

    val isComprasIdNotBlank = comprasId.isNotBlank()
    val icon = if (isComprasIdNotBlank) Icons.Default.Refresh else Icons.Default.Check

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(key1 = Unit) {
        if (isComprasIdNotBlank) {
            comprasViewModel?.getCompras(comprasId)
        } else {
            comprasViewModel?.resetState()
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = isFormsNotBlank) {
                FloatingActionButton(
                    onClick = {
                        if (isComprasIdNotBlank) {
                            comprasViewModel?.updateCompras(comprasId)
                        } else {
                            comprasViewModel?.addCompras()
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isComprasIdNotBlank) "Nota Editada Correctamente" else "Nota Agregada Correctamente"
                            )
                        }
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = cexamen)
                .padding(padding)
        ) {
            if (detailUiState.comprasAddedStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Nota Agregada Correctamente")
                    comprasViewModel?.resetComprasAddedStatus()
                    onNavigate.invoke()
                }
            }
            if (detailUiState.updateComprasStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Nota Editada Correctamente")
                    comprasViewModel?.resetComprasAddedStatus()
                    onNavigate.invoke()
                }
            }

            // Sección para centrar el título arriba
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // Espaciado superior e inferior
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AGREGAR COMPRA",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }

            // Separación entre el título y los demás campos
            Spacer(modifier = Modifier.height(16.dp))

            // Resto del contenido
            OutlinedTextField(
                value = detailUiState.producto,
                onValueChange = {
                    comprasViewModel?.onProductoChange(it)
                },
                label = { Text(text = "Producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.marca,
                onValueChange = {
                    comprasViewModel?.onMarcaChange(it)
                },
                label = { Text(text = "Marca") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.cantidad,
                onValueChange = {
                    comprasViewModel?.onCantidadChange(it)
                },
                label = { Text(text = "Cantidad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}


