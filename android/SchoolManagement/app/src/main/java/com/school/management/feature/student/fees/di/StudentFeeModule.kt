package com.school.management.feature.student.fees.di

import com.school.management.feature.student.fees.data.FeeRepositoryImpl
import com.school.management.feature.student.fees.domain.repository.FeeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StudentFeeModule {
    @Binds
    @Singleton
    abstract fun bindFeeRepository(impl: FeeRepositoryImpl): FeeRepository
}
