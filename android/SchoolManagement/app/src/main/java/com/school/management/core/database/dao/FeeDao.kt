package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.FeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeeDao {

    @Query("SELECT * FROM fees WHERE student_id = :studentId ORDER BY due_date DESC")
    fun getByStudent(studentId: Int): Flow<List<FeeEntity>>

    @Upsert
    suspend fun upsertAll(fees: List<FeeEntity>)
}
