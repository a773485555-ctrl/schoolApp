package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.AbsenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsenceDao {

    @Query("SELECT * FROM absences WHERE student_id = :studentId ORDER BY absence_date DESC")
    fun getByStudent(studentId: Int): Flow<List<AbsenceEntity>>

    @Query("SELECT * FROM absences WHERE absence_date = :date AND subject_id = :subjectId ORDER BY student_id ASC")
    fun getForClassDate(subjectId: Int, date: String): Flow<List<AbsenceEntity>>

    @Query("SELECT * FROM absences WHERE student_id = :studentId AND absence_date BETWEEN :startDate AND :endDate ORDER BY absence_date ASC")
    fun getByMonth(studentId: Int, startDate: String, endDate: String): Flow<List<AbsenceEntity>>

    @Query("SELECT * FROM absences WHERE sync_status != 'synced'")
    suspend fun getPendingSync(): List<AbsenceEntity>

    @Upsert
    suspend fun upsertAll(absences: List<AbsenceEntity>)

    @Query("UPDATE absences SET sync_status = 'synced' WHERE absence_id = :absenceId")
    suspend fun markSynced(absenceId: Int)
}
