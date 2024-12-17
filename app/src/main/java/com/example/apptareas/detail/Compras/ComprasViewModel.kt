package com.example.apptareas.detail.Compras

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptareas.home.HomeUiState
import com.example.apptareas.models.Compras
import com.example.apptareas.repository.Resources
import com.example.apptareas.repository.StorageRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ComprasViewModel(
    private val repository: StorageRepository = StorageRepository()
): ViewModel() {

    var detailUiState by mutableStateOf(DetailUiState())
        private set

    private val userId:String
        get() = repository.getUserId()

    private val hasUser:Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    var homeUiState by mutableStateOf(HomeUiState())

    fun onColorChange(colorIndex: Int){
        detailUiState = detailUiState.copy(colorIndex = colorIndex)
    }

    fun onProductoChange(producto: String){
        detailUiState = detailUiState.copy(producto = producto)
    }

    fun onMarcaChange(marca: String){
        detailUiState = detailUiState.copy(marca = marca)
    }

    fun onCantidadChange(cantidad: String){
        detailUiState = detailUiState.copy(cantidad = cantidad)
    }


    fun addCompras(){
        if (hasUser){
            repository.addCompras(
                userId = user!!.uid,
                producto = detailUiState.producto,
                marca = detailUiState.marca,
                cantidad = detailUiState.cantidad
            ){
                detailUiState = detailUiState.copy(comprasAddedStatus = it)
            }
        }
    }

    fun setEditFields(compras: Compras){
        detailUiState = detailUiState.copy(
            producto = compras.producto,
            marca = compras.marca,
            cantidad = compras.cantidad,
        )
    }

    fun getCompras(comprasId:String){
        repository.getCompras(
            comprasId = comprasId,
            onError = {},
        ){
            detailUiState = detailUiState.copy(selectedCompras = it)
            detailUiState.selectedCompras?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateCompras(
        comprasId: String
    ){
        repository.updateCompras(
            producto = detailUiState.producto,
            marca = detailUiState.marca,
            cantidad = detailUiState.cantidad,
            comprasId = comprasId,

        ){
            detailUiState = detailUiState.copy(updateComprasStatus = it)
        }
    }

    fun resetComprasAddedStatus(){
        detailUiState = detailUiState.copy(
            comprasAddedStatus = false,
            updateComprasStatus = false,
        )
    }

    fun resetState(){
        detailUiState = DetailUiState()
    }
    fun loadCompras() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserCompras(userId)
            }
        } else {
            detailUiState = detailUiState.copy(
                comprasList = Resources.Error(
                    throwable = Throwable(message = "Usuario no está logeado")
                )
            )
        }
    }

//    private fun getUserCompras(userId:String) = viewModelScope.launch {
//        repository.getUserCompras(userId).collect{
//            detailUiState = detailUiState.copy(comprasList = it)
//        }
//    }

    private fun getUserCompras(userId: String) = viewModelScope.launch {
        repository.getUserCompras(userId).collect {
            println("Datos obtenidos: $it") // Registro para depurar
            detailUiState = detailUiState.copy(comprasList = it)
        }
    }


    fun deleteCompras(comprasId:String) = repository.deleteCompras(comprasId){
        detailUiState = detailUiState.copy(comprasDeletedStatus = it)

    }

    fun signOut() = repository.signOut()
}


data class DetailUiState(
    val colorIndex:Int= 0,
    val producto:String="",
    val marca:String="",
    val cantidad:String="",
    val comprasAddedStatus:Boolean = false,
    val updateComprasStatus:Boolean = false,
    val selectedCompras: Compras? = null,
    val comprasList:Resources<List<Compras>> = Resources.Loading(),
    val comprasDeletedStatus:Boolean = false

)