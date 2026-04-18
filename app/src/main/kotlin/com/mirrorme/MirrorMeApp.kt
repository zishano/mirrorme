package com.mirrorme

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MirrorMeApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_COLLECTION,
                    "后台数据采集",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "MirrorMe 正在静默收集行为数据" }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_INSIGHT,
                    "每日洞察",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "你的今日行为报告已生成" }
            )
        }
    }

    companion object {
        const val CHANNEL_COLLECTION = "channel_collection"
        const val CHANNEL_INSIGHT = "channel_insight"
    }
}
