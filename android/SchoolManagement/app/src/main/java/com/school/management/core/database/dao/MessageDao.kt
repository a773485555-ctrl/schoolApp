package com.school.management.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.school.management.core.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY sent_at DESC")
    fun getAll(): Flow<List<MessageEntity>>

    @Upsert
    suspend fun upsertAll(messages: List<MessageEntity>)

    @Query("UPDATE messages SET is_read = 1 WHERE message_id = :messageId")
    suspend fun markRead(messageId: Int)
}
