package com.school.management.feature.teacher.attendance.data

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
