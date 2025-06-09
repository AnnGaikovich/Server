// Example/Client/src/main/java/SubjectAreaOrg/Role.java
// Example/Server/src/main/java/SubjectAreaOrg/Role.java
package SubjectAreaOrg;

import java.io.Serializable;

public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String role;
    private boolean isActive;
    private String login; // Добавлено новое поле для логина

    public Role() {
        this.id = 0;
        this.role = "";
        this.isActive = true;
        this.login = ""; // Инициализация логина
    }

    public Role(int id, String role) {
        this.id = id;
        this.role = role;
        this.isActive = true;
        this.login = ""; // Инициализация логина
    }

    public Role(int id, String role, boolean isActive) {
        this.id = id;
        this.role = role;
        this.isActive = isActive;
        this.login = ""; // Инициализация логина
    }

    public Role(int id, String role, boolean isActive, String login) { // Новый конструктор с логином
        this.id = id;
        this.role = role;
        this.isActive = isActive;
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // НОВЫЕ МЕТОДЫ: Геттер и сеттер для логина
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", login='" + login + '\'' + // Добавлено для toString
                '}';
    }
}