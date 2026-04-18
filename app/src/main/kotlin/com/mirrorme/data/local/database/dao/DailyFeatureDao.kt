package com.mirrorme.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mirrorme.data.local.database.entity.DailyFeatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyFeatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyFeatureEntity)

    @Query("SELECT * FROM daily_features ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<DailyFeatureEntity>>

    @Query("SELECT * FROM daily_features ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<DailyFeatureEntity?>
}
