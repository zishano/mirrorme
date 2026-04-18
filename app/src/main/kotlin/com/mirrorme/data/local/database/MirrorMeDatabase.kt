package com.mirrorme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mirrorme.data.local.database.dao.BehaviorEventDao
import com.mirrorme.data.local.database.dao.DailyFeatureDao
import com.mirrorme.data.local.database.dao.PersonaProfileDao
import com.mirrorme.data.local.database.entity.BehaviorEventEntity
import com.mirrorme.data.local.database.entity.DailyFeatureEntity
import com.mirrorme.data.local.database.entity.PersonaProfileEntity

@Database(
    entities = [
        BehaviorEventEntity::class,
        DailyFeatureEntity::class,
        PersonaProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MirrorMeDatabase : RoomDatabase() {
    abstract fun behaviorEventDao(): BehaviorEventDao
    abstract fun dailyFeatureDao(): DailyFeatureDao
    abstract fun personaProfileDao(): PersonaProfileDao
}
