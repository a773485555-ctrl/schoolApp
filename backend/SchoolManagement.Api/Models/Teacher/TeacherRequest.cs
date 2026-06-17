namespace SchoolManagement.Api.Models.Teacher;

public class TeacherRequest
{
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Phone { get; set; } = string.Empty;
    public string Gender { get; set; } = string.Empty;
    public DateTime? DateOfBirth { get; set; }
    public string Address { get; set; } = string.Empty;
    public string Qualification { get; set; } = string.Empty;
    public DateTime? JoinDate { get; set; }
    public string? Password { get; set; }
}
