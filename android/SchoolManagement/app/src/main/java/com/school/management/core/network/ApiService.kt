package com.school.management.core.network

import com.school.management.core.model.dto.AbsenceDto
import com.school.management.core.model.dto.AttendanceSubmitDto
import com.school.management.core.model.dto.AttendanceSummaryDto
import com.school.management.core.model.dto.AuthResponse
import com.school.management.core.model.dto.BatchResult
import com.school.management.core.model.dto.DashboardDto
import com.school.management.core.model.dto.FeeDto
import com.school.management.core.model.dto.FeeSummaryDto
import com.school.management.core.model.dto.HomeworkDto
import com.school.management.core.model.dto.HomeworkRequest
import com.school.management.core.model.dto.LoginRequest
import com.school.management.core.model.dto.MessageDto
import com.school.management.core.model.dto.PaginatedResponse
import com.school.management.core.model.dto.RefreshRequest
import com.school.management.core.model.dto.SchoolConfigRequest
import com.school.management.core.model.dto.SchoolDto
import com.school.management.core.model.dto.StudentDto
import com.school.management.core.model.dto.StudentRequest
import com.school.management.core.model.dto.StudentRosterDto
import com.school.management.core.model.dto.SubjectDto
import com.school.management.core.model.dto.SubjectWithCountDto
import com.school.management.core.model.dto.TeacherDto
import com.school.management.core.model.dto.TeacherRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<AuthResponse>

    // ── Admin: Dashboard ─────────────────────────────────────────────────────
    @GET("api/admin/dashboard")
    suspend fun getDashboard(): Response<DashboardDto>

    // ── Admin: Teachers CRUD ─────────────────────────────────────────────────
    @GET("api/admin/teachers")
    suspend fun getTeachers(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PaginatedResponse<TeacherDto>>

    @GET("api/admin/teachers/{id}")
    suspend fun getTeacherById(@Path("id") teacherId: Int): Response<TeacherDto>

    @POST("api/admin/teachers")
    suspend fun createTeacher(@Body request: TeacherRequest): Response<TeacherDto>

    @PUT("api/admin/teachers/{id}")
    suspend fun updateTeacher(
        @Path("id") teacherId: Int,
        @Body request: TeacherRequest
    ): Response<TeacherDto>

    @DELETE("api/admin/teachers/{id}")
    suspend fun deleteTeacher(@Path("id") teacherId: Int): Response<Unit>

    // ── Admin: Students CRUD ─────────────────────────────────────────────────
    @GET("api/admin/students")
    suspend fun getStudents(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("class_name") className: String? = null,
        @Query("section") section: String? = null,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<StudentDto>>

    @GET("api/admin/students/{id}")
    suspend fun getStudentById(@Path("id") studentId: Int): Response<StudentDto>

    @POST("api/admin/students")
    suspend fun createStudent(@Body request: StudentRequest): Response<StudentDto>

    @PUT("api/admin/students/{id}")
    suspend fun updateStudent(
        @Path("id") studentId: Int,
        @Body request: StudentRequest
    ): Response<StudentDto>

    @DELETE("api/admin/students/{id}")
    suspend fun deleteStudent(@Path("id") studentId: Int): Response<Unit>

    // ── Admin: School Config ─────────────────────────────────────────────────
    @GET("api/admin/school")
    suspend fun getSchoolConfig(): Response<SchoolDto>

    @PUT("api/admin/school")
    suspend fun updateSchoolConfig(@Body request: SchoolConfigRequest): Response<SchoolDto>

    // ── Teacher: Subjects ────────────────────────────────────────────────────
    @GET("api/teacher/subjects")
    suspend fun getTeacherSubjects(): Response<List<SubjectWithCountDto>>

    // ── Teacher: Attendance ──────────────────────────────────────────────────
    @GET("api/teacher/attendance/roster/{subjectId}")
    suspend fun getAttendanceRoster(
        @Path("subjectId") subjectId: Int
    ): Response<List<StudentRosterDto>>

    @GET("api/teacher/attendance/{subjectId}")
    suspend fun getAttendanceForDate(
        @Path("subjectId") subjectId: Int,
        @Query("date") date: String
    ): Response<List<AbsenceDto>>

    @POST("api/teacher/attendance")
    suspend fun submitBatchAttendance(
        @Body request: AttendanceSubmitDto
    ): Response<BatchResult>

    @PUT("api/teacher/attendance/{id}")
    suspend fun updateAttendanceRecord(
        @Path("id") absenceId: Int,
        @Body request: AbsenceDto
    ): Response<AbsenceDto>

    // ── Teacher: Homework ────────────────────────────────────────────────────
    @GET("api/teacher/homework")
    suspend fun getTeacherHomework(
        @Query("subject_id") subjectId: Int? = null
    ): Response<List<HomeworkDto>>

    @POST("api/teacher/homework")
    suspend fun createHomework(@Body request: HomeworkRequest): Response<HomeworkDto>

    @PUT("api/teacher/homework/{id}")
    suspend fun updateHomework(
        @Path("id") homeworkId: Int,
        @Body request: HomeworkRequest
    ): Response<HomeworkDto>

    @DELETE("api/teacher/homework/{id}")
    suspend fun deleteHomework(@Path("id") homeworkId: Int): Response<Unit>

    // ── Student: Homework ────────────────────────────────────────────────────
    @GET("api/student/homework")
    suspend fun getStudentHomework(): Response<List<HomeworkDto>>

    // ── Student: Subjects ────────────────────────────────────────────────────
    @GET("api/student/subjects")
    suspend fun getStudentSubjects(): Response<List<SubjectDto>>

    // ── Student: Attendance ──────────────────────────────────────────────────
    @GET("api/student/attendance")
    suspend fun getStudentAttendance(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<List<AbsenceDto>>

    @GET("api/student/attendance/summary")
    suspend fun getStudentAttendanceSummary(): Response<AttendanceSummaryDto>

    // ── Student: Fees ────────────────────────────────────────────────────────
    @GET("api/student/fees")
    suspend fun getStudentFees(): Response<List<FeeDto>>

    @GET("api/student/fees/summary")
    suspend fun getStudentFeeSummary(): Response<FeeSummaryDto>

    // ── Student: Messages ────────────────────────────────────────────────────
    @GET("api/student/messages")
    suspend fun getStudentMessages(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PaginatedResponse<MessageDto>>

    @PUT("api/student/messages/{id}/read")
    suspend fun markMessageRead(@Path("id") messageId: Int): Response<Unit>
}
