package com.example.apptareas.detail.TareasFacultad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
//import com.example.apptareas.models.Examenes
import com.example.apptareas.models.TareasFacultad
import com.example.apptareas.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class TareasFacultadViewModel(
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

    fun addTareasFacultad(){
        if (hasUser){
            repository.addTareaFacultad(
                userId = user!!.uid,
                materia = detailUiState.materia,
                description = detailUiState.description,
                fecha = detailUiState.fecha,
                dia = detailUiState.dia,
                hora = detailUiState.hora,
                color = detailUiState.colorIndex,
                timestamp = Timestamp.now()
            ){
                detailUiState = detailUiState.copy(TareasFacultadAddedStatus = it)
            }
        }
    }

    fun setEditFields(tareaFacultad: TareasFacultad){
        detailUiState = detailUiState.copy(
            colorIndex = tareaFacultad.colorIndex,
            materia = tareaFacultad.materia,
            description = tareaFacultad.description,
            fecha = tareaFacultad.fecha,
            dia = tareaFacultad.dia,
            hora = tareaFacultad.hora,
        )
    }

    fun getTareasFacultad(TareasFacultadId:String){
        repository.getTareaFacultad(
            tareaId = TareasFacultadId,
            onError = {},
        ){
            detailUiState = detailUiState.copy(selectedTareasFacultad = it)
            detailUiState.selectedTareasFacultad?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateTareasFacultad(
        TareasFacultadId: String
    ){
        repository.updateTareaFacultad(
            materia = detailUiState.materia,
            description = detailUiState.description,
            tareaId = TareasFacultadId,
            fecha = detailUiState.fecha,
            dia = detailUiState.dia,
            hora = detailUiState.hora,
            color = detailUiState.colorIndex
        ){
            detailUiState = detailUiState.copy(updateTareasFacultadStatus = it)
        }
    }

    fun resetTareasFacultadAddedStatus(){
        detailUiState = detailUiState.copy(
            TareasFacultadAddedStatus = false,
            updateTareasFacultadStatus = false,
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
    val TareasFacultadAddedStatus:Boolean = false,
    val updateTareasFacultadStatus:Boolean = false,
    val selectedTareasFacultad: TareasFacultad? = null

)