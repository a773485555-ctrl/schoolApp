package com.school.management.feature.admin.teachers.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.management.core.model.domain.Teacher
import com.school.management.core.network.TokenManager
import com.school.management.core.util.Resource
import com.school.management.feature.admin.teachers.domain.repository.TeacherRepository
import com.school.management.feature.admin.teachers.domain.usecase.CreateTeacherUseCase
import com.school.management.feature.admin.teachers.domain.usecase.DeleteTeacherUseCase
import com.school.management.feature.admin.teachers.domain.usecase.UpdateTeacherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherDetailUiState(
    val teacherId: String? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val specialization: String = "",
    val status: String = "active",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val showDeleteConfirmation: Boolean = false
)

@HiltViewModel
class TeacherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TeacherRepository,
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val updateTeacherUseCase: UpdateTeacherUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDetailUiState())
    val uiState: StateFlow<TeacherDetailUiState> = _uiState.asStateFlow()

    init {
        val teacherId = savedStateHandle.get<String>("teacherId")
        if (teacherId != null && teacherId != "new") {
            _uiState.update { it.copy(teacherId = teacherId, isEditing = true) }
            loadTeacher(teacherId)
        }
    }

    private fun loadTeacher(teacherId: String) {
        viewModelScope.launch {
            repository.getTeacherById(teacherId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        result.data?.let { teacher ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    name = teacher.name,
                                    email = teacher.email,
                                    phone = teacher.phone,
                                    specialization = teacher.specialization,
                                    status = teacher.status
                                )
                            }
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

    fun updateName(name: String) { _uiState.update { it.copy(name = name, error = null) } }
    fun updateEmail(email: String) { _uiState.update { it.copy(email = email, error = null) } }
    fun updatePhone(phone: String) { _uiState.update { it.copy(phone = phone, error = null) } }
    fun updateSpecialization(spec: String) { _uiState.update { it.copy(specialization = spec, error = null) } }
    fun updateStatus(status: String) { _uiState.update { it.copy(status = status, error = null) } }
    fun showDeleteConfirmation() { _uiState.update { it.copy(showDeleteConfirmation = true) } }
    fun hideDeleteConfirmation() { _uiState.update { it.copy(showDeleteConfirmation = false) } }

    fun save() {
        val state = _uiState.value
        val schoolId = tokenManager.getSchoolId() ?: return

        val teacher = Teacher(
            id = state.teacherId ?: "",
            name = state.name,
            email = state.email,
            phone = state.phone,
            specialization = state.specialization,
            schoolId = schoolId,
            status = state.status,
            avatarUrl = null,
            createdAt = ""
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val result = if (state.isEditing && state.teacherId != null) {
                updateTeacherUseCase(state.teacherId, teacher)
            } else {
                createTeacherUseCase(schoolId, teacher)
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }

    fun delete() {
        val teacherId = _uiState.value.teacherId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteConfirmation = false, isLoading = true) }
            when (val result = deleteTeacherUseCase(teacherId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, deleteSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }
}
