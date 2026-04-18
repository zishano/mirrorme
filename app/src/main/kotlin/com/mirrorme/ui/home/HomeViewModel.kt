package com.mirrorme.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.repository.BehaviorRepository
import com.mirrorme.domain.usecase.GeneratePersonaUseCase
import com.mirrorme.domain.usecase.empty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BehaviorRepository,
    private val generatePersona: GeneratePersonaUseCase
) : ViewModel() {

    val persona: StateFlow<PersonaProfile> = repository.getLatestPersonaProfile()
        .map { it ?: PersonaProfile.empty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonaProfile.empty())

    val todayFeatures: StateFlow<DailyFeatures?> = repository.getLatestFeatures()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refreshPersona() {
        viewModelScope.launch {
            val profile = generatePersona()
            repository.savePersonaProfile(profile)
        }
    }
}
