using System.Security.Claims;
using Dapper;
using Microsoft.Data.SqlClient;
using SchoolManagement.Api.Models.Auth;
using SchoolManagement.Api.Services;

namespace SchoolManagement.Api.Endpoints;

public static class AuthEndpoints
{
    public static void MapAuthEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/api/auth").WithTags("Authentication");

        group.MapPost("/login", Login);
        group.MapPost("/refresh", Refresh);
        group.MapPost("/change-password", ChangePassword).RequireAuthorization();
    }

    private static async Task<IResult> Login(
        LoginRequest request,
        SqlConnection db,
        JwtService jwtService,
        PasswordService passwordService)
    {
        if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Password))
        {
            return Results.BadRequest(new { error = "Email and password are required." });
        }

        var role = request.Role?.ToLowerInvariant();

        if (role == "admin" || role == "teacher")
        {
            var teacher = await db.QuerySingleOrDefaultAsync<dynamic>(
                @"SELECT teacher_id, school_id, first_name, last_name, email, password_hash, is_active
                  FROM teachers
                  WHERE email = @Email AND is_active = 1",
                new { request.Email });

            if (teacher is null)
            {
                return Results.Unauthorized();
            }

            if (!passwordService.VerifyPassword(request.Password, (string)teacher.password_hash))
            {
                return Results.Unauthorized();
            }

            var accessToken = jwtService.GenerateAccessToken(
                (int)teacher.teacher_id,
                role!,
                (int)teacher.school_id,
                null,
                null);

            var refreshToken = jwtService.GenerateRefreshToken();

            await db.ExecuteAsync(
                @"UPDATE teachers
                  SET refresh_token = @RefreshToken,
                      refresh_token_expiry = @Expiry
                  WHERE teacher_id = @TeacherId",
                new
                {
                    RefreshToken = refreshToken,
                    Expiry = DateTime.UtcNow.AddDays(jwtService.RefreshTokenExpiryDays),
                    TeacherId = (int)teacher.teacher_id
                });

            return Results.Ok(new AuthResponse
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                Role = role!,
                UserId = (int)teacher.teacher_id,
                SchoolId = (int)teacher.school_id,
                FullName = $"{teacher.first_name} {teacher.last_name}",
                Email = (string)teacher.email
            });
        }
        else if (role == "student")
        {
            var student = await db.QuerySingleOrDefaultAsync<dynamic>(
                @"SELECT student_id, school_id, first_name, last_name, email, password_hash,
                         class_name, section, is_active
                  FROM students
                  WHERE email = @Email AND is_active = 1",
                new { request.Email });

            if (student is null)
            {
                return Results.Unauthorized();
            }

            if (!passwordService.VerifyPassword(request.Password, (string)student.password_hash))
            {
                return Results.Unauthorized();
            }

            var accessToken = jwtService.GenerateAccessToken(
                (int)student.student_id,
                role,
                (int)student.school_id,
                (string)student.class_name,
                (string)student.section);

            var refreshToken = jwtService.GenerateRefreshToken();

            await db.ExecuteAsync(
                @"UPDATE students
                  SET refresh_token = @RefreshToken,
                      refresh_token_expiry = @Expiry
                  WHERE student_id = @StudentId",
                new
                {
                    RefreshToken = refreshToken,
                    Expiry = DateTime.UtcNow.AddDays(jwtService.RefreshTokenExpiryDays),
                    StudentId = (int)student.student_id
                });

            return Results.Ok(new AuthResponse
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                Role = role,
                UserId = (int)student.student_id,
                SchoolId = (int)student.school_id,
                FullName = $"{student.first_name} {student.last_name}",
                Email = (string)student.email
            });
        }

        return Results.BadRequest(new { error = "Invalid role. Must be 'admin', 'teacher', or 'student'." });
    }

    private static async Task<IResult> Refresh(
        RefreshRequest request,
        SqlConnection db,
        JwtService jwtService)
    {
        ClaimsPrincipal principal;
        try
        {
            principal = jwtService.GetPrincipalFromExpiredToken(request.AccessToken);
        }
        catch
        {
            return Results.Unauthorized();
        }

        var userId = int.Parse(principal.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var role = principal.FindFirstValue(ClaimTypes.Role)!;

        if (role == "admin" || role == "teacher")
        {
            var stored = await db.QuerySingleOrDefaultAsync<dynamic>(
                @"SELECT refresh_token, refresh_token_expiry, school_id, first_name, last_name, email
                  FROM teachers
                  WHERE teacher_id = @UserId AND is_active = 1",
                new { UserId = userId });

            if (stored is null ||
                (string)stored.refresh_token != request.RefreshToken ||
                (DateTime)stored.refresh_token_expiry <= DateTime.UtcNow)
            {
                return Results.Unauthorized();
            }

            var newAccessToken = jwtService.GenerateAccessToken(userId, role, (int)stored.school_id, null, null);
            var newRefreshToken = jwtService.GenerateRefreshToken();

            await db.ExecuteAsync(
                @"UPDATE teachers
                  SET refresh_token = @RefreshToken,
                      refresh_token_expiry = @Expiry
                  WHERE teacher_id = @TeacherId",
                new
                {
                    RefreshToken = newRefreshToken,
                    Expiry = DateTime.UtcNow.AddDays(jwtService.RefreshTokenExpiryDays),
                    TeacherId = userId
                });

            return Results.Ok(new AuthResponse
            {
                AccessToken = newAccessToken,
                RefreshToken = newRefreshToken,
                Role = role,
                UserId = userId,
                SchoolId = (int)stored.school_id,
                FullName = $"{stored.first_name} {stored.last_name}",
                Email = (string)stored.email
            });
        }
        else if (role == "student")
        {
            var stored = await db.QuerySingleOrDefaultAsync<dynamic>(
                @"SELECT refresh_token, refresh_token_expiry, school_id, first_name, last_name,
                         email, class_name, section
                  FROM students
                  WHERE student_id = @UserId AND is_active = 1",
                new { UserId = userId });

            if (stored is null ||
                (string)stored.refresh_token != request.RefreshToken ||
                (DateTime)stored.refresh_token_expiry <= DateTime.UtcNow)
            {
                return Results.Unauthorized();
            }

            var newAccessToken = jwtService.GenerateAccessToken(
                userId, role, (int)stored.school_id, (string)stored.class_name, (string)stored.section);
            var newRefreshToken = jwtService.GenerateRefreshToken();

            await db.ExecuteAsync(
                @"UPDATE students
                  SET refresh_token = @RefreshToken,
                      refresh_token_expiry = @Expiry
                  WHERE student_id = @StudentId",
                new
                {
                    RefreshToken = newRefreshToken,
                    Expiry = DateTime.UtcNow.AddDays(jwtService.RefreshTokenExpiryDays),
                    StudentId = userId
                });

            return Results.Ok(new AuthResponse
            {
                AccessToken = newAccessToken,
                RefreshToken = newRefreshToken,
                Role = role,
                UserId = userId,
                SchoolId = (int)stored.school_id,
                FullName = $"{stored.first_name} {stored.last_name}",
                Email = (string)stored.email
            });
        }

        return Results.Unauthorized();
    }

    private static async Task<IResult> ChangePassword(
        ChangePasswordRequest request,
        HttpContext httpContext,
        SqlConnection db,
        PasswordService passwordService)
    {
        var userId = int.Parse(httpContext.User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var role = httpContext.User.FindFirstValue(ClaimTypes.Role)!;

        if (string.IsNullOrWhiteSpace(request.NewPassword) || request.NewPassword.Length < 6)
        {
            return Results.BadRequest(new { error = "New password must be at least 6 characters." });
        }

        if (role == "admin" || role == "teacher")
        {
            var currentHash = await db.QuerySingleOrDefaultAsync<string>(
                "SELECT password_hash FROM teachers WHERE teacher_id = @UserId",
                new { UserId = userId });

            if (currentHash is null)
            {
                return Results.NotFound(new { error = "User not found." });
            }

            if (!passwordService.VerifyPassword(request.CurrentPassword, currentHash))
            {
                return Results.BadRequest(new { error = "Current password is incorrect." });
            }

            var newHash = passwordService.HashPassword(request.NewPassword);

            await db.ExecuteAsync(
                @"UPDATE teachers SET password_hash = @Hash, updated_at = @Now WHERE teacher_id = @UserId",
                new { Hash = newHash, Now = DateTime.UtcNow, UserId = userId });

            return Results.Ok(new { message = "Password updated successfully." });
        }
        else if (role == "student")
        {
            var currentHash = await db.QuerySingleOrDefaultAsync<string>(
                "SELECT password_hash FROM students WHERE student_id = @UserId",
                new { UserId = userId });

            if (currentHash is null)
            {
                return Results.NotFound(new { error = "User not found." });
            }

            if (!passwordService.VerifyPassword(request.CurrentPassword, currentHash))
            {
                return Results.BadRequest(new { error = "Current password is incorrect." });
            }

            var newHash = passwordService.HashPassword(request.NewPassword);

            await db.ExecuteAsync(
                @"UPDATE students SET password_hash = @Hash, updated_at = @Now WHERE student_id = @UserId",
                new { Hash = newHash, Now = DateTime.UtcNow, UserId = userId });

            return Results.Ok(new { message = "Password updated successfully." });
        }

        return Results.BadRequest(new { error = "Invalid role." });
    }
}
