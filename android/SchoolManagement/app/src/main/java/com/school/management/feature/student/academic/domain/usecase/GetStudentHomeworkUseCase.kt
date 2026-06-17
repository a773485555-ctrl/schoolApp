package com.school.management.feature.student.academic.domain.usecase

import com.school.management.feature.student.academic.domain.repository.AcademicRepository
import javax.inject.Inject

class GetStudentHomeworkUseCase @Inject constructor(
    private val repository: AcademicRepository
) {
    operator fun invoke(from: String, to: String) = repository.getStudentHomework(from, to)
}
