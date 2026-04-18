package com.mirrorme.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mirrorme.domain.repository.BehaviorRepository
import com.mirrorme.domain.usecase.CollectDailyFeaturesUseCase
import com.mirrorme.domain.usecase.GeneratePersonaUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyAnalysisWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val collectFeatures: CollectDailyFeaturesUseCase,
    private val generatePersona: GeneratePersonaUseCase,
    private val repository: BehaviorRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            val features = collectFeatures(System.currentTimeMillis())
            repository.saveDailyFeatures(features)
            val persona = generatePersona()
            repository.savePersonaProfile(persona)
            Result.success()
        }.getOrElse { Result.retry() }
    }

    companion object {
        private const val WORK_NAME = "daily_analysis"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<DailyAnalysisWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
