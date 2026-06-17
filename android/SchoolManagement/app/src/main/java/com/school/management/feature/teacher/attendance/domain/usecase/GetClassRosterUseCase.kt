package com.school.management.feature.teacher.attendance.domain.usecase

import com.school.management.feature.teacher.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class GetClassRosterUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    operator fun invoke(className: String, section: String) = repository.getClassRoster(className, section)
}
