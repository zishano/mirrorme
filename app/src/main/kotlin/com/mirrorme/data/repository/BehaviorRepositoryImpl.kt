package com.mirrorme.data.repository

import com.mirrorme.data.local.database.MirrorMeDatabase
import com.mirrorme.data.local.database.entity.BehaviorEventEntity
import com.mirrorme.data.local.database.entity.DailyFeatureEntity
import com.mirrorme.data.local.database.entity.PersonaProfileEntity
import com.mirrorme.domain.model.BehaviorEvent
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.repository.BehaviorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BehaviorRepositoryImpl @Inject constructor(
    private val db: MirrorMeDatabase
) : BehaviorRepository {

    override suspend fun insertEvent(event: BehaviorEvent) =
        db.behaviorEventDao().insert(BehaviorEventEntity.fromDomain(event))

    override suspend fun insertEvents(events: List<BehaviorEvent>) =
        db.behaviorEventDao().insertAll(events.map { BehaviorEventEntity.fromDomain(it) })

    override fun getEventsForDay(dayStartMs: Long, dayEndMs: Long): Flow<List<BehaviorEvent>> =
        db.behaviorEventDao().getEventsBetween(dayStartMs, dayEndMs)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun saveDailyFeatures(features: DailyFeatures) =
        db.dailyFeatureDao().upsert(DailyFeatureEntity.fromDomain(features))

    override fun getDailyFeatures(limit: Int): Flow<List<DailyFeatures>> =
        db.dailyFeatureDao().getRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun getLatestFeatures(): Flow<DailyFeatures?> =
        db.dailyFeatureDao().getLatest().map { it?.toDomain() }

    override suspend fun savePersonaProfile(profile: PersonaProfile) =
        db.personaProfileDao().upsert(PersonaProfileEntity.fromDomain(profile))

    override fun getLatestPersonaProfile(): Flow<PersonaProfile?> =
        db.personaProfileDao().getLatest().map { it?.toDomain() }

    override suspend fun deleteEventsOlderThan(timestampMs: Long) =
        db.behaviorEventDao().deleteOlderThan(timestampMs)
}
