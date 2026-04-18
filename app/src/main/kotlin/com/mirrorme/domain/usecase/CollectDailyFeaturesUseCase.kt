package com.mirrorme.domain.usecase

import com.mirrorme.domain.model.AppCategory
import com.mirrorme.domain.model.BehaviorEvent
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.domain.model.EventType
import com.mirrorme.domain.repository.BehaviorRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.ln

class CollectDailyFeaturesUseCase @Inject constructor(
    private val repository: BehaviorRepository
) {
    suspend operator fun invoke(targetDayMs: Long): DailyFeatures {
        val (dayStart, dayEnd) = dayBounds(targetDayMs)
        val events = repository.getEventsForDay(dayStart, dayEnd).first()
        return buildFeatures(events, dayStart)
    }

    private fun buildFeatures(events: List<BehaviorEvent>, dayStart: Long): DailyFeatures {
        val unlocks = events.filter { it.type == EventType.SCREEN_UNLOCK }
        val firstUnlock = unlocks.minByOrNull { it.timestamp }

        val appEvents = events.filter { it.type == EventType.APP_FOREGROUND }
        val categoryMap = appEvents.groupBy { parseCategory(it.metadata) }
        val totalAppMs = appEvents.sumOf { it.value }.toLong().coerceAtLeast(1L)

        val screenOnEvents = events.filter { it.type == EventType.SCREEN_ON }
        val screenOffEvents = events.filter { it.type == EventType.SCREEN_OFF }
        val nightScreenMs = nightScreenTime(screenOnEvents, screenOffEvents)

        val calls = events.filter { it.type == EventType.CALL_END }

        return DailyFeatures(
            date = dayStart,
            screenUnlockCount = unlocks.size,
            totalScreenOnMinutes = (events.filter { it.type == EventType.SCREEN_ON }
                .sumOf { it.value } / 60_000).toInt(),
            nightScreenMinutes = (nightScreenMs / 60_000).toInt(),
            firstUnlockHour = firstUnlock?.let {
                Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.HOUR_OF_DAY)
            } ?: -1,
            topAppCategory = categoryMap.maxByOrNull { it.value.sumOf { e -> e.value } }?.key
                ?: AppCategory.OTHER,
            socialAppRatio = (categoryMap[AppCategory.SOCIAL]?.sumOf { it.value } ?: 0.0) / totalAppMs,
            productivityAppRatio = (categoryMap[AppCategory.PRODUCTIVITY]?.sumOf { it.value } ?: 0.0) / totalAppMs,
            entertainmentAppRatio = (categoryMap[AppCategory.ENTERTAINMENT]?.sumOf { it.value } ?: 0.0) / totalAppMs,
            uniqueAppsUsed = appEvents.map { it.metadata }.toSet().size,
            stepCount = events.lastOrNull { it.type == EventType.STEP_COUNT }?.value?.toInt() ?: 0,
            locationEntropy = computeLocationEntropy(events.filter { it.type == EventType.LOCATION_CHANGE }),
            outdoorMinutes = 0, // 由传感器融合计算，占位
            notificationsReceived = events.count { it.type == EventType.NOTIFICATION_RECEIVED },
            avgNotificationResponseSec = avgResponseTime(events),
            notificationDismissRatio = dismissRatio(events),
            callCount = calls.size,
            totalCallMinutes = (calls.sumOf { it.value } / 60_000).toInt(),
            chargeStartHour = events.firstOrNull { it.type == EventType.CHARGE_START }?.let {
                Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.HOUR_OF_DAY)
            } ?: -1,
            batteryLowEvents = events.count { it.type == EventType.CHARGE_START && it.value < 15 }
        )
    }

    private fun computeLocationEntropy(locationEvents: List<BehaviorEvent>): Double {
        if (locationEvents.isEmpty()) return 0.0
        val counts = locationEvents.groupBy { it.metadata }.values.map { it.size }
        val total = counts.sum().toDouble()
        return -counts.sumOf { c -> (c / total) * ln(c / total) }
    }

    private fun avgResponseTime(events: List<BehaviorEvent>): Int {
        val received = events.filter { it.type == EventType.NOTIFICATION_RECEIVED }
        val dismissed = events.filter { it.type == EventType.NOTIFICATION_DISMISSED }
        if (received.isEmpty()) return 0
        val pairs = received.zip(dismissed.take(received.size))
        val avgMs = pairs.map { (r, d) -> d.timestamp - r.timestamp }.average()
        return (avgMs / 1000).toInt()
    }

    private fun dismissRatio(events: List<BehaviorEvent>): Double {
        val received = events.count { it.type == EventType.NOTIFICATION_RECEIVED }
        val dismissed = events.count { it.type == EventType.NOTIFICATION_DISMISSED }
        if (received == 0) return 0.0
        return dismissed.toDouble() / received
    }

    private fun nightScreenTime(onEvents: List<BehaviorEvent>, offEvents: List<BehaviorEvent>): Long {
        var total = 0L
        val nightStart = 23 * 3_600_000L
        val nightEnd = 30 * 3_600_000L // 06:00 次日
        for (on in onEvents) {
            val off = offEvents.firstOrNull { it.timestamp > on.timestamp } ?: continue
            val start = on.timestamp % 86_400_000L
            val end = off.timestamp % 86_400_000L
            if (start >= nightStart || end <= 6 * 3_600_000L) {
                total += off.timestamp - on.timestamp
            }
        }
        return total
    }

    private fun parseCategory(packageName: String): AppCategory {
        return when {
            packageName.containsAny("wechat", "whatsapp", "telegram", "instagram", "weibo",
                "twitter", "facebook", "tiktok", "douyin") -> AppCategory.SOCIAL
            packageName.containsAny("youtube", "netflix", "bilibili", "iqiyi", "music",
                "spotify", "game", "games") -> AppCategory.ENTERTAINMENT
            packageName.containsAny("chrome", "browser", "news", "zhihu",
                "weixin.read") -> AppCategory.NEWS
            packageName.containsAny("office", "docs", "notion", "calendar", "email",
                "mail", "slack", "zoom", "teams") -> AppCategory.PRODUCTIVITY
            packageName.containsAny("bank", "pay", "alipay", "wallet") -> AppCategory.FINANCE
            packageName.containsAny("health", "fitness", "workout", "sleep") -> AppCategory.HEALTH
            packageName.containsAny("taobao", "jd", "amazon", "shop") -> AppCategory.SHOPPING
            packageName.containsAny("learn", "course", "study", "edu") -> AppCategory.EDUCATION
            else -> AppCategory.OTHER
        }
    }

    private fun dayBounds(ms: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = ms }
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        return Pair(start, start + 86_400_000L)
    }

    private fun String.containsAny(vararg keywords: String) =
        keywords.any { this.lowercase().contains(it) }
}
