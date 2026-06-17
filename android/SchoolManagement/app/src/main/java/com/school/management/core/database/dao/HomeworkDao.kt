package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.HomeworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {

    @Query("SELECT * FROM homework WHERE class_name = :className AND section = :section ORDER BY due_date DESC")
    fun getByClass(className: String, section: String): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework WHERE sync_status != 'synced'")
    suspend fun getPendingSync(): List<HomeworkEntity>

    @Upsert
    suspend fun upsertAll(homework: List<HomeworkEntity>)

    @Query("UPDATE homework SET sync_status = 'synced' WHERE homework_id = :homeworkId")
    suspend fun markSynced(homeworkId: Int)

    @Query("DELETE FROM homework WHERE homework_id = :homeworkId")
    suspend fun delete(homeworkId: Int)

    @Query("DELETE FROM homework")
    suspend fun deleteAll()
}
