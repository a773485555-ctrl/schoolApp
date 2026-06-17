package com.school.management.feature.admin.teachers.domain.repository

import com.school.management.core.model.domain.Teacher
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    fun getTeachers(schoolId: String): Flow<Resource<List<Teacher>>>
    fun getTeacherById(teacherId: String): Flow<Resource<Teacher>>
    suspend fun createTeacher(schoolId: String, teacher: Teacher): Resource<Teacher>
    suspend fun updateTeacher(teacherId: String, teacher: Teacher): Resource<Teacher>
    suspend fun deleteTeacher(teacherId: String): Resource<Unit>
    fun searchTeachers(schoolId: String, query: String): Flow<Resource<List<Teacher>>>
}
