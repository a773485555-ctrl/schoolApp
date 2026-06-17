package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects WHERE teacher_id = :teacherId ORDER BY subject_name ASC")
    fun getByTeacher(teacherId: Int): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE class_name = :className AND section = :section ORDER BY subject_name ASC")
    fun getByClassSection(className: String, section: String): Flow<List<SubjectEntity>>

    @Upsert
    suspend fun upsertAll(subjects: List<SubjectEntity>)
}
