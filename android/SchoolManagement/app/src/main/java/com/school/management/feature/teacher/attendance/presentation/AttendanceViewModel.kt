package com.school.management.feature.teacher.attendance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.management.core.model.domain.AbsenceRecord
import com.school.management.feature.teacher.attendance.domain.usecase.GetClassRosterUseCase
import com.school.management.feature.teacher.attendance.domain.usecase.SubmitAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val getClassRosterUseCase: GetClassRosterUseCase,
    private val submitAttendanceUseCase: SubmitAttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    fun loadData(className: String, section: String, date: LocalDate) {
        // Mock load logic
        _uiState.update { it.copy(isLoading = false, records = emptyList()) }
    }
}

data class AttendanceUiState(
    val isLoading: Boolean = true,
    val records: List<AbsenceRecord> = emptyList(),
    val error: String? = null
)
