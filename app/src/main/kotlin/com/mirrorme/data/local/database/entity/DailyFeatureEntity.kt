package com.mirrorme.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mirrorme.domain.model.AppCategory
import com.mirrorme.domain.model.DailyFeatures

@Entity(tableName = "daily_features")
data class DailyFeatureEntity(
    @PrimaryKey val date: Long,
    val screenUnlockCount: Int,
    val totalScreenOnMinutes: Int,
    val nightScreenMinutes: Int,
    val firstUnlockHour: Int,
    val topAppCategory: String,
    val socialAppRatio: Double,
    val productivityAppRatio: Double,
    val entertainmentAppRatio: Double,
    val uniqueAppsUsed: Int,
    val stepCount: Int,
    val locationEntropy: Double,
    val outdoorMinutes: Int,
    val notificationsReceived: Int,
    val avgNotificationResponseSec: Int,
    val notificationDismissRatio: Double,
    val callCount: Int,
    val totalCallMinutes: Int,
    val chargeStartHour: Int,
    val batteryLowEvents: Int
) {
    fun toDomain() = DailyFeatures(
        date = date,
        screenUnlockCount = screenUnlockCount,
        totalScreenOnMinutes = totalScreenOnMinutes,
        nightScreenMinutes = nightScreenMinutes,
        firstUnlockHour = firstUnlockHour,
        topAppCategory = AppCategory.valueOf(topAppCategory),
        socialAppRatio = socialAppRatio,
        productivityAppRatio = productivityAppRatio,
        entertainmentAppRatio = entertainmentAppRatio,
        uniqueAppsUsed = uniqueAppsUsed,
        stepCount = stepCount,
        locationEntropy = locationEntropy,
        outdoorMinutes = outdoorMinutes,
        notificationsReceived = notificationsReceived,
        avgNotificationResponseSec = avgNotificationResponseSec,
        notificationDismissRatio = notificationDismissRatio,
        callCount = callCount,
        totalCallMinutes = totalCallMinutes,
        chargeStartHour = chargeStartHour,
        batteryLowEvents = batteryLowEvents
    )

    companion object {
        fun fromDomain(f: DailyFeatures) = DailyFeatureEntity(
            date = f.date,
            screenUnlockCount = f.screenUnlockCount,
            totalScreenOnMinutes = f.totalScreenOnMinutes,
            nightScreenMinutes = f.nightScreenMinutes,
            firstUnlockHour = f.firstUnlockHour,
            topAppCategory = f.topAppCategory.name,
            socialAppRatio = f.socialAppRatio,
            productivityAppRatio = f.productivityAppRatio,
            entertainmentAppRatio = f.entertainmentAppRatio,
            uniqueAppsUsed = f.uniqueAppsUsed,
            stepCount = f.stepCount,
            locationEntropy = f.locationEntropy,
            outdoorMinutes = f.outdoorMinutes,
            notificationsReceived = f.notificationsReceived,
            avgNotificationResponseSec = f.avgNotificationResponseSec,
            notificationDismissRatio = f.notificationDismissRatio,
            callCount = f.callCount,
            totalCallMinutes = f.totalCallMinutes,
            chargeStartHour = f.chargeStartHour,
            batteryLowEvents = f.batteryLowEvents
        )
    }
}
