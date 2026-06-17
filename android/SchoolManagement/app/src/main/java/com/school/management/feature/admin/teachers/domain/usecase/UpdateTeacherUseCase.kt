package com.school.management.feature.admin.teachers.domain.usecase

import com.school.management.core.model.domain.Teacher
import com.school.management.core.util.Resource
import com.school.management.feature.admin.teachers.domain.repository.TeacherRepository
import javax.inject.Inject

class UpdateTeacherUseCase @Inject constructor(
    private val repository: TeacherRepository
) {
    suspend operator fun invoke(teacherId: String, teacher: Teacher): Resource<Teacher> {
        if (teacher.name.isBlank()) return Resource.Error("Teacher name is required")
        if (teacher.email.isBlank()) return Resource.Error("Email is required")
        return repository.updateTeacher(teacherId, teacher)
    }
}
