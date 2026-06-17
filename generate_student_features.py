import os

BASE_DIR = r"C:\Users\mh\.gemini\antigravity\scratch\school-management\android\SchoolManagement\app\src\main\java\com\school\management\feature\student"

FILES = {
    # Academic Hub
    "academic/domain/repository/AcademicRepository.kt": """package com.school.management.feature.student.academic.domain.repository

import com.school.management.core.model.domain.Homework
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    fun getStudentHomework(from: String, to: String): Flow<Resource<List<Homework>>>
}
""",
    "academic/data/AcademicRepositoryImpl.kt": """package com.school.management.feature.student.academic.data

import com.school.management.core.model.domain.Homework
import com.school.management.core.network.ApiService
import com.school.management.core.util.Resource
import com.school.management.feature.student.academic.domain.repository.AcademicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AcademicRepositoryImpl @Inject constructor(
    private val api: ApiService
) : AcademicRepository {
    override fun getStudentHomework(from: String, to: String): Flow<Resource<List<Homework>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch homework"))
        }
    }
}
""",
    "academic/domain/usecase/GetStudentHomeworkUseCase.kt": """package com.school.management.feature.student.academic.domain.usecase

import com.school.management.feature.student.academic.domain.repository.AcademicRepository
import javax.inject.Inject

class GetStudentHomeworkUseCase @Inject constructor(
    private val repository: AcademicRepository
) {
    operator fun invoke(from: String, to: String) = repository.getStudentHomework(from, to)
}
""",
    "academic/presentation/AcademicHubViewModel.kt": """package com.school.management.feature.student.academic.presentation

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
""",
    "academic/di/AcademicModule.kt": """package com.school.management.feature.student.academic.di

import com.school.management.feature.student.academic.data.AcademicRepositoryImpl
import com.school.management.feature.student.academic.domain.repository.AcademicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AcademicModule {
    @Binds
    @Singleton
    abstract fun bindAcademicRepository(impl: AcademicRepositoryImpl): AcademicRepository
}
""",

    # Financial Ledger
    "fees/domain/repository/FeeRepository.kt": """package com.school.management.feature.student.fees.domain.repository

import com.school.management.core.model.domain.Fee
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface FeeRepository {
    fun getStudentFees(): Flow<Resource<List<Fee>>>
}
""",
    "fees/data/FeeRepositoryImpl.kt": """package com.school.management.feature.student.fees.data

import com.school.management.core.database.dao.FeeDao
import com.school.management.core.model.domain.Fee
import com.school.management.core.network.ApiService
import com.school.management.core.util.Resource
import com.school.management.feature.student.fees.domain.repository.FeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeeRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val feeDao: FeeDao
) : FeeRepository {
    override fun getStudentFees(): Flow<Resource<List<Fee>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(emptyList()))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load fees"))
        }
    }
}
""",
    "fees/domain/usecase/GetStudentFeesUseCase.kt": """package com.school.management.feature.student.fees.domain.usecase

import com.school.management.feature.student.fees.domain.repository.FeeRepository
import javax.inject.Inject

class GetStudentFeesUseCase @Inject constructor(
    private val repository: FeeRepository
) {
    operator fun invoke() = repository.getStudentFees()
}
""",
    "fees/presentation/FeeViewModel.kt": """package com.school.management.feature.student.fees.presentation

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
""",
    "fees/presentation/FeeLedgerScreen.kt": """package com.school.management.feature.student.fees.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FeeLedgerScreen(
    viewModel: FeeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text(text = "Student Financial Ledger (Placeholder)", style = MaterialTheme.typography.titleLarge)
        }
    }
}
""",
    "fees/di/StudentFeeModule.kt": """package com.school.management.feature.student.fees.di

import com.school.management.feature.student.fees.data.FeeRepositoryImpl
import com.school.management.feature.student.fees.domain.repository.FeeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StudentFeeModule {
    @Binds
    @Singleton
    abstract fun bindFeeRepository(impl: FeeRepositoryImpl): FeeRepository
}
"""
}

for path, content in FILES.items():
    full_path = os.path.join(BASE_DIR, path.replace("/", "\\"))
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Generated Student Academic and Fee module files")
