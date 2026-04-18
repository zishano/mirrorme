package com.mirrorme.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mirrorme.MainActivity
import com.mirrorme.MirrorMeApp
import com.mirrorme.R
import com.mirrorme.data.local.sensor.MotionCollector
import com.mirrorme.data.local.sensor.UsageStatsCollector
import com.mirrorme.domain.repository.BehaviorRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BehaviorCollectionService : Service() {

    @Inject lateinit var usageStatsCollector: UsageStatsCollector
    @Inject lateinit var motionCollector: MotionCollector
    @Inject lateinit var repository: BehaviorRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var collectJob: Job? = null
    private var lastCollectedMs = System.currentTimeMillis() - 60_000L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification())
        startCollecting()
        return START_STICKY
    }

    private fun startCollecting() {
        collectJob?.cancel()
        collectJob = serviceScope.launch {
            // 步数持续监听
            launch {
                motionCollector.stepCounterFlow().collect { event ->
                    repository.insertEvent(event)
                }
            }

            // 使用统计每 15 分钟轮询一次
            while (true) {
                delay(POLL_INTERVAL_MS)
                runCatching {
                    val events = usageStatsCollector.collectSince(lastCollectedMs)
                    if (events.isNotEmpty()) {
                        repository.insertEvents(events)
                        lastCollectedMs = System.currentTimeMillis()
                    }
                }.onFailure { /* 静默失败，下次重试 */ }

                // 清理 30 天前的原始事件数据
                val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 3_600_000
                repository.deleteEventsOlderThan(thirtyDaysAgo)
            }
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, MirrorMeApp.CHANNEL_COLLECTION)
            .setContentTitle("MirrorMe 运行中")
            .setContentText("正在静默记录你的行为数据")
            .setSmallIcon(R.drawable.ic_mirror)
            .setContentIntent(pi)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        collectJob?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val NOTIF_ID = 1001
        private const val POLL_INTERVAL_MS = 15 * 60 * 1000L // 15 分钟
    }
}
