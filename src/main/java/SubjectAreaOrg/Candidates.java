// Example/Client/src/main/java/SubjectAreaOrg/Candidates.java
// Example/Server/src/main/java/SubjectAreaOrg/Candidates.java
package SubjectAreaOrg;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Candidates implements Serializable{

    int id;
    String name;
    String surname;
    String mobile;
    LocalDate birthday;
    String email;
    private String login;
    private String password;
    private String status; // НОВОЕ ПОЛЕ: статус кандидата (например, "Активен", "Принят", "Отклонен")


    public Candidates() { // Добавляем конструктор по умолчанию, если его нет или обновим его
        this.id = 0;
        this.name = "";
        this.surname = "";
        this.mobile = "";
        this.birthday = null;
        this.email = "";
        this.login = "";
        this.password = "";
        this.status = "Активен"; // Значение по умолчанию
    }

    public Candidates(int id, String name, String surname, String mobile, LocalDate birthday, String email, String login, String password, String status) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.mobile = mobile;
        this.birthday = birthday;
        this.email = email;
        this.login = login;
        this.password = password;
        this.status = status;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getMobile() {return mobile;}
    public void setMobile(String mobile) {this.mobile = mobile;}

    public LocalDate getBirthday() {return birthday;}
    public void setBirthday(LocalDate birthday) {this.birthday = birthday;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getLogin() {return login;}
    public void setLogin(String login) {this.login = login;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getStatus() {return status;} // НОВЫЙ ГЕТТЕР
    public void setStatus(String status) {this.status = status;} // НОВЫЙ СЕТТЕР

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Candidates that = (Candidates) o;

        return  Objects.equals(this.login, that.login) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.surname, that.surname) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.status, that.status); // Включаем статус в equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.login, this.password, this.name, this.surname, this.email, this.status); // Включаем статус в hashCode
    }

    @Override
    public String toString() {
        return "Сandidates{" +
                "id=" + id +
                ", name=" + name +
                ", surname=" + surname +
                ", mobile='" + mobile + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' + // Добавляем статус в toString
                '}';
    }
}