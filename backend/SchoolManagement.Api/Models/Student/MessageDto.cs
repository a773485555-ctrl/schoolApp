namespace SchoolManagement.Api.Models.Student;

public class MessageDto
{
    public int MessageId { get; set; }
    public int SchoolId { get; set; }
    public int? StudentId { get; set; }
    public string MessageType { get; set; } = string.Empty;
    public string Subject { get; set; } = string.Empty;
    public string Body { get; set; } = string.Empty;
    public bool IsRead { get; set; }
    public bool IsBroadcast { get; set; }
    public string? TargetClass { get; set; }
    public string? TargetSection { get; set; }
    public DateTime CreatedAt { get; set; }
    public DateTime? ReadAt { get; set; }
}
