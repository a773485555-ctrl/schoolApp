package com.school.management.feature.admin.dashboard.domain.repository

import com.school.management.core.model.domain.DashboardMetrics
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AdminDashboardRepository {
    fun getDashboardMetrics(schoolId: String): Flow<Resource<DashboardMetrics>>
    suspend fun refreshMetrics(schoolId: String): Resource<DashboardMetrics>
}
