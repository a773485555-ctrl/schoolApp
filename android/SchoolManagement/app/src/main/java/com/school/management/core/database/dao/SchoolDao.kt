package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.SchoolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    @Query("SELECT * FROM schools WHERE school_id = :schoolId")
    fun getById(schoolId: Int): Flow<SchoolEntity?>

    @Upsert
    suspend fun upsert(school: SchoolEntity)
}
