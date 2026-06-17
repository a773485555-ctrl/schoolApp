package com.school.management.core.model.mapper

import com.school.management.core.database.entity.TeacherEntity
import com.school.management.core.model.domain.Teacher
import com.school.management.core.model.dto.TeacherDto
import com.school.management.core.model.dto.TeacherRequest
import java.time.LocalDate

fun TeacherDto.toDomain(): Teacher {
    return Teacher(
        id = id,
        schoolId = schoolId,
        fullName = fullName,
        email = email,
        phone = phone,
        specialization = specialization,
        isActive = isActive,
        hireDate = hireDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        subjects = subjects?.map { it.toDomain() } ?: emptyList()
    )
}

fun TeacherDto.toEntity(): TeacherEntity {
    return TeacherEntity(
        teacherId = id,
        schoolId = schoolId,
        fullName = fullName,
        email = email,
        phone = phone,
        specialization = specialization,
        isActive = isActive,
        hireDate = hireDate
    )
}

fun TeacherEntity.toDomain(): Teacher {
    return Teacher(
        id = teacherId,
        schoolId = schoolId,
        fullName = fullName,
        email = email,
        phone = phone,
        specialization = specialization,
        isActive = isActive,
        hireDate = hireDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        subjects = emptyList()
    )
}

fun Teacher.toEntity(): TeacherEntity {
    return TeacherEntity(
        teacherId = id,
        schoolId = schoolId,
        fullName = fullName,
        email = email,
        phone = phone,
        specialization = specialization,
        isActive = isActive,
        hireDate = hireDate?.toString()
    )
}

fun Teacher.toRequest(): TeacherRequest {
    return TeacherRequest(
        fullName = fullName,
        email = email,
        phone = phone,
        specialization = specialization,
        isActive = isActive,
        hireDate = hireDate?.toString()
    )
}
