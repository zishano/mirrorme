package com.mirrorme.ui.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.repository.BehaviorRepository
import com.mirrorme.domain.usecase.empty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PersonaViewModel @Inject constructor(repository: BehaviorRepository) : ViewModel() {

    val persona: StateFlow<PersonaProfile> = repository.getLatestPersonaProfile()
        .map { it ?: PersonaProfile.empty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonaProfile.empty())
}
