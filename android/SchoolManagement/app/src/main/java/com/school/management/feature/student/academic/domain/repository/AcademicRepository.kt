package com.school.management.feature.student.academic.domain.repository

import com.school.management.core.model.domain.Homework
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    fun getStudentHomework(from: String, to: String): Flow<Resource<List<Homework>>>
}
