namespace SchoolManagement.Api.Models.Student;

public class AttendanceSummaryDto
{
    public int TotalPresent { get; set; }
    public int TotalAbsent { get; set; }
    public int TotalLate { get; set; }
    public int TotalJustified { get; set; }
    public int TotalRecords { get; set; }
}
