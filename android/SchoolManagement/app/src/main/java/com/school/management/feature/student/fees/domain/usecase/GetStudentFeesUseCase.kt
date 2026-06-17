package com.school.management.feature.student.fees.domain.usecase

import com.school.management.feature.student.fees.domain.repository.FeeRepository
import javax.inject.Inject

class GetStudentFeesUseCase @Inject constructor(
    private val repository: FeeRepository
) {
    operator fun invoke() = repository.getStudentFees()
}
