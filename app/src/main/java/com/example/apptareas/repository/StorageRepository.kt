package com.example.apptareas.repository

import com.example.apptareas.models.Compras
import com.example.apptareas.models.Examenes
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val EXAMENES_COLLECTION_REF = "examenes"

const val COMPRAS_COLLECTION_REF = "compras"

class StorageRepository(){
    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId():String = Firebase.auth.currentUser?.uid.orEmpty()

    private val examenesRef:CollectionReference = Firebase
        .firestore.collection(EXAMENES_COLLECTION_REF)

    private val comprasRef:CollectionReference = Firebase
        .firestore.collection(COMPRAS_COLLECTION_REF)

    fun getUserExamenes(
        userId:String
    ): Flow<Resources<List<Examenes>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try{
            snapshotStateListener = examenesRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener{ snapshot,e ->
                    val response = if(snapshot !=null){
                        val examenes = snapshot.toObjects(Examenes::class.java)
                        Resources.Success(data = examenes)
                    }else{
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }

        }catch (e:Exception){
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose{
            snapshotStateListener?.remove()
        }
    }

    fun getExamen(
        examenId:String,
        onError:(Throwable?) -> Unit,
        onSuccess:(Examenes?) -> Unit,
    ) {
        examenesRef
            .document(examenId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it.toObject(Examenes::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addExamen(
        userId: String,
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit,
    ){
        val documentId = examenesRef.document().id
        val examen = Examenes(
            userId,
            materia,
            description,
            fecha,
            dia,
            hora,
            timestamp,
            colorIndex = color,
            documentId = documentId
        )
        examenesRef
            .document(documentId)
            .set(examen)
            .addOnCompleteListener{ result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteExamen(examenId: String, onComplete: (Boolean) -> Unit){
        examenesRef.document(examenId)
            .delete()
            .addOnCompleteListener{
                onComplete.invoke(it.isSuccessful)
            }

    }

    fun updateExamen(
        materia: String,
        description:String,
        fecha:String,
        dia: String,
        hora: String,
        color: Int,
        examenId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "colorIndex" to color,
            "description" to description,
            "materia" to materia,
            "fecha" to fecha,
            "dia" to dia,
            "hora" to hora
        )

        examenesRef.document(examenId)
            .update(updateData)
            .addOnCompleteListener{
                onResult(it.isSuccessful)
            }
    }

//Compras
fun getUserCompras(
    userId: String
): Flow<Resources<List<Compras>>> = callbackFlow {
    var snapshotStateListener: ListenerRegistration? = null

    try {
        snapshotStateListener = comprasRef
            // Ordena por "producto"
            .orderBy("producto")
            // Filtra por "userId"
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    // Convierte los resultados a una lista de objetos Compras
                    val compras = snapshot.toObjects(Compras::class.java)
                    Resources.Success(data = compras)
                } else {
                    Resources.Error(throwable = e?.cause)
                }
                trySend(response)
            }

    } catch (e: Exception) {
        trySend(Resources.Error(e?.cause))
        e.printStackTrace()
    }
    awaitClose {
        snapshotStateListener?.remove()
    }
}


    fun getCompras(
        comprasId:String,
        onError:(Throwable?) -> Unit,
        onSuccess:(Compras?) -> Unit,
    ) {
        comprasRef
            .document(comprasId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it.toObject(Compras::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addCompras(
            userId: String,
            producto: String,
            marca: String,
            cantidad: String,
            onComplete: (Boolean) -> Unit,
    ){
        val documentId = comprasRef.document().id
        val compras = Compras(
            userId,
            producto,
            marca,
            cantidad,
            documentId = documentId
            )
        comprasRef
            .document(documentId)
            .set(compras)
            .addOnCompleteListener{ result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteCompras(comprasId: String, onComplete: (Boolean) -> Unit){
        comprasRef.document(comprasId)
            .delete()
            .addOnCompleteListener{
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateCompras(
        producto: String,
        marca:String,
        cantidad:String,
        comprasId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "producto" to producto,
            "marca" to marca,
            "cantidad" to cantidad,
        )

        comprasRef.document(comprasId)
            .update(updateData)
            .addOnCompleteListener{
                onResult(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()


}


sealed class  Resources <T>(
    val data:T? = null,
    val throwable: Throwable? = null,
    ){
    class Loading<T>:Resources<T>()
    class Success<T>(data: T?):Resources<T>(data = data)
    class Error<T>(throwable: Throwable?):Resources<T>(throwable = throwable)
}