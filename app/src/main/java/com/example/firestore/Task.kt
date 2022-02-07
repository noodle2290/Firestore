package com.example.firestore

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Task(
    @DocumentId
    val id: String = "",
    val name:String = "",
    val title: String = "",
    var createdAt: Date = Date(System.currentTimeMillis()),
)
