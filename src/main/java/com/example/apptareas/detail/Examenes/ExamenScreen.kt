package com.example.apptareas.detail.Examenes

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.apptareas.Utils
import com.example.apptareas.ui.theme.cexamen
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ExamenScreen(
    examenViewModel: ExamenViewModel?,
    examenId: String,
    onNavigate: () -> Unit
) {
    val detailUiState = examenViewModel?.detailUiState ?: DetailUiState()
    val isFormsNotBlank = detailUiState.description.isNotBlank() &&
            detailUiState.materia.isNotBlank() &&
            detailUiState.fecha.isNotBlank() && // Validación de la fecha
            isValidHour(detailUiState.hora) &&
            detailUiState.dia.isNotBlank() // Validación del día

    val isExamenIdNotBlank = examenId.isNotBlank()
    val icon = if (isExamenIdNotBlank) Icons.Default.Refresh else Icons.Default.Check

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            examenViewModel?.onFechaChange(selectedDate)
            calendar.set(year, month, dayOfMonth)
            val dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            examenViewModel?.onDiaChange(dayOfWeek)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(key1 = Unit) {
        if (isExamenIdNotBlank) {
            examenViewModel?.getExamen(examenId)
        } else {
            examenViewModel?.resetState()
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = isFormsNotBlank) {
                FloatingActionButton(
                    onClick = {
                        if (isExamenIdNotBlank) {
                            examenViewModel?.updateExamen(examenId)
                        } else {
                            examenViewModel?.addExamen()
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isExamenIdNotBlank) "Nota Editada Correctamente" else "Nota Agregada Correctamente"
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
            if (detailUiState.examenAddedStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Nota Agregada Correctamente")
                    examenViewModel?.resetExamenAddedStatus()
                    onNavigate.invoke()
                }
            }
            if (detailUiState.updateExamenStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Nota Editada Correctamente")
                    examenViewModel?.resetExamenAddedStatus()
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
                    text = "AGREGAR UN EXAMEN",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }

            // Separación entre el título y los demás campos
            Spacer(modifier = Modifier.height(16.dp))

            // Resto del contenido
            OutlinedTextField(
                value = detailUiState.materia,
                onValueChange = {
                    examenViewModel?.onMateriaChange(it)
                },
                label = { Text(text = "Materia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.description,
                onValueChange = {
                    examenViewModel?.onDescriptionChange(it)
                },
                label = { Text(text = "Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.fecha,
                onValueChange = {}, // Solo seleccionable mediante el DatePickerDialog
                label = { Text(text = "Fecha (AAAA-MM-DD)") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            datePickerDialog.show()
                        }
                    )
                },
                readOnly = true, // No se permite editar manualmente
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.dia,
                onValueChange = {},
                label = { Text(text = "Día de la Semana") },
                readOnly = true, // Se autocompleta desde la fecha
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.hora,
                onValueChange = {
                    examenViewModel?.onHoraChange(it)
                },
                label = { Text(text = "Hora (HH-MM)") },
                isError = !isValidHour(detailUiState.hora),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

// Función para validar la hora en formato "HH-MM"
fun isValidHour(hour: String): Boolean {
    val regex = Regex("^([01]\\d|2[0-3])-[0-5]\\d$") // Expresión regular para "HH-MM"
    return regex.matches(hour)
}






















