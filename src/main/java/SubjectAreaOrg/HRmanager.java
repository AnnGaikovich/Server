// Example/Server/src/main/java/SubjectAreaOrg/HRmanager.java
package SubjectAreaOrg;

import java.io.Serializable;

public class HRmanager implements Serializable {
    int id;
    String lastlogin;
    String name;
    String surname;
    String mobile;
    String email;
    String login;
    String password;

    // Добавим конструктор по умолчанию, чтобы убедиться, что поля инициализируются
    public HRmanager() {
        this.id = 0;
        this.lastlogin = "";
        this.name = "";
        this.surname = "";
        this.mobile = "";
        this.email = "";
        this.login = "";
        this.password = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return name;
    }

    public void setFirstname(String name) { // <-- ИСПРАВЛЕНО: параметр name
        this.name = name; // <-- ИСПРАВЛЕНО
    }

    public String getLastname() {
        return surname;
    }

    public void setLastname(String surname) { // <-- ИСПРАВЛЕНО: параметр surname
        this.surname = surname; // <-- ИСПРАВЛЕНО
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) { // <-- ИСПРАВЛЕНО: параметр mobile
        this.mobile = mobile; // <-- ИСПРАВЛЕНО
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { // <-- ИСПРАВЛЕНО: параметр email
        this.email = email; // <-- ИСПРАВЛЕНО
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }

    @Override
    public String toString() {
        return "HRmanager{" +
                "id=" + id +
                ", lastlogin='" + lastlogin + '\'' +
                ", firstname='" + name + '\'' +
                ", lastname='" + surname + '\'' +
                ", mobile='" + mobile + '\'' + // Исправлено для ясности
                ", email='" + email + '\'' +   // Исправлено для ясности
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}