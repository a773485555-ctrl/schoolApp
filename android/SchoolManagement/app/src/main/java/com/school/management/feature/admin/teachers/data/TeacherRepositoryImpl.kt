package com.school.management.feature.admin.teachers.data

import com.school.management.core.database.dao.TeacherDao
import com.school.management.core.database.entity.TeacherEntity
import com.school.management.core.model.domain.Teacher
import com.school.management.core.model.dto.CreateTeacherRequest
import com.school.management.core.model.dto.UpdateTeacherRequest
import com.school.management.core.network.ApiService
import com.school.management.core.util.Resource
import com.school.management.feature.admin.teachers.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val teacherDao: TeacherDao
) : TeacherRepository {

    override fun getTeachers(schoolId: String): Flow<Resource<List<Teacher>>> = flow {
        emit(Resource.Loading())

        // Emit cached data first
        val cachedTeachers = teacherDao.getTeachersBySchool(schoolId)
        if (cachedTeachers.isNotEmpty()) {
            emit(Resource.Success(cachedTeachers.map { it.toDomain() }))
        }

        // Fetch from network
        try {
            val response = apiService.getTeachers(schoolId)
            if (response.isSuccessful) {
                response.body()?.let { dtoList ->
                    val teachers = dtoList.map { dto ->
                        Teacher(
                            id = dto.id,
                            name = dto.name,
                            email = dto.email,
                            phone = dto.phone,
                            specialization = dto.specialization,
                            schoolId = dto.schoolId,
                            status = dto.status,
                            avatarUrl = dto.avatarUrl,
                            createdAt = dto.createdAt
                        )
                    }
                    // Update cache
                    teacherDao.deleteAllBySchool(schoolId)
                    teacherDao.insertAll(teachers.map { it.toEntity() })
                    emit(Resource.Success(teachers))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                if (cachedTeachers.isEmpty()) {
                    emit(Resource.Error("Failed to load teachers: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            if (cachedTeachers.isEmpty()) {
                emit(Resource.Error(e.localizedMessage ?: "Failed to load teachers"))
            }
        }
    }

    override fun getTeacherById(teacherId: String): Flow<Resource<Teacher>> = flow {
        emit(Resource.Loading())

        val cached = teacherDao.getTeacherById(teacherId)
        cached?.let { emit(Resource.Success(it.toDomain())) }

        try {
            val response = apiService.getTeacherById(teacherId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val teacher = Teacher(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        phone = dto.phone,
                        specialization = dto.specialization,
                        schoolId = dto.schoolId,
                        status = dto.status,
                        avatarUrl = dto.avatarUrl,
                        createdAt = dto.createdAt
                    )
                    teacherDao.insert(teacher.toEntity())
                    emit(Resource.Success(teacher))
                } ?: emit(Resource.Error("Teacher not found"))
            } else {
                if (cached == null) emit(Resource.Error("Failed to load teacher"))
            }
        } catch (e: Exception) {
            if (cached == null) emit(Resource.Error(e.localizedMessage ?: "Failed to load teacher"))
        }
    }

    override suspend fun createTeacher(schoolId: String, teacher: Teacher): Resource<Teacher> {
        return try {
            val request = CreateTeacherRequest(
                name = teacher.name,
                email = teacher.email,
                phone = teacher.phone,
                specialization = teacher.specialization,
                schoolId = schoolId
            )
            val response = apiService.createTeacher(schoolId, request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val created = Teacher(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        phone = dto.phone,
                        specialization = dto.specialization,
                        schoolId = dto.schoolId,
                        status = dto.status,
                        avatarUrl = dto.avatarUrl,
                        createdAt = dto.createdAt
                    )
                    teacherDao.insert(created.toEntity())
                    Resource.Success(created)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Failed to create teacher: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to create teacher")
        }
    }

    override suspend fun updateTeacher(teacherId: String, teacher: Teacher): Resource<Teacher> {
        return try {
            val request = UpdateTeacherRequest(
                name = teacher.name,
                email = teacher.email,
                phone = teacher.phone,
                specialization = teacher.specialization,
                status = teacher.status
            )
            val response = apiService.updateTeacher(teacherId, request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val updated = Teacher(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        phone = dto.phone,
                        specialization = dto.specialization,
                        schoolId = dto.schoolId,
                        status = dto.status,
                        avatarUrl = dto.avatarUrl,
                        createdAt = dto.createdAt
                    )
                    teacherDao.insert(updated.toEntity())
                    Resource.Success(updated)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Failed to update teacher: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update teacher")
        }
    }

    override suspend fun deleteTeacher(teacherId: String): Resource<Unit> {
        return try {
            val response = apiService.deleteTeacher(teacherId)
            if (response.isSuccessful) {
                teacherDao.deleteById(teacherId)
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to delete teacher: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to delete teacher")
        }
    }

    override fun searchTeachers(schoolId: String, query: String): Flow<Resource<List<Teacher>>> = flow {
        emit(Resource.Loading())
        val results = teacherDao.searchTeachers(schoolId, "%$query%")
        emit(Resource.Success(results.map { it.toDomain() }))
    }

    private fun TeacherEntity.toDomain(): Teacher = Teacher(
        id = id,
        name = name,
        email = email,
        phone = phone,
        specialization = specialization,
        schoolId = schoolId,
        status = status,
        avatarUrl = avatarUrl,
        createdAt = createdAt
    )

    private fun Teacher.toEntity(): TeacherEntity = TeacherEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        specialization = specialization,
        schoolId = schoolId,
        status = status,
        avatarUrl = avatarUrl,
        createdAt = createdAt
    )
}
