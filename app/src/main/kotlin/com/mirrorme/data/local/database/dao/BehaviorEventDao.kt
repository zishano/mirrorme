package com.mirrorme.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mirrorme.data.local.database.entity.BehaviorEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BehaviorEventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: BehaviorEventEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<BehaviorEventEntity>)

    @Query("SELECT * FROM behavior_events WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    fun getEventsBetween(start: Long, end: Long): Flow<List<BehaviorEventEntity>>

    @Query("DELETE FROM behavior_events WHERE timestamp < :beforeMs")
    suspend fun deleteOlderThan(beforeMs: Long)

    @Query("SELECT COUNT(*) FROM behavior_events")
    suspend fun count(): Int
}
