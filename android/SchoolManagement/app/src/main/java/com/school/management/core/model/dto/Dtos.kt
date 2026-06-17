package com.school.management.core.model.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("user") val user: UserDto?
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName("school_id") val schoolId: Int?,
    @SerializedName("class_name") val className: String?,
    @SerializedName("section") val section: String?
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class DashboardDto(
    @SerializedName("total_students") val totalStudents: Int,
    @SerializedName("total_teachers") val totalTeachers: Int,
    @SerializedName("total_classes") val totalClasses: Int,
    @SerializedName("attendance_rate") val attendanceRate: Double,
    @SerializedName("fee_collection_rate") val feeCollectionRate: Double,
    @SerializedName("pending_fees") val pendingFees: Double,
    @SerializedName("recent_activities") val recentActivities: List<ActivityDto>?
)

data class ActivityDto(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("type") val type: String
)

data class TeacherDto(
    @SerializedName("id") val id: Int,
    @SerializedName("school_id") val schoolId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("hire_date") val hireDate: String?,
    @SerializedName("subjects") val subjects: List<SubjectDto>?
)

data class TeacherRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("hire_date") val hireDate: String?
)

data class StudentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("parent_name") val parentName: String?,
    @SerializedName("parent_phone") val parentPhone: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("enrollment_date") val enrollmentDate: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?
)

data class StudentRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("parent_name") val parentName: String?,
    @SerializedName("parent_phone") val parentPhone: String?,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class StudentRosterDto(
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String
)

data class SchoolDto(
    @SerializedName("id") val id: Int,
    @SerializedName("school_name") val schoolName: String,
    @SerializedName("address") val address: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("logo_url") val logoUrl: String?,
    @SerializedName("academic_year") val academicYear: String?,
    @SerializedName("is_active") val isActive: Boolean
)

data class SchoolConfigRequest(
    @SerializedName("school_name") val schoolName: String,
    @SerializedName("address") val address: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("logo_url") val logoUrl: String?,
    @SerializedName("academic_year") val academicYear: String?
)

data class SubjectWithCountDto(
    @SerializedName("id") val id: Int,
    @SerializedName("subject_name") val subjectName: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("student_count") val studentCount: Int,
    @SerializedName("weekly_hours") val weeklyHours: Int?
)

data class SubjectDto(
    @SerializedName("id") val id: Int,
    @SerializedName("subject_name") val subjectName: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("teacher_id") val teacherId: Int?,
    @SerializedName("weekly_hours") val weeklyHours: Int?,
    @SerializedName("teacher_name") val teacherName: String?
)

data class AttendanceSubmitDto(
    @SerializedName("subject_id") val subjectId: Int?,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("date") val date: String,
    @SerializedName("records") val records: List<AttendanceRecordDto>
)

data class AbsenceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("teacher_id") val teacherId: Int?,
    @SerializedName("subject_id") val subjectId: Int?,
    @SerializedName("absence_date") val absenceDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String?,
    @SerializedName("is_justified") val isJustified: Boolean,
    @SerializedName("recorded_at") val recordedAt: String?,
    @SerializedName("student_name") val studentName: String?
)

data class AttendanceRecordDto(
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String?
)

data class AttendanceSummaryDto(
    @SerializedName("total_days") val totalDays: Int,
    @SerializedName("present_days") val presentDays: Int,
    @SerializedName("absent_days") val absentDays: Int,
    @SerializedName("late_days") val lateDays: Int,
    @SerializedName("excused_days") val excusedDays: Int,
    @SerializedName("attendance_percentage") val attendancePercentage: Double
)

data class HomeworkDto(
    @SerializedName("id") val id: Int,
    @SerializedName("subject_id") val subjectId: Int,
    @SerializedName("teacher_id") val teacherId: Int?,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("assignment_date") val assignmentDate: String,
    @SerializedName("due_date") val dueDate: String,
    @SerializedName("attachment_url") val attachmentUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("subject_name") val subjectName: String?
)

data class HomeworkRequest(
    @SerializedName("subject_id") val subjectId: Int,
    @SerializedName("class_name") val className: String,
    @SerializedName("section") val section: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("assignment_date") val assignmentDate: String,
    @SerializedName("due_date") val dueDate: String,
    @SerializedName("attachment_url") val attachmentUrl: String?
)

data class FeeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("fee_type") val feeType: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("paid_amount") val paidAmount: Double,
    @SerializedName("balance") val balance: Double,
    @SerializedName("status") val status: String,
    @SerializedName("due_date") val dueDate: String,
    @SerializedName("paid_date") val paidDate: String?,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("invoice_number") val invoiceNumber: String?
)

data class FeeSummaryDto(
    @SerializedName("total_fees") val totalFees: Double,
    @SerializedName("total_paid") val totalPaid: Double,
    @SerializedName("total_balance") val totalBalance: Double,
    @SerializedName("payment_percentage") val paymentPercentage: Double
)

data class MessageDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("message_type") val messageType: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("sent_at") val sentAt: String,
    @SerializedName("sender_name") val senderName: String?
)

data class BatchResult(
    @SerializedName("success_count") val successCount: Int,
    @SerializedName("failure_count") val failureCount: Int,
    @SerializedName("errors") val errors: List<String>?
)

data class PaginatedResponse<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_pages") val totalPages: Int
)
