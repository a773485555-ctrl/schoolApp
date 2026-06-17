package com.school.management.feature.student.fees.domain.repository

import com.school.management.core.model.domain.Fee
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface FeeRepository {
    fun getStudentFees(): Flow<Resource<List<Fee>>>
}
