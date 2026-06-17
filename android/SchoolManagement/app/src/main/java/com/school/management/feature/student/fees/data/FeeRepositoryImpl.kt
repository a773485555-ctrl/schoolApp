package com.school.management.feature.student.fees.data

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
