package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TestResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int candidateId;
    private int vacancyId; // К какой вакансии относится тест
    private String testQuestion; // Вопрос, если тест простой
    private String candidateAnswer; // Ответ кандидата
    private LocalDateTime submissionDate;
    private String status; // Например, "На рассмотрении", "Проверен", "Неудовлетворительно"
    private String hrComment; // Комментарий HR-менеджера после проверки

    public TestResult() {
        this.id = 0;
        this.candidateId = 0;
        this.vacancyId = 0;
        this.testQuestion = "";
        this.candidateAnswer = "";
        this.submissionDate = LocalDateTime.now();
        this.status = "На рассмотрении";
        this.hrComment = "";
    }

    public TestResult(int id, int candidateId, int vacancyId, String testQuestion, String candidateAnswer, LocalDateTime submissionDate, String status, String hrComment) {
        this.id = id;
        this.candidateId = candidateId;
        this.vacancyId = vacancyId;
        this.testQuestion = testQuestion;
        this.candidateAnswer = candidateAnswer;
        this.submissionDate = submissionDate;
        this.status = status;
        this.hrComment = hrComment;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public int getVacancyId() { return vacancyId; }
    public void setVacancyId(int vacancyId) { this.vacancyId = vacancyId; }

    public String getTestQuestion() { return testQuestion; }
    public void setTestQuestion(String testQuestion) { this.testQuestion = testQuestion; }

    public String getCandidateAnswer() { return candidateAnswer; }
    public void setCandidateAnswer(String candidateAnswer) { this.candidateAnswer = candidateAnswer; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHrComment() { return hrComment; }
    public void setHrComment(String hrComment) { this.hrComment = hrComment; }

    @Override
    public String toString() {
        return "TestResult{" +
                "id=" + id +
                ", candidateId=" + candidateId +
                ", vacancyId=" + vacancyId +
                ", testQuestion='" + testQuestion + '\'' +
                ", candidateAnswer='" + candidateAnswer + '\'' +
                ", submissionDate=" + submissionDate +
                ", status='" + status + '\'' +
                ", hrComment='" + hrComment + '\'' +
                '}';
    }
}