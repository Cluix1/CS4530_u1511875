package com.example.courseapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY id")
    fun observeAll(): Flow<List<Course>>

    @Insert
    suspend fun insert(course: Course)

    @Update
    suspend fun update(course: Course)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteById(courseId: Long)
}

@Database(entities = [Course::class], version = 1, exportSchema = false)
abstract class CourseDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile private var instance: CourseDatabase? = null

        fun getInstance(context: Context): CourseDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CourseDatabase::class.java,
                "courses.db"
            ).build().also { instance = it }
        }
    }
}
