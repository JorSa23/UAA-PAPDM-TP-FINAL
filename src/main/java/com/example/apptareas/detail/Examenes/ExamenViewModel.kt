package com.example.apptareas.detail.Examenes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.apptareas.models.Examenes
import com.example.apptareas.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class ExamenViewModel(
    private val repository: StorageRepository = StorageRepository()
):ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set

    private val hasUser:Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    fun onColorChange(colorIndex: Int){
        detailUiState = detailUiState.copy(colorIndex = colorIndex)
    }

    fun onMateriaChange(materia: String){
        detailUiState = detailUiState.copy(materia = materia)
    }

    fun onDescriptionChange(description: String){
        detailUiState = detailUiState.copy(description = description)
    }

    fun onFechaChange(fecha: String){
        detailUiState = detailUiState.copy(fecha = fecha)
    }

    fun onDiaChange(dia: String){
        detailUiState = detailUiState.copy(dia = dia)
    }

    fun onHoraChange(hora: String){
        detailUiState = detailUiState.copy(hora = hora)
    }

    fun addExamen(){
        if (hasUser){
            repository.addExamen(
                userId = user!!.uid,
                materia = detailUiState.materia,
                description = detailUiState.description,
                fecha = detailUiState.fecha,
                dia = detailUiState.dia,
                hora = detailUiState.hora,
                color = detailUiState.colorIndex,
                timestamp = Timestamp.now()
            ){
                detailUiState = detailUiState.copy(examenAddedStatus = it)
            }
        }
    }

    fun setEditFields(examen: Examenes){
        detailUiState = detailUiState.copy(
            colorIndex = examen.colorIndex,
            materia = examen.materia,
            description = examen.description,
            fecha = examen.fecha,
            dia = examen.dia,
            hora = examen.hora,
        )
    }

    fun getExamen(examenId:String){
        repository.getExamen(
            examenId = examenId,
            onError = {},
        ){
            detailUiState = detailUiState.copy(selectedExamen = it)
            detailUiState.selectedExamen?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateExamen(
        examenId: String
    ){
        repository.updateExamen(
            materia = detailUiState.materia,
            description = detailUiState.description,
            examenId = examenId,
            fecha = detailUiState.fecha,
            dia = detailUiState.dia,
            hora = detailUiState.hora,
            color = detailUiState.colorIndex
        ){
            detailUiState = detailUiState.copy(updateExamenStatus = it)
        }
    }

    fun resetExamenAddedStatus(){
        detailUiState = detailUiState.copy(
            examenAddedStatus = false,
            updateExamenStatus = false,
        )
    }

    fun resetState(){
        detailUiState = DetailUiState()
    }





}

data class DetailUiState(
    val colorIndex:Int= 0,
    val materia:String="",
    val description:String="",
    val fecha:String="",
    val dia:String="",
    val hora:String="",
    val examenAddedStatus:Boolean = false,
    val updateExamenStatus:Boolean = false,
    val selectedExamen: Examenes? = null

)