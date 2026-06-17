package com.school.management.feature.student.academic.data

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
