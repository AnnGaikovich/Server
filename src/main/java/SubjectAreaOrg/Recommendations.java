package SubjectAreaOrg;

import java.io.Serializable;

public class Recommendations implements Serializable {

    int id;
    String recommendations;
    int candidateId; // Добавлено поле для ID кандидата
    int hrId;        // Добавлено поле для ID HR-менеджера

    public Recommendations(int id, String recommendations) {
        this.id = id;
        this.recommendations = recommendations;
        this.candidateId = 0;
        this.hrId = 0;
    }

    // Добавленный конструктор для удобства
    public Recommendations(int id, String recommendations, int candidateId, int hrId) {
        this.id = id;
        this.recommendations = recommendations;
        this.candidateId = candidateId;
        this.hrId = hrId;
    }

    public Recommendations() {
        this.id = 0;
        this.recommendations = "";
        this.candidateId = 0;
        this.hrId = 0;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getRecommendations() {return recommendations;}
    public void setRecommendations(String recommendations) {this.recommendations = recommendations;}

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getHrId() {
        return hrId;
    }

    public void setHrId(int hrId) {
        this.hrId = hrId;
    }

    @Override
    public String toString() {
        return "Recommendations{" +
                "id=" + id +
                ", recommendations='" + recommendations + '\'' +
                ", candidateId=" + candidateId +
                ", hrId=" + hrId +
                '}';
    }
}