package com.mirrorme.domain.model

data class DailyFeatures(
    val date: Long,                        // epoch millis of day start (00:00)

    // --- 屏幕行为 ---
    val screenUnlockCount: Int,            // 解锁次数
    val totalScreenOnMinutes: Int,         // 总亮屏时长（分钟）
    val nightScreenMinutes: Int,           // 23:00-06:00 亮屏时长
    val firstUnlockHour: Int,              // 首次解锁小时

    // --- App 使用 ---
    val topAppCategory: AppCategory,       // 使用最多的 App 类别
    val socialAppRatio: Double,            // 社交 App 占总时长比例
    val productivityAppRatio: Double,      // 工具/生产力类占比
    val entertainmentAppRatio: Double,     // 娱乐类占比
    val uniqueAppsUsed: Int,               // 当天打开的独立 App 数量

    // --- 活动 / 位置 ---
    val stepCount: Int,                    // 步数
    val locationEntropy: Double,           // 位置多样性熵值（0=未离开，越大越丰富）
    val outdoorMinutes: Int,               // 室外时间估算（分钟）

    // --- 通知响应 ---
    val notificationsReceived: Int,        // 收到通知数量
    val avgNotificationResponseSec: Int,   // 平均响应时间（秒）
    val notificationDismissRatio: Double,  // 直接消除比例

    // --- 通话 ---
    val callCount: Int,                    // 通话次数
    val totalCallMinutes: Int,             // 通话总时长

    // --- 充电 / 电量 ---
    val chargeStartHour: Int,              // 开始充电时刻
    val batteryLowEvents: Int              // 低电量次数（反映使用强度）
)

enum class AppCategory {
    SOCIAL, PRODUCTIVITY, ENTERTAINMENT, NEWS, HEALTH,
    FINANCE, EDUCATION, SHOPPING, OTHER
}
