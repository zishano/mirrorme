package com.mirrorme.domain.model

data class BehaviorEvent(
    val id: Long = 0,
    val timestamp: Long,
    val type: EventType,
    val value: Double,
    val metadata: String = ""
)

enum class EventType {
    SCREEN_UNLOCK,
    APP_FOREGROUND,
    APP_BACKGROUND,
    STEP_COUNT,
    LOCATION_CHANGE,
    NOTIFICATION_RECEIVED,
    NOTIFICATION_DISMISSED,
    CALL_START,
    CALL_END,
    SCREEN_OFF,
    SCREEN_ON,
    CHARGE_START,
    CHARGE_END
}
