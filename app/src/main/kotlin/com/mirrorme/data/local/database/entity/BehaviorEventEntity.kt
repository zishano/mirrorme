package com.mirrorme.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mirrorme.domain.model.BehaviorEvent
import com.mirrorme.domain.model.EventType

@Entity(
    tableName = "behavior_events",
    indices = [Index("timestamp"), Index("type")]
)
data class BehaviorEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val type: String,
    val value: Double,
    val metadata: String
) {
    fun toDomain() = BehaviorEvent(
        id = id,
        timestamp = timestamp,
        type = EventType.valueOf(type),
        value = value,
        metadata = metadata
    )

    companion object {
        fun fromDomain(e: BehaviorEvent) = BehaviorEventEntity(
            id = e.id,
            timestamp = e.timestamp,
            type = e.type.name,
            value = e.value,
            metadata = e.metadata
        )
    }
}
