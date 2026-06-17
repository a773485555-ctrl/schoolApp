namespace SchoolManagement.Api.Models.Admin;

public class DashboardMetrics
{
    public int TotalActiveStudents { get; set; }
    public int TotalActiveTeachers { get; set; }
    public int TodayTotalAbsences { get; set; }
    public decimal TotalBilled { get; set; }
    public decimal TotalCollected { get; set; }
    public decimal TotalOutstanding { get; set; }
}
