package DB;

import SubjectAreaOrg.Vacancies;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet; // Добавлено
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class SQLVacancies implements ISQLVacancies {

    private static SQLVacancies instance;
    private ConnectionDB dbConnection;

    private SQLVacancies() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLVacancies getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLVacancies();
        }
        return instance;
    }

    @Override
    public boolean insert(Vacancies obj) {
        String proc = "{call insert_vacancy(?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getName());
            callableStatement.setString(2, obj.getDescription());
            callableStatement.setDate(3, Date.valueOf(obj.getDate()));
            callableStatement.setInt(4, obj.getId()); // ID HR-менеджера, который добавляет вакансию
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке вакансии: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Vacancies> get() {
        String query = "SELECT v.id_job, v.name, v.description, v.date, h.name, h.surname " +
                "FROM Vacancies v " +
                "LEFT JOIN HR_Managers h ON h.id_HR = v.id_HR;"; // Changed to LEFT JOIN for cases where HR might be null

        ArrayList<Vacancies> vacancyList = new ArrayList<>();
        try (
                Statement statement = ConnectionDB.dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Vacancies vacancy = new Vacancies();
                vacancy.setId(resultSet.getInt("id_job"));
                vacancy.setName(resultSet.getString("name"));
                vacancy.setDescription(resultSet.getString("description"));
                vacancy.setDate(resultSet.getDate("date").toLocalDate());
                String hrName = resultSet.getString("h.name"); // Имя HR
                String hrSurname = resultSet.getString("h.surname"); // Фамилия HR
                if (hrName != null && hrSurname != null) {
                    vacancy.setHrManagerName(hrName + " " + hrSurname);
                } else {
                    vacancy.setHrManagerName("Не назначен");
                }
                vacancyList.add(vacancy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacancyList;
    }


    @Override
    public ArrayList<Vacancies> find(Vacancies c) {
        String query = "SELECT v.id_job, v.name, v.description, v.date, h.name, h.surname " +
                "FROM Vacancies v " +
                "LEFT JOIN HR_Managers h ON h.id_HR = v.id_HR " +
                "WHERE v.name LIKE '%" + c.getName() + "%';"; // Changed to LIKE for partial search

        ArrayList<Vacancies> vacancyList = new ArrayList<>();
        try (
                Statement statement = ConnectionDB.dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Vacancies vacancy = new Vacancies();
                vacancy.setId(resultSet.getInt("id_job"));
                vacancy.setName(resultSet.getString("name"));
                vacancy.setDescription(resultSet.getString("description"));
                vacancy.setDate(resultSet.getDate("date").toLocalDate());
                String hrName = resultSet.getString("h.name");
                String hrSurname = resultSet.getString("h.surname");
                if (hrName != null && hrSurname != null) {
                    vacancy.setHrManagerName(hrName + " " + hrSurname);
                } else {
                    vacancy.setHrManagerName("Не назначен");
                }
                vacancyList.add(vacancy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacancyList;
    }


    @Override
    public boolean insertVacancy(Vacancies obj) {
        String proc = "{call insert_vacancy(?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getName());
            callableStatement.setString(2, obj.getDescription());
            callableStatement.setDate(3, Date.valueOf(obj.getDate()));
            callableStatement.setInt(4, obj.getId()); // ID HR-менеджера
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке вакансии: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean registration(Vacancies obj) {
        // Эта процедура "register_vacancy" в SQL была создана как insert_vacancy.
        // Если это регистрация кандидата на вакансию, то нужно изменить логику.
        // Предполагаем, что это просто добавление вакансии, но с другим вызовом в worker.
        String proc = "{call insert_vacancy(?,?,?,?)}"; // Используем insert_vacancy процедуру, так как register_vacancy в SQL была такой же
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getName());
            callableStatement.setString(2, obj.getDescription());
            callableStatement.setDate(3, Date.valueOf(obj.getDate()));
            callableStatement.setInt(4, obj.getId()); // ID HR-менеджера
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при регистрации вакансии: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean change(Vacancies obj) {
        String proc = "{call change_vacancy(?,?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getId());
            callableStatement.setString(2, obj.getName());
            callableStatement.setString(3, obj.getDescription());
            callableStatement.setDate(4, Date.valueOf(obj.getDate()));
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при изменении вакансии: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}