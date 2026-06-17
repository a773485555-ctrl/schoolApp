import os

BASE_DIR = r"C:\Users\mh\.gemini\antigravity\scratch\school-management\android\SchoolManagement\app\src\main\java\com\school\management\feature\teacher\attendance"

FILES = {
    "domain/repository/AttendanceRepository.kt": """package com.school.management.feature.teacher.attendance.domain.repository

import com.school.management.core.model.domain.AbsenceRecord
import com.school.management.core.model.domain.Student
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRepository {
    fun getClassRoster(className: String, section: String): Flow<Resource<List<Student>>>
    fun getAttendanceForDate(className: String, section: String, date: LocalDate): Flow<Resource<List<AbsenceRecord>>>
    suspend fun submitAttendance(
        className: String,
        section: String,
        date: LocalDate,
        subjectId: Int,
        records: List<AbsenceRecord>
    ): Resource<Unit>
}
""",

    "data/AttendanceRepositoryImpl.kt": """package com.school.management.feature.teacher.attendance.data

import com.school.management.core.database.dao.AbsenceDao
import com.school.management.core.database.dao.StudentDao
import com.school.management.core.model.domain.AbsenceRecord
import com.school.management.core.model.domain.Student
import com.school.management.core.network.ApiService
import com.school.management.core.sync.SyncScheduler
import com.school.management.core.util.Resource
import com.school.management.feature.teacher.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val absenceDao: AbsenceDao,
    private val studentDao: StudentDao,
    private val syncScheduler: SyncScheduler
) : AttendanceRepository {

    override fun getClassRoster(className: String, section: String): Flow<Resource<List<Student>>> = flow {
        emit(Resource.Loading)
        try {
            // Simplified return for scaffold
            emit(Resource.Success(emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch roster"))
        }
    }

    override fun getAttendanceForDate(
        className: String, section: String, date: LocalDate
    ): Flow<Resource<List<AbsenceRecord>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load attendance"))
        }
    }

    override suspend fun submitAttendance(
        className: String, section: String, date: LocalDate,
        subjectId: Int, records: List<AbsenceRecord>
    ): Resource<Unit> {
        return try {
            syncScheduler.triggerImmediateSync()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to submit attendance")
        }
    }
}
""",

    "domain/usecase/GetClassRosterUseCase.kt": """package com.school.management.feature.teacher.attendance.domain.usecase

import com.school.management.feature.teacher.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class GetClassRosterUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    operator fun invoke(className: String, section: String) = repository.getClassRoster(className, section)
}
""",

    "domain/usecase/SubmitAttendanceUseCase.kt": """package com.school.management.feature.teacher.attendance.domain.usecase

import com.school.management.core.model.domain.AbsenceRecord
import com.school.management.feature.teacher.attendance.domain.repository.AttendanceRepository
import java.time.LocalDate
import javax.inject.Inject

class SubmitAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(
        className: String, section: String, date: LocalDate, subjectId: Int, records: List<AbsenceRecord>
    ) = repository.submitAttendance(className, section, date, subjectId, records)
}
""",

    "presentation/AttendanceViewModel.kt": """package com.school.management.feature.teacher.attendance.presentation

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
""",

    "presentation/AttendanceScreen.kt": """package com.school.management.feature.teacher.attendance.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // App bar here
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = "Attendance Roster (UI Placeholder)", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
""",

    "di/TeacherAttendanceModule.kt": """package com.school.management.feature.teacher.attendance.di

import com.school.management.feature.teacher.attendance.data.AttendanceRepositoryImpl
import com.school.management.feature.teacher.attendance.domain.repository.AttendanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TeacherAttendanceModule {

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        impl: AttendanceRepositoryImpl
    ): AttendanceRepository
}
"""
}

for path, content in FILES.items():
    full_path = os.path.join(BASE_DIR, path.replace("/", "\\"))
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Generated Teacher Attendance module files")
