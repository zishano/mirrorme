package com.mirrorme.data.local.sensor

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.mirrorme.domain.model.BehaviorEvent
import com.mirrorme.domain.model.EventType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsCollector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    fun collectSince(sinceMs: Long): List<BehaviorEvent> {
        val events = mutableListOf<BehaviorEvent>()
        val now = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents(sinceMs, now)
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> events.add(
                    BehaviorEvent(
                        timestamp = event.timeStamp,
                        type = EventType.APP_FOREGROUND,
                        value = 0.0,
                        metadata = event.packageName ?: ""
                    )
                )
                UsageEvents.Event.ACTIVITY_PAUSED -> events.add(
                    BehaviorEvent(
                        timestamp = event.timeStamp,
                        type = EventType.APP_BACKGROUND,
                        value = 0.0,
                        metadata = event.packageName ?: ""
                    )
                )
                UsageEvents.Event.SCREEN_INTERACTIVE -> events.add(
                    BehaviorEvent(timestamp = event.timeStamp, type = EventType.SCREEN_ON, value = 0.0)
                )
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> events.add(
                    BehaviorEvent(timestamp = event.timeStamp, type = EventType.SCREEN_OFF, value = 0.0)
                )
                UsageEvents.Event.KEYGUARD_HIDDEN -> events.add(
                    BehaviorEvent(timestamp = event.timeStamp, type = EventType.SCREEN_UNLOCK, value = 0.0)
                )
            }
        }

        // 补充 App 使用时长（前后台配对）
        return enrichWithDuration(events)
    }

    private fun enrichWithDuration(events: List<BehaviorEvent>): List<BehaviorEvent> {
        val result = events.toMutableList()
        val foregroundMap = mutableMapOf<String, Long>()

        for (e in events.sortedBy { it.timestamp }) {
            when (e.type) {
                EventType.APP_FOREGROUND -> foregroundMap[e.metadata] = e.timestamp
                EventType.APP_BACKGROUND -> {
                    val startMs = foregroundMap.remove(e.metadata)
                    if (startMs != null) {
                        val idx = result.indexOf(e)
                        result[idx] = e.copy(value = (e.timestamp - startMs).toDouble())
                    }
                }
                else -> {}
            }
        }
        return result
    }
}
