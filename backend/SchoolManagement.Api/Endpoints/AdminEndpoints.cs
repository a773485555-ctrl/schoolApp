using System.Security.Claims;
using Dapper;
using Microsoft.Data.SqlClient;
using SchoolManagement.Api.Models.Admin;
using SchoolManagement.Api.Models.Common;
using SchoolManagement.Api.Models.Student;
using SchoolManagement.Api.Models.Teacher;
using SchoolManagement.Api.Services;

namespace SchoolManagement.Api.Endpoints;

public static class AdminEndpoints
{
    public static void MapAdminEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/api/admin")
            .WithTags("Admin")
            .RequireAuthorization(policy => policy.RequireRole("admin"));

        group.MapGet("/dashboard", GetDashboard);

        group.MapGet("/teachers", GetTeachers);
        group.MapGet("/teachers/{id:int}", GetTeacherById);
        group.MapPost("/teachers", CreateTeacher);
        group.MapPut("/teachers/{id:int}", UpdateTeacher);
        group.MapDelete("/teachers/{id:int}", DeleteTeacher);

        group.MapGet("/students", GetStudents);
        group.MapGet("/students/{id:int}", GetStudentById);
        group.MapPost("/students", CreateStudent);
        group.MapPut("/students/{id:int}", UpdateStudent);
        group.MapDelete("/students/{id:int}", DeleteStudent);

        group.MapGet("/school", GetSchool);
        group.MapPut("/school", UpdateSchool);
    }

    private static int GetSchoolId(HttpContext ctx) =>
        int.Parse(ctx.User.FindFirstValue("school_id")!);

    private static async Task<IResult> GetDashboard(HttpContext httpContext, SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var metrics = await db.QuerySingleAsync<DashboardMetrics>(
            @"SELECT
                (SELECT COUNT(*) FROM students WHERE school_id = @SchoolId AND is_active = 1) AS TotalActiveStudents,
                (SELECT COUNT(*) FROM teachers WHERE school_id = @SchoolId AND is_active = 1) AS TotalActiveTeachers,
                (SELECT COUNT(*) FROM absence a
                    INNER JOIN students s ON a.student_id = s.student_id
                    WHERE s.school_id = @SchoolId AND CAST(a.absence_date AS DATE) = CAST(GETUTCDATE() AS DATE)
                      AND a.status = 'absent') AS TodayTotalAbsences,
                (SELECT ISNULL(SUM(amount), 0) FROM fees f
                    INNER JOIN students s ON f.student_id = s.student_id
                    WHERE s.school_id = @SchoolId) AS TotalBilled,
                (SELECT ISNULL(SUM(paid_amount), 0) FROM fees f
                    INNER JOIN students s ON f.student_id = s.student_id
                    WHERE s.school_id = @SchoolId) AS TotalCollected,
                (SELECT ISNULL(SUM(balance_amount), 0) FROM fees f
                    INNER JOIN students s ON f.student_id = s.student_id
                    WHERE s.school_id = @SchoolId) AS TotalOutstanding",
            new { SchoolId = schoolId });

        return Results.Ok(metrics);
    }

    private static async Task<IResult> GetTeachers(
        HttpContext httpContext,
        SqlConnection db,
        int offset = 0,
        int limit = 20)
    {
        var schoolId = GetSchoolId(httpContext);

        var totalCount = await db.QuerySingleAsync<int>(
            "SELECT COUNT(*) FROM teachers WHERE school_id = @SchoolId AND is_active = 1",
            new { SchoolId = schoolId });

        var teachers = await db.QueryAsync<TeacherDto>(
            @"SELECT teacher_id AS TeacherId, school_id AS SchoolId, first_name AS FirstName,
                     last_name AS LastName, email AS Email, phone AS Phone, gender AS Gender,
                     date_of_birth AS DateOfBirth, address AS Address, qualification AS Qualification,
                     join_date AS JoinDate, is_active AS IsActive, created_at AS CreatedAt,
                     updated_at AS UpdatedAt
              FROM teachers
              WHERE school_id = @SchoolId AND is_active = 1
              ORDER BY last_name, first_name
              OFFSET @Offset ROWS FETCH NEXT @Limit ROWS ONLY",
            new { SchoolId = schoolId, Offset = offset, Limit = limit });

        return Results.Ok(new PaginatedResponse<TeacherDto>
        {
            Items = teachers.ToList(),
            TotalCount = totalCount,
            Offset = offset,
            Limit = limit
        });
    }

    private static async Task<IResult> GetTeacherById(int id, HttpContext httpContext, SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var teacher = await db.QuerySingleOrDefaultAsync<TeacherDto>(
            @"SELECT teacher_id AS TeacherId, school_id AS SchoolId, first_name AS FirstName,
                     last_name AS LastName, email AS Email, phone AS Phone, gender AS Gender,
                     date_of_birth AS DateOfBirth, address AS Address, qualification AS Qualification,
                     join_date AS JoinDate, is_active AS IsActive, created_at AS CreatedAt,
                     updated_at AS UpdatedAt
              FROM teachers
              WHERE teacher_id = @Id AND school_id = @SchoolId",
            new { Id = id, SchoolId = schoolId });

        return teacher is null
            ? Results.NotFound(new { error = "Teacher not found." })
            : Results.Ok(teacher);
    }

    private static async Task<IResult> CreateTeacher(
        TeacherRequest request,
        HttpContext httpContext,
        SqlConnection db,
        PasswordService passwordService)
    {
        var schoolId = GetSchoolId(httpContext);

        var existingEmail = await db.QuerySingleOrDefaultAsync<int?>(
            "SELECT teacher_id FROM teachers WHERE email = @Email",
            new { request.Email });

        if (existingEmail.HasValue)
        {
            return Results.Conflict(new { error = "A teacher with this email already exists." });
        }

        var passwordHash = passwordService.HashPassword(request.Password ?? "changeme123");

        var newId = await db.QuerySingleAsync<int>(
            @"INSERT INTO teachers
                (school_id, first_name, last_name, email, phone, gender, date_of_birth,
                 address, qualification, join_date, password_hash, is_active, created_at)
              VALUES
                (@SchoolId, @FirstName, @LastName, @Email, @Phone, @Gender, @DateOfBirth,
                 @Address, @Qualification, @JoinDate, @PasswordHash, 1, GETUTCDATE());
              SELECT CAST(SCOPE_IDENTITY() AS INT);",
            new
            {
                SchoolId = schoolId,
                request.FirstName,
                request.LastName,
                request.Email,
                request.Phone,
                request.Gender,
                request.DateOfBirth,
                request.Address,
                request.Qualification,
                request.JoinDate,
                PasswordHash = passwordHash
            });

        return Results.Created($"/api/admin/teachers/{newId}", new { teacherId = newId });
    }

    private static async Task<IResult> UpdateTeacher(
        int id,
        TeacherRequest request,
        HttpContext httpContext,
        SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE teachers
              SET first_name = @FirstName, last_name = @LastName, email = @Email,
                  phone = @Phone, gender = @Gender, date_of_birth = @DateOfBirth,
                  address = @Address, qualification = @Qualification, join_date = @JoinDate,
                  updated_at = GETUTCDATE()
              WHERE teacher_id = @Id AND school_id = @SchoolId",
            new
            {
                Id = id,
                SchoolId = schoolId,
                request.FirstName,
                request.LastName,
                request.Email,
                request.Phone,
                request.Gender,
                request.DateOfBirth,
                request.Address,
                request.Qualification,
                request.JoinDate
            });

        return rows == 0
            ? Results.NotFound(new { error = "Teacher not found." })
            : Results.Ok(new { message = "Teacher updated successfully." });
    }

    private static async Task<IResult> DeleteTeacher(int id, HttpContext httpContext, SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE teachers SET is_active = 0, updated_at = GETUTCDATE()
              WHERE teacher_id = @Id AND school_id = @SchoolId",
            new { Id = id, SchoolId = schoolId });

        return rows == 0
            ? Results.NotFound(new { error = "Teacher not found." })
            : Results.Ok(new { message = "Teacher deactivated successfully." });
    }

    private static async Task<IResult> GetStudents(
        HttpContext httpContext,
        SqlConnection db,
        string? @class = null,
        string? section = null,
        int offset = 0,
        int limit = 20)
    {
        var schoolId = GetSchoolId(httpContext);

        var whereClause = "WHERE school_id = @SchoolId AND is_active = 1";
        if (!string.IsNullOrEmpty(@class)) whereClause += " AND class_name = @ClassName";
        if (!string.IsNullOrEmpty(section)) whereClause += " AND section = @Section";

        var totalCount = await db.QuerySingleAsync<int>(
            $"SELECT COUNT(*) FROM students {whereClause}",
            new { SchoolId = schoolId, ClassName = @class, Section = section });

        var students = await db.QueryAsync<StudentDto>(
            $@"SELECT student_id AS StudentId, school_id AS SchoolId, first_name AS FirstName,
                      last_name AS LastName, email AS Email, phone AS Phone, gender AS Gender,
                      date_of_birth AS DateOfBirth, address AS Address, class_name AS ClassName,
                      section AS Section, roll_number AS RollNumber, guardian_name AS GuardianName,
                      guardian_phone AS GuardianPhone, admission_date AS AdmissionDate,
                      is_active AS IsActive, created_at AS CreatedAt, updated_at AS UpdatedAt
               FROM students
               {whereClause}
               ORDER BY class_name, section, roll_number
               OFFSET @Offset ROWS FETCH NEXT @Limit ROWS ONLY",
            new { SchoolId = schoolId, ClassName = @class, Section = section, Offset = offset, Limit = limit });

        return Results.Ok(new PaginatedResponse<StudentDto>
        {
            Items = students.ToList(),
            TotalCount = totalCount,
            Offset = offset,
            Limit = limit
        });
    }

    private static async Task<IResult> GetStudentById(int id, HttpContext httpContext, SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var student = await db.QuerySingleOrDefaultAsync<StudentDto>(
            @"SELECT student_id AS StudentId, school_id AS SchoolId, first_name AS FirstName,
                     last_name AS LastName, email AS Email, phone AS Phone, gender AS Gender,
                     date_of_birth AS DateOfBirth, address AS Address, class_name AS ClassName,
                     section AS Section, roll_number AS RollNumber, guardian_name AS GuardianName,
                     guardian_phone AS GuardianPhone, admission_date AS AdmissionDate,
                     is_active AS IsActive, created_at AS CreatedAt, updated_at AS UpdatedAt
              FROM students
              WHERE student_id = @Id AND school_id = @SchoolId",
            new { Id = id, SchoolId = schoolId });

        return student is null
            ? Results.NotFound(new { error = "Student not found." })
            : Results.Ok(student);
    }

    private static async Task<IResult> CreateStudent(
        StudentRequest request,
        HttpContext httpContext,
        SqlConnection db,
        PasswordService passwordService)
    {
        var schoolId = GetSchoolId(httpContext);

        var existingEmail = await db.QuerySingleOrDefaultAsync<int?>(
            "SELECT student_id FROM students WHERE email = @Email",
            new { request.Email });

        if (existingEmail.HasValue)
        {
            return Results.Conflict(new { error = "A student with this email already exists." });
        }

        var passwordHash = passwordService.HashPassword(request.Password ?? "changeme123");

        var newId = await db.QuerySingleAsync<int>(
            @"INSERT INTO students
                (school_id, first_name, last_name, email, phone, gender, date_of_birth,
                 address, class_name, section, roll_number, guardian_name, guardian_phone,
                 admission_date, password_hash, is_active, created_at)
              VALUES
                (@SchoolId, @FirstName, @LastName, @Email, @Phone, @Gender, @DateOfBirth,
                 @Address, @ClassName, @Section, @RollNumber, @GuardianName, @GuardianPhone,
                 @AdmissionDate, @PasswordHash, 1, GETUTCDATE());
              SELECT CAST(SCOPE_IDENTITY() AS INT);",
            new
            {
                SchoolId = schoolId,
                request.FirstName,
                request.LastName,
                request.Email,
                request.Phone,
                request.Gender,
                request.DateOfBirth,
                request.Address,
                request.ClassName,
                request.Section,
                request.RollNumber,
                request.GuardianName,
                request.GuardianPhone,
                request.AdmissionDate,
                PasswordHash = passwordHash
            });

        return Results.Created($"/api/admin/students/{newId}", new { studentId = newId });
    }

    private static async Task<IResult> UpdateStudent(
        int id,
        StudentRequest request,
        HttpContext httpContext,
        SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE students
              SET first_name = @FirstName, last_name = @LastName, email = @Email,
                  phone = @Phone, gender = @Gender, date_of_birth = @DateOfBirth,
                  address = @Address, class_name = @ClassName, section = @Section,
                  roll_number = @RollNumber, guardian_name = @GuardianName,
                  guardian_phone = @GuardianPhone, admission_date = @AdmissionDate,
                  updated_at = GETUTCDATE()
              WHERE student_id = @Id AND school_id = @SchoolId",
            new
            {
                Id = id,
                SchoolId = schoolId,
                request.FirstName,
                request.LastName,
                request.Email,
                request.Phone,
                request.Gender,
                request.DateOfBirth,
                request.Address,
                request.ClassName,
                request.Section,
                request.RollNumber,
                request.GuardianName,
                request.GuardianPhone,
                request.AdmissionDate
            });

        return rows == 0
            ? Results.NotFound(new { error = "Student not found." })
            : Results.Ok(new { message = "Student updated successfully." });
    }

    private static async Task<IResult> DeleteStudent(int id, HttpContext httpContext, SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE students SET is_active = 0, updated_at = GETUTCDATE()
              WHERE student_id = @Id AND school_id = @SchoolId",
            new { Id = id, SchoolId = schoolId });

        return rows == 0
            ? Results.NotFound(new { error = "Student not found." })
            : Results.Ok(new { message = "Student deactivated successfully." });
    }

    private static async Task<IResult> GetSchool(HttpContext httpContext, SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var school = await db.QuerySingleOrDefaultAsync<SchoolDto>(
            @"SELECT school_id AS SchoolId, school_name AS SchoolName, address AS Address,
                     city AS City, state AS State, zip_code AS ZipCode, phone AS Phone,
                     email AS Email, website AS Website, logo_url AS LogoUrl,
                     principal_name AS PrincipalName, is_active AS IsActive,
                     created_at AS CreatedAt, updated_at AS UpdatedAt
              FROM schools
              WHERE school_id = @SchoolId",
            new { SchoolId = schoolId });

        return school is null
            ? Results.NotFound(new { error = "School not found." })
            : Results.Ok(school);
    }

    private static async Task<IResult> UpdateSchool(
        SchoolConfigRequest request,
        HttpContext httpContext,
        SqlConnection db)
    {
        var schoolId = GetSchoolId(httpContext);

        var rows = await db.ExecuteAsync(
            @"UPDATE schools
              SET school_name = @SchoolName, address = @Address, city = @City,
                  state = @State, zip_code = @ZipCode, phone = @Phone,
                  email = @Email, website = @Website, logo_url = @LogoUrl,
                  principal_name = @PrincipalName, updated_at = GETUTCDATE()
              WHERE school_id = @SchoolId",
            new
            {
                SchoolId = schoolId,
                request.SchoolName,
                request.Address,
                request.City,
                request.State,
                request.ZipCode,
                request.Phone,
                request.Email,
                request.Website,
                request.LogoUrl,
                request.PrincipalName
            });

        return rows == 0
            ? Results.NotFound(new { error = "School not found." })
            : Results.Ok(new { message = "School updated successfully." });
    }
}
