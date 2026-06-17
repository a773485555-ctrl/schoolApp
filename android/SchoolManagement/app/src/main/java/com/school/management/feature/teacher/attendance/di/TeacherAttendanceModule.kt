package com.school.management.feature.teacher.attendance.di

import com.school.management.feature.teacher.attendance.data.AttendanceRepositoryImpl
import com.school.management.feature.teacher.attendance.domain.repository.AttendanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TeacherAttendanceModule {

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        impl: AttendanceRepositoryImpl
    ): AttendanceRepository
}
