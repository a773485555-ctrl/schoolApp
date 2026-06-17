package com.school.management.feature.admin.teachers.domain.usecase

import com.school.management.core.util.Resource
import com.school.management.feature.admin.teachers.domain.repository.TeacherRepository
import javax.inject.Inject

class DeleteTeacherUseCase @Inject constructor(
    private val repository: TeacherRepository
) {
    suspend operator fun invoke(teacherId: String): Resource<Unit> {
        if (teacherId.isBlank()) return Resource.Error("Invalid teacher ID")
        return repository.deleteTeacher(teacherId)
    }
}
