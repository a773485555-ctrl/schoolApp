namespace SchoolManagement.Api.Models.Teacher;

public class HomeworkRequest
{
    public int SubjectId { get; set; }
    public string ClassName { get; set; } = string.Empty;
    public string Section { get; set; } = string.Empty;
    public string Title { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
    public DateTime AssignedDate { get; set; }
    public DateTime DueDate { get; set; }
}
