package com.mirrorme.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.domain.repository.BehaviorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(repository: BehaviorRepository) : ViewModel() {

    val recentFeatures: StateFlow<List<DailyFeatures>> = repository.getDailyFeatures(limit = 14)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
