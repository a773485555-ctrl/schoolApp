namespace SchoolManagement.Api.Models.Student;

public class AttendanceRecordDto
{
    public int AbsenceId { get; set; }
    public DateTime AbsenceDate { get; set; }
    public string Status { get; set; } = string.Empty;
    public string? Reason { get; set; }
    public string SubjectName { get; set; } = string.Empty;
    public string TeacherName { get; set; } = string.Empty;
}
