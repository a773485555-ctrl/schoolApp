package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.TeacherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {

    @Query("SELECT * FROM teachers WHERE is_active = 1 ORDER BY full_name ASC")
    fun getAll(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE teacher_id = :teacherId")
    suspend fun getById(teacherId: Int): TeacherEntity?

    @Upsert
    suspend fun upsertAll(teachers: List<TeacherEntity>)

    @Delete
    suspend fun delete(teacher: TeacherEntity)
}
