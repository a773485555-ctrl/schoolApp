package com.school.management.feature.student.academic.di

import com.school.management.feature.student.academic.data.AcademicRepositoryImpl
import com.school.management.feature.student.academic.domain.repository.AcademicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AcademicModule {
    @Binds
    @Singleton
    abstract fun bindAcademicRepository(impl: AcademicRepositoryImpl): AcademicRepository
}
