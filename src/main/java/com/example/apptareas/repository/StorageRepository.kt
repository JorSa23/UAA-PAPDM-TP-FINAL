package com.example.apptareas.repository

import com.example.apptareas.models.Examenes
import com.example.apptareas.models.TareasFacultad
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val EXAMENES_COLLECTION_REF = "examenes"
const val TAREASFACULTAD_COLLECTION_REF = "tareasFacultad" // Nueva colección

class StorageRepository() {
    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val examenesRef: CollectionReference = Firebase
        .firestore.collection(EXAMENES_COLLECTION_REF)

    private val tareasFacultadRef: CollectionReference = Firebase
        .firestore.collection(TAREASFACULTAD_COLLECTION_REF)

    // ======================= MÉTODOS PARA EXÁMENES ==========================
    fun getUserExamenes(userId: String): Flow<Resources<List<Examenes>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = examenesRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val examenes = snapshot.toObjects(Examenes::class.java)
                        Resources.Success(data = examenes)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }

        } catch (e: Exception) {
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose { snapshotStateListener?.remove() }
    }

    fun getExamen(examenId: String, onError: (Throwable?) -> Unit, onSuccess: (Examenes?) -> Unit) {
        examenesRef
            .document(examenId)
            .get()
            .addOnSuccessListener { onSuccess.invoke(it.toObject(Examenes::class.java)) }
            .addOnFailureListener { result -> onError.invoke(result.cause) }
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
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = examenesRef.document().id
        val examen = Examenes(
            userId, materia, description, fecha, dia, hora, timestamp, colorIndex = color, documentId = documentId
        )
        examenesRef.document(documentId).set(examen).addOnCompleteListener { result ->
            onComplete.invoke(result.isSuccessful)
        }
    }

    fun updateExamen(
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        color: Int,
        examenId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to description,
            "materia" to materia,
            "fecha" to fecha,
            "dia" to dia,
            "hora" to hora
        )

        examenesRef.document(examenId).update(updateData).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }

    fun deleteExamen(examenId: String, onComplete: (Boolean) -> Unit) {
        examenesRef.document(examenId).delete().addOnCompleteListener {
            onComplete.invoke(it.isSuccessful)
        }
    }

    // ======================= MÉTODOS PARA TAREASFACULTAD ==========================
    fun getUserTareasFacultad(userId: String): Flow<Resources<List<TareasFacultad>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = tareasFacultadRef
                //.orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val tareas = snapshot.toObjects(TareasFacultad::class.java)
                        Resources.Success(data = tareas)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }

        } catch (e: Exception) {
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose { snapshotStateListener?.remove() }
    }

    fun getTareaFacultad(
        tareaId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (TareasFacultad?) -> Unit
    ) {
        tareasFacultadRef.document(tareaId).get()
            .addOnSuccessListener { onSuccess.invoke(it.toObject(TareasFacultad::class.java)) }
            .addOnFailureListener { result -> onError.invoke(result.cause) }
    }

    fun addTareaFacultad(
        userId: String,
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = tareasFacultadRef.document().id
        val tarea = TareasFacultad(
            userId, materia, description, fecha, dia, hora, timestamp, colorIndex = color, documentId = documentId
        )
        tareasFacultadRef.document(documentId).set(tarea).addOnCompleteListener { result ->
            onComplete.invoke(result.isSuccessful)
        }
    }

    fun updateTareaFacultad(
        materia: String,
        description: String,
        fecha: String,
        dia: String,
        hora: String,
        color: Int,
        tareaId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to description,
            "materia" to materia,
            "fecha" to fecha,
            "dia" to dia,
            "hora" to hora
        )

        tareasFacultadRef.document(tareaId).update(updateData).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }

    fun deleteTareaFacultad(tareaId: String, onComplete: (Boolean) -> Unit) {
        tareasFacultadRef.document(tareaId).delete().addOnCompleteListener {
            onComplete.invoke(it.isSuccessful)
        }
    }

    fun signOut() = Firebase.auth.signOut()
}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}
