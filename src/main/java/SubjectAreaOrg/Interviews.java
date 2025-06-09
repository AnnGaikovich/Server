package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDate;

public class Interviews implements Serializable {

    int id;
    LocalDate date;
    String place;
    int candidateId; // Добавлено поле для ID кандидата
    int vacancyId;   // Добавлено поле для ID вакансии

    public Interviews(int id, LocalDate date, String place) {
        this.id = id;
        this.date = date;
        this.place = place;
        this.candidateId = 0; // Инициализация
        this.vacancyId = 0;   // Инициализация
    }

    // Добавленный конструктор для удобства
    public Interviews(int id, LocalDate date, String place, int candidateId, int vacancyId) {
        this.id = id;
        this.date = date;
        this.place = place;
        this.candidateId = candidateId;
        this.vacancyId = vacancyId;
    }

    public Interviews() {
        this.id = 0;
        this.date = null;
        this.place = null;
        this.candidateId = 0;
        this.vacancyId = 0;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public String getPlace() {return place;}
    public void setPlace(String place) {this.place = place;}

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(int vacancyId) {
        this.vacancyId = vacancyId;
    }

    @Override
    public String toString() {
        return "Interviews{" +
                "id=" + id +
                ", date=" + date +
                ", place='" + place + '\'' +
                ", candidateId=" + candidateId +
                ", vacancyId=" + vacancyId +
                '}';
    }
}