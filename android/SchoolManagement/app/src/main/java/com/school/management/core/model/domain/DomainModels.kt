package com.school.management.core.model.domain

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val schoolId: Int?,
    val className: String?,
    val section: String?
)

enum class UserRole {
    ADMIN, TEACHER, STUDENT;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.lowercase()) {
                "admin" -> ADMIN
                "teacher" -> TEACHER
                "student" -> STUDENT
                else -> STUDENT
            }
        }
    }
}

data class DashboardMetrics(
    val totalStudents: Int,
    val totalTeachers: Int,
    val totalClasses: Int,
    val attendanceRate: Double,
    val feeCollectionRate: Double,
    val pendingFees: Double,
    val recentActivities: List<Activity>
)

data class Activity(
    val id: Int,
    val description: String,
    val timestamp: LocalDateTime,
    val type: String
)

data class Teacher(
    val id: Int,
    val schoolId: Int,
    val fullName: String,
    val email: String,
    val phone: String?,
    val specialization: String?,
    val isActive: Boolean,
    val hireDate: LocalDate?,
    val subjects: List<Subject>
)

data class Student(
    val id: Int,
    val fullName: String,
    val className: String,
    val section: String,
    val parentName: String?,
    val parentPhone: String?,
    val isActive: Boolean
)

data class Subject(
    val id: Int,
    val subjectName: String,
    val className: String,
    val section: String,
    val teacherId: Int?,
    val weeklyHours: Int?,
    val teacherName: String?
)

data class Homework(
    val id: Int,
    val subjectId: Int,
    val teacherId: Int?,
    val className: String,
    val section: String,
    val title: String,
    val description: String?,
    val assignmentDate: LocalDate,
    val dueDate: LocalDate,
    val attachmentUrl: String?,
    val createdAt: LocalDateTime?,
    val subjectName: String?
)

data class AbsenceRecord(
    val id: Int,
    val studentId: Int,
    val teacherId: Int?,
    val subjectId: Int?,
    val absenceDate: LocalDate,
    val status: AttendanceStatus,
    val reason: String?,
    val isJustified: Boolean,
    val recordedAt: LocalDateTime?,
    val studentName: String?
)

enum class AttendanceStatus(val value: String) {
    PRESENT("present"),
    ABSENT("absent"),
    LATE("late"),
    EXCUSED("excused");

    companion object {
        fun fromString(status: String): AttendanceStatus {
            return entries.find { it.value.equals(status, ignoreCase = true) } ?: ABSENT
        }
    }
}

data class AttendanceSummary(
    val totalDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val lateDays: Int,
    val excusedDays: Int,
    val attendancePercentage: Double
)

data class Fee(
    val id: Int,
    val studentId: Int,
    val feeType: String,
    val amount: Double,
    val paidAmount: Double,
    val balance: Double,
    val status: FeeStatus,
    val dueDate: LocalDate,
    val paidDate: LocalDate?,
    val paymentMethod: String?,
    val invoiceNumber: String?
)

enum class FeeStatus(val value: String) {
    PAID("paid"),
    PARTIAL("partial"),
    UNPAID("unpaid"),
    OVERDUE("overdue");

    companion object {
        fun fromString(status: String): FeeStatus {
            return entries.find { it.value.equals(status, ignoreCase = true) } ?: UNPAID
        }
    }
}

data class FeeSummary(
    val totalFees: Double,
    val totalPaid: Double,
    val totalBalance: Double,
    val paymentPercentage: Double
)

data class Message(
    val id: Int,
    val title: String,
    val body: String,
    val messageType: MessageType,
    val isRead: Boolean,
    val sentAt: LocalDateTime,
    val senderName: String?
)

enum class MessageType(val value: String) {
    ANNOUNCEMENT("announcement"),
    ALERT("alert"),
    NOTIFICATION("notification"),
    REMINDER("reminder");

    companion object {
        fun fromString(type: String): MessageType {
            return entries.find { it.value.equals(type, ignoreCase = true) } ?: NOTIFICATION
        }
    }
}

data class School(
    val id: Int,
    val schoolName: String,
    val address: String?,
    val phone: String?,
    val email: String?,
    val logoUrl: String?,
    val academicYear: String?,
    val isActive: Boolean
)
