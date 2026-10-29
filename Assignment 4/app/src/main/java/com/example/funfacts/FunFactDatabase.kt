package com.example.funfacts

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface FunFactDao {
    @Query("SELECT * FROM facts ORDER BY id DESC")
    fun observeAll(): Flow<List<FunFact>>

    @Insert
    suspend fun insert(fact: FunFact)
}

@Database(entities = [FunFact::class], version = 1, exportSchema = false)
abstract class FunFactDatabase : RoomDatabase() {
    abstract fun funFactDao(): FunFactDao

    companion object {
        @Volatile private var instance: FunFactDatabase? = null

        fun getInstance(context: Context): FunFactDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                FunFactDatabase::class.java,
                "fun_facts.db"
            ).build().also { instance = it }
        }
    }
}
