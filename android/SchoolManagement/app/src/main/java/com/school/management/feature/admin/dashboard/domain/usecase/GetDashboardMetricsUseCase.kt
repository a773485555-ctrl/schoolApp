package com.school.management.feature.admin.dashboard.domain.usecase

import com.school.management.core.model.domain.DashboardMetrics
import com.school.management.core.util.Resource
import com.school.management.feature.admin.dashboard.domain.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDashboardMetricsUseCase @Inject constructor(
    private val repository: AdminDashboardRepository
) {
    operator fun invoke(schoolId: String): Flow<Resource<DashboardMetrics>> {
        return repository.getDashboardMetrics(schoolId)
    }
}
