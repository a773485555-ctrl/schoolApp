package com.school.management.feature.student.fees.presentation

import androidx.lifecycle.ViewModel
import com.school.management.core.model.domain.Fee
import com.school.management.feature.student.fees.domain.usecase.GetStudentFeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeeViewModel @Inject constructor(
    private val getFeesUseCase: GetStudentFeesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeeUiState())
    val uiState: StateFlow<FeeUiState> = _uiState.asStateFlow()
}

data class FeeUiState(
    val isLoading: Boolean = false,
    val fees: List<Fee> = emptyList(),
    val error: String? = null
)
