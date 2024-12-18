package com.example.apptareas.ComprasHome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptareas.home.HomeUiState
import com.example.apptareas.models.Compras
import com.example.apptareas.models.Examenes
import com.example.apptareas.repository.Resources
import com.example.apptareas.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ComprasHomeViewModel(
    private val repository: StorageRepository = StorageRepository()
): ViewModel() {

    var comprasUiState by mutableStateOf(ComprasUiState())

    val user = repository.user()
    val hasUser:Boolean
        get() = repository.hasUser()
    private val userId:String
        get() = repository.getUserId()

    fun loadCompras() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserCompras(userId)
            }
        } else {
            comprasUiState = comprasUiState.copy(
                comprasList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

    private fun getUserCompras(userId: String) = viewModelScope.launch {
        repository.getUserCompras(userId).collect {
            println("Datos obtenidos: $it") // Registro para depurar
            comprasUiState = comprasUiState.copy(comprasList = it)
        }
    }

    fun deleteCompras(comprasId:String) = repository.deleteCompras(comprasId){
        comprasUiState = comprasUiState.copy(comprasDeletedStatus = it)
    }
    fun signOut() = repository.signOut()
}

data class ComprasUiState(
    val comprasList:Resources<List<Compras>> = Resources.Loading(),
    val comprasDeletedStatus:Boolean = false

)