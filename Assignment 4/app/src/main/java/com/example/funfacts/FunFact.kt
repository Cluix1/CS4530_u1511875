package com.example.funfacts

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "facts")
data class FunFact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String
)

// The API response is separate so its fields do not interfere with Room's local ID.
@Serializable
data class FunFactResponse(val text: String)
