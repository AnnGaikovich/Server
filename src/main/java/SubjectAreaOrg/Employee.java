package SubjectAreaOrg;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Serializable {

    int id;
    String name;
    String surname;
    String mobile;
    String email;
    private String login;
    private String password; // Though password won't be sent back to client

    public Employee() {
        this.id = 0;
        this.name = "";
        this.surname = "";
        this.mobile = "";
        this.email = "";
        this.login = "";
        this.password = "";
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getMobile() {return mobile;}
    public void setMobile(String mobile) {this.mobile = mobile;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getLogin() {return login;}
    public void setLogin(String login) {this.login = login;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(login, employee.login) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(surname, employee.surname) &&
                Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, name, surname, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}