package com.school.management.feature.admin.dashboard.data

import com.school.management.core.model.domain.DashboardMetrics
import com.school.management.core.network.ApiService
import com.school.management.core.util.Resource
import com.school.management.feature.admin.dashboard.domain.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminDashboardRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AdminDashboardRepository {

    private var cachedMetrics: DashboardMetrics? = null

    override fun getDashboardMetrics(schoolId: String): Flow<Resource<DashboardMetrics>> = flow {
        emit(Resource.Loading())

        // Emit cached data first if available
        cachedMetrics?.let { cached ->
            emit(Resource.Success(cached))
        }

        // Fetch fresh data from network
        try {
            val response = apiService.getDashboardMetrics(schoolId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val metrics = DashboardMetrics(
                        activeStudents = dto.activeStudents,
                        activeTeachers = dto.activeTeachers,
                        todayAbsences = dto.todayAbsences,
                        totalBilled = dto.totalBilled,
                        totalCollected = dto.totalCollected,
                        outstanding = dto.outstanding,
                        attendanceRate = dto.attendanceRate,
                        collectionRate = dto.collectionRate
                    )
                    cachedMetrics = metrics
                    emit(Resource.Success(metrics))
                } ?: emit(Resource.Error("Empty response from server"))
            } else {
                if (cachedMetrics == null) {
                    emit(Resource.Error("Failed to load dashboard: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            if (cachedMetrics == null) {
                emit(Resource.Error(e.localizedMessage ?: "Failed to load dashboard data"))
            }
        }
    }

    override suspend fun refreshMetrics(schoolId: String): Resource<DashboardMetrics> {
        return try {
            val response = apiService.getDashboardMetrics(schoolId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val metrics = DashboardMetrics(
                        activeStudents = dto.activeStudents,
                        activeTeachers = dto.activeTeachers,
                        todayAbsences = dto.todayAbsences,
                        totalBilled = dto.totalBilled,
                        totalCollected = dto.totalCollected,
                        outstanding = dto.outstanding,
                        attendanceRate = dto.attendanceRate,
                        collectionRate = dto.collectionRate
                    )
                    cachedMetrics = metrics
                    Resource.Success(metrics)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Refresh failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Refresh failed")
        }
    }
}
