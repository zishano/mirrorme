package com.mirrorme.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mirrorme.data.local.database.entity.PersonaProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PersonaProfileEntity)

    @Query("SELECT * FROM persona_profiles ORDER BY generatedAt DESC LIMIT 1")
    fun getLatest(): Flow<PersonaProfileEntity?>
}
