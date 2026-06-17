package com.school.management.feature.student.academic.presentation

import androidx.lifecycle.ViewModel
import com.school.management.core.model.domain.Homework
import com.school.management.feature.student.academic.domain.usecase.GetStudentHomeworkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AcademicHubViewModel @Inject constructor(
    private val getHomeworkUseCase: GetStudentHomeworkUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AcademicUiState())
    val uiState: StateFlow<AcademicUiState> = _uiState.asStateFlow()
}

data class AcademicUiState(
    val isLoading: Boolean = false,
    val homework: List<Homework> = emptyList(),
    val error: String? = null
)
