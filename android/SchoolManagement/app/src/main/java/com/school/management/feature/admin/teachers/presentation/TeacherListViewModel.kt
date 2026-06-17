package com.school.management.feature.admin.teachers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.management.core.model.domain.Teacher
import com.school.management.core.network.TokenManager
import com.school.management.core.util.Resource
import com.school.management.feature.admin.teachers.domain.usecase.DeleteTeacherUseCase
import com.school.management.feature.admin.teachers.domain.usecase.GetTeachersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherListUiState(
    val teachers: List<Teacher> = emptyList(),
    val filteredTeachers: List<Teacher> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteSuccess: Boolean = false,
    val deleteError: String? = null
)

@HiltViewModel
class TeacherListViewModel @Inject constructor(
    private val getTeachersUseCase: GetTeachersUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherListUiState())
    val uiState: StateFlow<TeacherListUiState> = _uiState.asStateFlow()

    init {
        loadTeachers()
    }

    fun loadTeachers() {
        val schoolId = tokenManager.getSchoolId() ?: return
        viewModelScope.launch {
            getTeachersUseCase(schoolId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val teachers = result.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                teachers = teachers,
                                filteredTeachers = filterTeachers(teachers, it.searchQuery),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredTeachers = filterTeachers(it.teachers, query)
            )
        }
    }

    fun deleteTeacher(teacherId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(deleteSuccess = false, deleteError = null) }
            when (val result = deleteTeacherUseCase(teacherId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(deleteSuccess = true) }
                    loadTeachers()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(deleteError = result.message) }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }

    fun clearDeleteState() {
        _uiState.update { it.copy(deleteSuccess = false, deleteError = null) }
    }

    private fun filterTeachers(teachers: List<Teacher>, query: String): List<Teacher> {
        if (query.isBlank()) return teachers
        val lower = query.lowercase()
        return teachers.filter {
            it.name.lowercase().contains(lower) ||
                    it.email.lowercase().contains(lower) ||
                    it.specialization.lowercase().contains(lower)
        }
    }
}
