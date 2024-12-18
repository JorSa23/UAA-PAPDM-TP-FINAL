package com.example.apptareas.models

import com.google.firebase.Timestamp

data class Compras(
    val userId:String = "",
    val producto:String = "",
    val marca:String = "",
    val cantidad:String = "",
    val documentId:String = "",
)
