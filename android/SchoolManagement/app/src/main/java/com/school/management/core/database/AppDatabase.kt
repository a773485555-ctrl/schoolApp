package com.school.management.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.school.management.core.database.dao.AbsenceDao
import com.school.management.core.database.dao.FeeDao
import com.school.management.core.database.dao.HomeworkDao
import com.school.management.core.database.dao.MessageDao
import com.school.management.core.database.dao.SchoolDao
import com.school.management.core.database.dao.StudentDao
import com.school.management.core.database.dao.SubjectDao
import com.school.management.core.database.dao.TeacherDao
import com.school.management.core.database.entity.AbsenceEntity
import com.school.management.core.database.entity.FeeEntity
import com.school.management.core.database.entity.HomeworkEntity
import com.school.management.core.database.entity.MessageEntity
import com.school.management.core.database.entity.SchoolEntity
import com.school.management.core.database.entity.StudentEntity
import com.school.management.core.database.entity.SubjectEntity
import com.school.management.core.database.entity.TeacherEntity

@Database(
    entities = [
        HomeworkEntity::class,
        AbsenceEntity::class,
        StudentEntity::class,
        SubjectEntity::class,
        FeeEntity::class,
        MessageEntity::class,
        TeacherEntity::class,
        SchoolEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeworkDao(): HomeworkDao
    abstract fun absenceDao(): AbsenceDao
    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun feeDao(): FeeDao
    abstract fun messageDao(): MessageDao
    abstract fun teacherDao(): TeacherDao
    abstract fun schoolDao(): SchoolDao
}
