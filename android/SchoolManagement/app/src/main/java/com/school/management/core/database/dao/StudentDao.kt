package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students WHERE class_name = :className AND section = :section AND is_active = 1 ORDER BY full_name ASC")
    fun getByClass(className: String, section: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE is_active = 1 ORDER BY full_name ASC")
    fun getAll(): Flow<List<StudentEntity>>

    @Upsert
    suspend fun upsertAll(students: List<StudentEntity>)

    @Query("SELECT * FROM students WHERE student_id = :studentId")
    suspend fun getById(studentId: Int): StudentEntity?
}
