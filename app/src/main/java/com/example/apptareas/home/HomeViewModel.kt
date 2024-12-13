package com.example.apptareas.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptareas.models.Examenes
import com.example.apptareas.repository.Resources
import com.example.apptareas.repository.StorageRepository
import kotlinx.coroutines.launch

class HomeViewMode(
    private val repository: StorageRepository = StorageRepository()
):ViewModel(){
    var homeUiState by mutableStateOf(HomeUiState())

    val user = repository.user()
    val hasUser:Boolean
        get() = repository.hasUser()
    private val userId:String
        get() = repository.getUserId()

    fun loadExamenes() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserExamenes(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                examenesList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserExamenes(userId:String) = viewModelScope.launch {
        repository.getUserExamenes(userId).collect{
            homeUiState = homeUiState.copy(examenesList = it)
        }
    }

    fun deleteExamen(examenId:String) = repository.deleteExamen(examenId){
        homeUiState = homeUiState.copy(examenDeletedStatus = it)

    }

    fun signOut() = repository.signOut()










}

data class HomeUiState(
    val examenesList:Resources<List<Examenes>> = Resources.Loading(),
    val examenDeletedStatus:Boolean = false
)