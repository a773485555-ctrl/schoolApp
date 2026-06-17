namespace SchoolManagement.Api.Models.Teacher;

public class AttendanceSubmitDto
{
    public int StudentId { get; set; }
    public int SubjectId { get; set; }
    public DateTime AbsenceDate { get; set; }
    public string Status { get; set; } = string.Empty;
    public string? Reason { get; set; }
}
