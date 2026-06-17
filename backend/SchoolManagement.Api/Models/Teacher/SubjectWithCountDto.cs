namespace SchoolManagement.Api.Models.Teacher;

public class SubjectWithCountDto
{
    public int SubjectId { get; set; }
    public string SubjectName { get; set; } = string.Empty;
    public string ClassName { get; set; } = string.Empty;
    public string Section { get; set; } = string.Empty;
    public int StudentCount { get; set; }
}
