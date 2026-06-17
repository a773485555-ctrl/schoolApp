package com.school.management.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "homework")
data class HomeworkEntity(
    @PrimaryKey
    @ColumnInfo(name = "homework_id") val homeworkId: Int,
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    @ColumnInfo(name = "teacher_id") val teacherId: Int?,
    @ColumnInfo(name = "class_name") val className: String,
    @ColumnInfo(name = "section") val section: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "assignment_date") val assignmentDate: String,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "attachment_url") val attachmentUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "synced",
    @ColumnInfo(name = "last_modified_locally") val lastModifiedLocally: Long = System.currentTimeMillis()
)

@Entity(tableName = "absences")
data class AbsenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "absence_id") val absenceId: Int,
    @ColumnInfo(name = "student_id") val studentId: Int,
    @ColumnInfo(name = "teacher_id") val teacherId: Int?,
    @ColumnInfo(name = "subject_id") val subjectId: Int?,
    @ColumnInfo(name = "absence_date") val absenceDate: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "reason") val reason: String?,
    @ColumnInfo(name = "is_justified") val isJustified: Boolean,
    @ColumnInfo(name = "recorded_at") val recordedAt: String?,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "synced",
    @ColumnInfo(name = "last_modified_locally") val lastModifiedLocally: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey
    @ColumnInfo(name = "student_id") val studentId: Int,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "class_name") val className: String,
    @ColumnInfo(name = "section") val section: String,
    @ColumnInfo(name = "parent_name") val parentName: String?,
    @ColumnInfo(name = "parent_phone") val parentPhone: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    @ColumnInfo(name = "subject_name") val subjectName: String,
    @ColumnInfo(name = "class_name") val className: String,
    @ColumnInfo(name = "section") val section: String,
    @ColumnInfo(name = "teacher_id") val teacherId: Int?,
    @ColumnInfo(name = "weekly_hours") val weeklyHours: Int?,
    @ColumnInfo(name = "teacher_name") val teacherName: String?
)

@Entity(tableName = "fees")
data class FeeEntity(
    @PrimaryKey
    @ColumnInfo(name = "fee_id") val feeId: Int,
    @ColumnInfo(name = "student_id") val studentId: Int,
    @ColumnInfo(name = "fee_type") val feeType: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "paid_amount") val paidAmount: Double,
    @ColumnInfo(name = "balance") val balance: Double,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "paid_date") val paidDate: String?,
    @ColumnInfo(name = "payment_method") val paymentMethod: String?,
    @ColumnInfo(name = "invoice_number") val invoiceNumber: String?
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id") val messageId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "message_type") val messageType: String,
    @ColumnInfo(name = "is_read") val isRead: Boolean,
    @ColumnInfo(name = "sent_at") val sentAt: String,
    @ColumnInfo(name = "sender_name") val senderName: String?
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey
    @ColumnInfo(name = "teacher_id") val teacherId: Int,
    @ColumnInfo(name = "school_id") val schoolId: Int,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "specialization") val specialization: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "hire_date") val hireDate: String?
)

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey
    @ColumnInfo(name = "school_id") val schoolId: Int,
    @ColumnInfo(name = "school_name") val schoolName: String,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "logo_url") val logoUrl: String?,
    @ColumnInfo(name = "academic_year") val academicYear: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)
