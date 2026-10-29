package com.example.funfacts

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "facts")
data class FunFact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String
)

// Only read the fact text from the API. Room creates its own ID.
@Serializable
data class FunFactResponse(val text: String)
