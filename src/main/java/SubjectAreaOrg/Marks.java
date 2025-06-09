package SubjectAreaOrg;

import java.io.Serializable;

public class Marks implements Serializable{

    int id; // Это будет id_mark
    int interviewId; // Добавлено поле для ID интервью
    int mark;
    String comment;

    public Marks(int id, int mark, String comment) {
        this.id = id;
        this.mark = mark;
        this.comment = comment;
        this.interviewId = 0; // Инициализация
    }

    // Добавленный конструктор для удобства
    public Marks(int id, int interviewId, int mark, String comment) {
        this.id = id;
        this.interviewId = interviewId;
        this.mark = mark;
        this.comment = comment;
    }

    public Marks() {
        this.id = 0;
        this.interviewId = 0;
        this.mark = 0;
        this.comment = "";
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public int getMark() {return mark;}
    public void setMark(int mark) {this.mark = mark;}

    public String getComment() {return comment;}
    public void setComment(String comment) {this.comment = comment;}

    @Override
    public String toString() {
        return "Marks{" +
                "id=" + id +
                ", interviewId=" + interviewId +
                ", mark=" + mark +
                ", comment='" + comment + '\'' +
                '}';
    }
}