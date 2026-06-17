package com.school.management.core.database

import android.content.Context
import androidx.room.Room
import com.school.management.core.database.dao.AbsenceDao
import com.school.management.core.database.dao.FeeDao
import com.school.management.core.database.dao.HomeworkDao
import com.school.management.core.database.dao.MessageDao
import com.school.management.core.database.dao.SchoolDao
import com.school.management.core.database.dao.StudentDao
import com.school.management.core.database.dao.SubjectDao
import com.school.management.core.database.dao.TeacherDao
import com.school.management.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideHomeworkDao(database: AppDatabase): HomeworkDao = database.homeworkDao()

    @Provides
    fun provideAbsenceDao(database: AppDatabase): AbsenceDao = database.absenceDao()

    @Provides
    fun provideStudentDao(database: AppDatabase): StudentDao = database.studentDao()

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideFeeDao(database: AppDatabase): FeeDao = database.feeDao()

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideTeacherDao(database: AppDatabase): TeacherDao = database.teacherDao()

    @Provides
    fun provideSchoolDao(database: AppDatabase): SchoolDao = database.schoolDao()
}
