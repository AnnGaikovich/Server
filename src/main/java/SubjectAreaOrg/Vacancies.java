package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDate;

public class Vacancies implements Serializable {

    int id;
    String name;
    String description;
    LocalDate date;
    String hrManagerName; // Добавлено для отображения имени HR-менеджера в таблице

    public Vacancies(int id, String name, String discription, LocalDate date) {
        this.id = id;
        this.name = name;
        this.description = discription;
        this.date = date; // Инициализация date
    }

    public Vacancies() {
        this.id = 0;
        this.name = "";
        this.description = "";
        this.date = LocalDate.now();
        this.hrManagerName = "";
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public String getHrManagerName() {
        return hrManagerName;
    }

    public void setHrManagerName(String hrManagerName) {
        this.hrManagerName = hrManagerName;
    }

    @Override
    public String toString() {
        return "Vacancies{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description + // Исправлена опечатка 'discription'
                ", date='" + date + '\'' +
                ", hrManagerName='" + hrManagerName + '\'' +
                '}';
    }
}