package com.school.management.feature.admin.teachers.domain.usecase

import com.school.management.core.model.domain.Teacher
import com.school.management.core.util.Resource
import com.school.management.feature.admin.teachers.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTeachersUseCase @Inject constructor(
    private val repository: TeacherRepository
) {
    operator fun invoke(schoolId: String, query: String = ""): Flow<Resource<List<Teacher>>> {
        return if (query.isBlank()) {
            repository.getTeachers(schoolId)
        } else {
            repository.searchTeachers(schoolId, query)
        }
    }
}
