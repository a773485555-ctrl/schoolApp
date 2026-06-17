using System.Security.Claims;
using Dapper;
using Microsoft.Data.SqlClient;
using SchoolManagement.Api.Models.Student;
using SchoolManagement.Api.Models.Teacher;

namespace SchoolManagement.Api.Endpoints;

public static class TeacherEndpoints
{
    public static void MapTeacherEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/api/teacher")
            .WithTags("Teacher")
            .RequireAuthorization(policy => policy.RequireRole("teacher"));

        group.MapGet("/subjects", GetSubjects);

        group.MapGet("/attendance/students", GetAttendanceStudents);
        group.MapGet("/attendance", GetAttendance);
        group.MapPost("/attendance/batch", BatchUpsertAttendance);
        group.MapPut("/attendance/{id:int}", UpdateAttendance);

        group.MapGet("/homework", GetHomework);
        group.MapPost("/homework", CreateHomework);
        group.MapPut("/homework/{id:int}", UpdateHomework);
        group.MapDelete("/homework/{id:int}", DeleteHomework);
    }

    private static int GetTeacherId(HttpContext ctx) =>
        int.Parse(ctx.User.FindFirstValue(ClaimTypes.NameIdentifier)!);

    private static int GetSchoolId(HttpContext ctx) =>
        int.Parse(ctx.User.FindFirstValue("school_id")!);

    private static async Task<IResult> GetSubjects(HttpContext httpContext, SqlConnection db)
    {
        var teacherId = GetTeacherId(httpContext);

        var subjects = await db.QueryAsync<SubjectWithCountDto>(
            @"SELECT
                sub.subject_id AS SubjectId,
                sub.subject_name AS SubjectName,
                sub.class_name AS ClassName,
                sub.section AS Section,
                (SELECT COUNT(*) FROM students st
                 WHERE st.class_name = sub.class_name
                   AND st.section = sub.section
                   AND st.school_id = sub.school_id
                   AND st.is_active = 1) AS StudentCount
              FROM subjects sub
              WHERE sub.teacher_id = @TeacherId
              ORDER BY sub.class_name, sub.section, sub.subject_name",
            new { TeacherId = teacherId });

        return Results.Ok(subjects);
    }

    private static async Task<IResult> GetAttendanceStudents(
        HttpContext httpContext,
        SqlConnection db,
        string? @class = null,
        string? section = null)
    {
        var schoolId = GetSchoolId(httpContext);

        if (string.IsNullOrEmpty(@class) || string.IsNullOrEmpty(section))
        {
            return Results.BadRequest(new { error = "Both 'class' and 'section' query parameters are required." });
        }

        var students = await db.QueryAsync<StudentRosterDto>(
            @"SELECT student_id AS StudentId, first_name AS FirstName, last_name AS LastName,
                     roll_number AS RollNumber, class_name AS ClassName, section AS Section
              FROM students
              WHERE school_id = @SchoolId AND class_name = @ClassName AND section = @Section
                AND is_active = 1
              ORDER BY roll_number",
            new { SchoolId = schoolId, ClassName = @class, Section = section });

        return Results.Ok(students);
    }

    private static async Task<IResult> GetAttendance(
        HttpContext httpContext,
        SqlConnection db,
        string? @class = null,
        string? section = null,
        DateTime? date = null)
    {
        var schoolId = GetSchoolId(httpContext);
        var teacherId = GetTeacherId(httpContext);

        if (string.IsNullOrEmpty(@class) || string.IsNullOrEmpty(section))
        {
            return Results.BadRequest(new { error = "Both 'class' and 'section' query parameters are required." });
        }

        var absenceDate = date ?? DateTime.UtcNow.Date;

        var records = await db.QueryAsync<AbsenceDto>(
            @"SELECT a.absence_id AS AbsenceId, a.student_id AS StudentId,
                     a.subject_id AS SubjectId, a.teacher_id AS TeacherId,
                     a.absence_date AS AbsenceDate, a.status AS Status,
                     a.reason AS Reason, a.created_at AS CreatedAt, a.updated_at AS UpdatedAt
              FROM absence a
              INNER JOIN students s ON a.student_id = s.student_id
              WHERE s.school_id = @SchoolId
                AND s.class_name = @ClassName
                AND s.section = @Section
                AND CAST(a.absence_date AS DATE) = CAST(@AbsenceDate AS DATE)
                AND a.teacher_id = @TeacherId
              ORDER BY s.roll_number",
            new { SchoolId = schoolId, ClassName = @class, Section = section, AbsenceDate = absenceDate, TeacherId = teacherId });

        return Results.Ok(records);
    }

    private static async Task<IResult> BatchUpsertAttendance(
        List<AttendanceSubmitDto> records,
        HttpContext httpContext,
        SqlConnection db)
    {
        var teacherId = GetTeacherId(httpContext);

        if (records is null || records.Count == 0)
        {
            return Results.BadRequest(new { error = "No attendance records provided." });
        }

        int inserted = 0;
        int updated = 0;

        foreach (var record in records)
        {
            var result = await db.QuerySingleAsync<string>(
                @"MERGE INTO absence AS target
                  USING (SELECT @StudentId AS student_id, @SubjectId AS subject_id,
                                @AbsenceDate AS absence_date, @TeacherId AS teacher_id) AS source
                  ON target.student_id = source.student_id
                     AND target.subject_id = source.subject_id
                     AND CAST(target.absence_date AS DATE) = CAST(source.absence_date AS DATE)
                     AND target.teacher_id = source.teacher_id
                  WHEN MATCHED THEN
                      UPDATE SET status = @Status, reason = @Reason, updated_at = GETUTCDATE()
                  WHEN NOT MATCHED THEN
                      INSERT (student_id, subject_id, teacher_id, absence_date, status, reason, created_at)
                      VALUES (@StudentId, @SubjectId, @TeacherId, @AbsenceDate, @Status, @Reason, GETUTCDATE())
                  OUTPUT $action;",
                new
                {
                    record.StudentId,
                    record.SubjectId,
                    TeacherId = teacherId,
                    record.AbsenceDate,
                    record.Status,
                    record.Reason
                });

            if (result == "INSERT") inserted++;
            else updated++;
        }

        return Results.Ok(new BatchResult
        {
            Inserted = inserted,
            Updated = updated,
            Total = records.Count
        });
    }

    private static async Task<IResult> UpdateAttendance(
        int id,
        AttendanceSubmitDto record,
        HttpContext httpContext,
        SqlConnection db)
    {
        var teacherId = GetTeacherId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE absence
              SET status = @Status, reason = @Reason, updated_at = GETUTCDATE()
              WHERE absence_id = @Id AND teacher_id = @TeacherId",
            new { Id = id, TeacherId = teacherId, record.Status, record.Reason });

        return rows == 0
            ? Results.NotFound(new { error = "Attendance record not found or you do not own it." })
            : Results.Ok(new { message = "Attendance record updated." });
    }

    private static async Task<IResult> GetHomework(
        HttpContext httpContext,
        SqlConnection db,
        int? subjectId = null,
        DateTime? from = null,
        DateTime? to = null)
    {
        var teacherId = GetTeacherId(httpContext);

        var whereClause = "WHERE h.teacher_id = @TeacherId";
        if (subjectId.HasValue) whereClause += " AND h.subject_id = @SubjectId";
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
            new { TeacherId = teacherId, SubjectId = subjectId, From = from, To = to });

        return Results.Ok(homework);
    }

    private static async Task<IResult> CreateHomework(
        HomeworkRequest request,
        HttpContext httpContext,
        SqlConnection db)
    {
        var teacherId = GetTeacherId(httpContext);

        var newId = await db.QuerySingleAsync<int>(
            @"INSERT INTO homework
                (teacher_id, subject_id, class_name, section, title, description,
                 assigned_date, due_date, created_at)
              VALUES
                (@TeacherId, @SubjectId, @ClassName, @Section, @Title, @Description,
                 @AssignedDate, @DueDate, GETUTCDATE());
              SELECT CAST(SCOPE_IDENTITY() AS INT);",
            new
            {
                TeacherId = teacherId,
                request.SubjectId,
                request.ClassName,
                request.Section,
                request.Title,
                request.Description,
                request.AssignedDate,
                request.DueDate
            });

        return Results.Created($"/api/teacher/homework/{newId}", new { homeworkId = newId });
    }

    private static async Task<IResult> UpdateHomework(
        int id,
        HomeworkRequest request,
        HttpContext httpContext,
        SqlConnection db)
    {
        var teacherId = GetTeacherId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE homework
              SET subject_id = @SubjectId, class_name = @ClassName, section = @Section,
                  title = @Title, description = @Description,
                  assigned_date = @AssignedDate, due_date = @DueDate,
                  updated_at = GETUTCDATE()
              WHERE homework_id = @Id AND teacher_id = @TeacherId",
            new
            {
                Id = id,
                TeacherId = teacherId,
                request.SubjectId,
                request.ClassName,
                request.Section,
                request.Title,
                request.Description,
                request.AssignedDate,
                request.DueDate
            });

        return rows == 0
            ? Results.NotFound(new { error = "Homework not found or you do not own it." })
            : Results.Ok(new { message = "Homework updated successfully." });
    }

    private static async Task<IResult> DeleteHomework(int id, HttpContext httpContext, SqlConnection db)
    {
        var teacherId = GetTeacherId(httpContext);

        var rows = await db.ExecuteAsync(
            "DELETE FROM homework WHERE homework_id = @Id AND teacher_id = @TeacherId",
            new { Id = id, TeacherId = teacherId });

        return rows == 0
            ? Results.NotFound(new { error = "Homework not found or you do not own it." })
            : Results.Ok(new { message = "Homework deleted successfully." });
    }
}
