package com.mirrorme.domain.repository

import com.mirrorme.domain.model.BehaviorEvent
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.domain.model.PersonaProfile
import kotlinx.coroutines.flow.Flow

interface BehaviorRepository {
    suspend fun insertEvent(event: BehaviorEvent)
    suspend fun insertEvents(events: List<BehaviorEvent>)
    fun getEventsForDay(dayStartMs: Long, dayEndMs: Long): Flow<List<BehaviorEvent>>
    suspend fun saveDailyFeatures(features: DailyFeatures)
    fun getDailyFeatures(limit: Int = 30): Flow<List<DailyFeatures>>
    fun getLatestFeatures(): Flow<DailyFeatures?>
    suspend fun savePersonaProfile(profile: PersonaProfile)
    fun getLatestPersonaProfile(): Flow<PersonaProfile?>
    suspend fun deleteEventsOlderThan(timestampMs: Long)
}
