package com.school.management.feature.teacher.attendance.domain.repository

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
