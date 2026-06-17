package com.school.management.feature.teacher.attendance.domain.usecase

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
