// Example/Server/src/main/java/SubjectAreaOrg/SubmittedResume.java
// Example/Client/src/main/java/SubjectAreaOrg/SubmittedResume.java
package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SubmittedResume implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int candidateId; // Может быть 0 или реальное ID кандидата
    private int employeeId;  // НОВОЕ ПОЛЕ: Может быть 0 или реальное ID сотрудника
    private String candidateName; // Для удобства отображения
    private String employeeName;  // НОВОЕ ПОЛЕ: Для удобства отображения
    private int vacancyId;
    private String vacancyName; // Для удобства отображения
    private String resumeText;
    private String coverLetter;
    private LocalDateTime submissionDate;
    private String status;
    private String hrFeedback;

    public SubmittedResume() {
        this.id = 0;
        this.candidateId = 0;
        this.employeeId = 0; // Инициализация
        this.candidateName = "";
        this.employeeName = ""; // Инициализация
        this.vacancyId = 0;
        this.vacancyName = "";
        this.resumeText = "";
        this.coverLetter = "";
        this.submissionDate = null;
        this.status = "";
        this.hrFeedback = "";
    }

    public SubmittedResume(int id, int candidateId, int employeeId, String candidateName, String employeeName, int vacancyId, String vacancyName, String resumeText, String coverLetter, LocalDateTime submissionDate, String status, String hrFeedback) {
        this.id = id;
        this.candidateId = candidateId;
        this.employeeId = employeeId;
        this.candidateName = candidateName;
        this.employeeName = employeeName;
        this.vacancyId = vacancyId;
        this.vacancyName = vacancyName;
        this.resumeText = resumeText;
        this.coverLetter = coverLetter;
        this.submissionDate = submissionDate;
        this.status = status;
        this.hrFeedback = hrFeedback;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public int getEmployeeId() { return employeeId; } // НОВЫЙ ГЕТТЕР
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; } // НОВЫЙ СЕТТЕР

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getEmployeeName() { return employeeName; } // НОВЫЙ ГЕТТЕР
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; } // НОВЫЙ СЕТТЕР

    public int getVacancyId() { return vacancyId; }
    public void setVacancyId(int vacancyId) { this.vacancyId = vacancyId; }

    public String getVacancyName() { return vacancyName; }
    public void setVacancyName(String vacancyName) { this.vacancyName = vacancyName; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHrFeedback() { return hrFeedback; }
    public void setHrFeedback(String hrFeedback) { this.hrFeedback = hrFeedback; }

    @Override
    public String toString() {
        return "SubmittedResume{" +
                "id=" + id +
                ", candidateId=" + candidateId +
                ", employeeId=" + employeeId + // Добавлено
                ", candidateName='" + candidateName + '\'' +
                ", employeeName='" + employeeName + '\'' + // Добавлено
                ", vacancyId=" + vacancyId +
                ", vacancyName='" + vacancyName + '\'' +
                ", resumeText='" + resumeText + '\'' +
                ", coverLetter='" + coverLetter + '\'' +
                ", submissionDate=" + submissionDate +
                ", status='" + status + '\'' +
                ", hrFeedback='" + hrFeedback + '\'' +
                '}';
    }
}