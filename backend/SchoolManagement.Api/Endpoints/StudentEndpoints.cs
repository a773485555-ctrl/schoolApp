using System.Security.Claims;
using Dapper;
using Microsoft.Data.SqlClient;
using SchoolManagement.Api.Models.Common;
using SchoolManagement.Api.Models.Student;
using SchoolManagement.Api.Models.Teacher;

namespace SchoolManagement.Api.Endpoints;

public static class StudentEndpoints
{
    public static void MapStudentEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/api/student")
            .WithTags("Student")
            .RequireAuthorization(policy => policy.RequireRole("student"));

        group.MapGet("/homework", GetHomework);
        group.MapGet("/subjects", GetSubjects);
        group.MapGet("/attendance", GetAttendance);
        group.MapGet("/attendance/summary", GetAttendanceSummary);
        group.MapGet("/fees", GetFees);
        group.MapGet("/fees/summary", GetFeeSummary);
        group.MapGet("/messages", GetMessages);
        group.MapPut("/messages/{id:int}/read", MarkMessageRead);
    }

    private static int GetStudentId(HttpContext ctx) =>
        int.Parse(ctx.User.FindFirstValue(ClaimTypes.NameIdentifier)!);

    private static int GetSchoolId(HttpContext ctx) =>
        int.Parse(ctx.User.FindFirstValue("school_id")!);

    private static string GetClassName(HttpContext ctx) =>
        ctx.User.FindFirstValue("class_name")!;

    private static string GetSection(HttpContext ctx) =>
        ctx.User.FindFirstValue("section")!;

    private static async Task<IResult> GetHomework(
        HttpContext httpContext,
        SqlConnection db,
        DateTime? from = null,
        DateTime? to = null)
    {
        var className = GetClassName(httpContext);
        var section = GetSection(httpContext);

        var whereClause = "WHERE h.class_name = @ClassName AND h.section = @Section";
        if (from.HasValue) whereClause += " AND h.due_date >= @From";
        if (to.HasValue) whereClause += " AND h.due_date <= @To";

        var homework = await db.QueryAsync<HomeworkDto>(
            $@"SELECT h.homework_id AS HomeworkId, h.teacher_id AS TeacherId,
                      h.subject_id AS SubjectId, sub.subject_name AS SubjectName,
                      h.class_name AS ClassName, h.section AS Section,
                      h.title AS Title, h.description AS Description,
                      h.assigned_date AS AssignedDate, h.due_date AS DueDate,
                      h.created_at AS CreatedAt, h.updated_at AS UpdatedAt
               FROM homework h
               INNER JOIN subjects sub ON h.subject_id = sub.subject_id
               {whereClause}
               ORDER BY h.due_date DESC",
            new { ClassName = className, Section = section, From = from, To = to });

        return Results.Ok(homework);
    }

    private static async Task<IResult> GetSubjects(HttpContext httpContext, SqlConnection db)
    {
        var className = GetClassName(httpContext);
        var section = GetSection(httpContext);
        var schoolId = GetSchoolId(httpContext);

        var subjects = await db.QueryAsync<SubjectWithCountDto>(
            @"SELECT sub.subject_id AS SubjectId, sub.subject_name AS SubjectName,
                     sub.class_name AS ClassName, sub.section AS Section,
                     (SELECT COUNT(*) FROM students st
                      WHERE st.class_name = sub.class_name
                        AND st.section = sub.section
                        AND st.school_id = sub.school_id
                        AND st.is_active = 1) AS StudentCount
              FROM subjects sub
              WHERE sub.class_name = @ClassName AND sub.section = @Section
                AND sub.school_id = @SchoolId
              ORDER BY sub.subject_name",
            new { ClassName = className, Section = section, SchoolId = schoolId });

        return Results.Ok(subjects);
    }

    private static async Task<IResult> GetAttendance(
        HttpContext httpContext,
        SqlConnection db,
        int? month = null,
        int? year = null)
    {
        var studentId = GetStudentId(httpContext);

        var targetMonth = month ?? DateTime.UtcNow.Month;
        var targetYear = year ?? DateTime.UtcNow.Year;

        var records = await db.QueryAsync<AttendanceRecordDto>(
            @"SELECT a.absence_id AS AbsenceId, a.absence_date AS AbsenceDate,
                     a.status AS Status, a.reason AS Reason,
                     sub.subject_name AS SubjectName,
                     CONCAT(t.first_name, ' ', t.last_name) AS TeacherName
              FROM absence a
              INNER JOIN subjects sub ON a.subject_id = sub.subject_id
              INNER JOIN teachers t ON a.teacher_id = t.teacher_id
              WHERE a.student_id = @StudentId
                AND MONTH(a.absence_date) = @Month
                AND YEAR(a.absence_date) = @Year
              ORDER BY a.absence_date DESC, sub.subject_name",
            new { StudentId = studentId, Month = targetMonth, Year = targetYear });

        return Results.Ok(records);
    }

    private static async Task<IResult> GetAttendanceSummary(HttpContext httpContext, SqlConnection db)
    {
        var studentId = GetStudentId(httpContext);

        var summary = await db.QuerySingleAsync<AttendanceSummaryDto>(
            @"SELECT
                ISNULL(SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END), 0) AS TotalPresent,
                ISNULL(SUM(CASE WHEN status = 'absent' THEN 1 ELSE 0 END), 0) AS TotalAbsent,
                ISNULL(SUM(CASE WHEN status = 'late' THEN 1 ELSE 0 END), 0) AS TotalLate,
                ISNULL(SUM(CASE WHEN status = 'justified' THEN 1 ELSE 0 END), 0) AS TotalJustified,
                COUNT(*) AS TotalRecords
              FROM absence
              WHERE student_id = @StudentId",
            new { StudentId = studentId });

        return Results.Ok(summary);
    }

    private static async Task<IResult> GetFees(HttpContext httpContext, SqlConnection db)
    {
        var studentId = GetStudentId(httpContext);

        var fees = await db.QueryAsync<FeeDto>(
            @"SELECT fee_id AS FeeId, student_id AS StudentId, fee_type AS FeeType,
                     amount AS Amount, paid_amount AS PaidAmount, balance_amount AS BalanceAmount,
                     due_date AS DueDate, paid_date AS PaidDate, status AS Status,
                     payment_method AS PaymentMethod, transaction_ref AS TransactionRef,
                     created_at AS CreatedAt, updated_at AS UpdatedAt
              FROM fees
              WHERE student_id = @StudentId
              ORDER BY due_date DESC",
            new { StudentId = studentId });

        return Results.Ok(fees);
    }

    private static async Task<IResult> GetFeeSummary(HttpContext httpContext, SqlConnection db)
    {
        var studentId = GetStudentId(httpContext);

        var summary = await db.QuerySingleAsync<FeeSummaryDto>(
            @"SELECT
                ISNULL(SUM(amount), 0) AS TotalBilled,
                ISNULL(SUM(paid_amount), 0) AS TotalPaid,
                ISNULL(SUM(balance_amount), 0) AS TotalOutstanding,
                COUNT(*) AS TotalInvoices,
                ISNULL(SUM(CASE WHEN status = 'paid' THEN 1 ELSE 0 END), 0) AS PaidInvoices,
                ISNULL(SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END), 0) AS PendingInvoices,
                ISNULL(SUM(CASE WHEN status = 'overdue' THEN 1 ELSE 0 END), 0) AS OverdueInvoices
              FROM fees
              WHERE student_id = @StudentId",
            new { StudentId = studentId });

        return Results.Ok(summary);
    }

    private static async Task<IResult> GetMessages(
        HttpContext httpContext,
        SqlConnection db,
        string? type = null,
        bool? unreadOnly = null,
        int offset = 0,
        int limit = 20)
    {
        var studentId = GetStudentId(httpContext);
        var schoolId = GetSchoolId(httpContext);
        var className = GetClassName(httpContext);
        var section = GetSection(httpContext);

        var whereClause = @"WHERE m.school_id = @SchoolId
            AND (
                (m.is_broadcast = 0 AND m.student_id = @StudentId)
                OR (m.is_broadcast = 1 AND (m.target_class IS NULL OR m.target_class = @ClassName)
                    AND (m.target_section IS NULL OR m.target_section = @Section))
            )";

        if (!string.IsNullOrEmpty(type)) whereClause += " AND m.message_type = @Type";
        if (unreadOnly == true) whereClause += " AND m.is_read = 0";

        var totalCount = await db.QuerySingleAsync<int>(
            $"SELECT COUNT(*) FROM message2 m {whereClause}",
            new { SchoolId = schoolId, StudentId = studentId, ClassName = className, Section = section, Type = type });

        var messages = await db.QueryAsync<MessageDto>(
            $@"SELECT m.message_id AS MessageId, m.school_id AS SchoolId,
                      m.student_id AS StudentId, m.message_type AS MessageType,
                      m.subject AS Subject, m.body AS Body,
                      m.is_read AS IsRead, m.is_broadcast AS IsBroadcast,
                      m.target_class AS TargetClass, m.target_section AS TargetSection,
                      m.created_at AS CreatedAt, m.read_at AS ReadAt
               FROM message2 m
               {whereClause}
               ORDER BY m.created_at DESC
               OFFSET @Offset ROWS FETCH NEXT @Limit ROWS ONLY",
            new
            {
                SchoolId = schoolId,
                StudentId = studentId,
                ClassName = className,
                Section = section,
                Type = type,
                Offset = offset,
                Limit = limit
            });

        return Results.Ok(new PaginatedResponse<MessageDto>
        {
            Items = messages.ToList(),
            TotalCount = totalCount,
            Offset = offset,
            Limit = limit
        });
    }

    private static async Task<IResult> MarkMessageRead(int id, HttpContext httpContext, SqlConnection db)
    {
        var studentId = GetStudentId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE message2
              SET is_read = 1, read_at = GETUTCDATE()
              WHERE message_id = @Id AND student_id = @StudentId AND is_read = 0",
            new { Id = id, StudentId = studentId });

        return rows == 0
            ? Results.NotFound(new { error = "Message not found or already read." })
            : Results.Ok(new { message = "Message marked as read." });
    }
}
