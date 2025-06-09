package DB;

import SubjectAreaOrg.Interviews;
import SubjectAreaOrg.Role;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SQLInterviews implements ISQLInterviews {

    private static SQLInterviews instance;
    private ConnectionDB dbConnection;

    private SQLInterviews() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLInterviews getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLInterviews();
        }
        return instance;
    }
    @Override
    public boolean insert(Interviews obj) {
        String proc = "{call insert_interview(?,?,?,?)}"; // Добавлено 2 параметра
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setDate(1, Date.valueOf(obj.getDate()));
            callableStatement.setString(2, obj.getPlace());
            callableStatement.setInt(3, obj.getCandidateId()); // ID кандидата
            callableStatement.setInt(4, obj.getVacancyId()); // ID вакансии
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке интервью: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Interviews> get() {
        // Измените запрос, чтобы явно выбирать id_candidate и id_job
        String query = "SELECT i.id_interview, i.date, i.place, i.id_candidate, i.id_job, c.name, c.surname, v.name AS vacancy_name " + // <-- ИЗМЕНЕНО: добавлены i.id_candidate, i.id_job
                "FROM Interviews i " +
                "JOIN Candidates c ON c.id_candidate = i.id_candidate "
                + "JOIN Vacancies v ON v.id_job = i.id_job;"; // Добавил JOIN для получения имен кандидата и вакансии

        ArrayList<Interviews> interviewList = new ArrayList<>();
        try (
                Statement statement = ConnectionDB.dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Interviews interview = new Interviews();
                interview.setId(resultSet.getInt("id_interview"));
                interview.setDate(resultSet.getDate("date").toLocalDate());
                interview.setPlace(resultSet.getString("place"));
                interview.setCandidateId(resultSet.getInt("id_candidate")); // <-- ДОБАВЛЕНО
                interview.setVacancyId(resultSet.getInt("id_job"));       // <-- ДОБАВЛЕНО
                // ... (Если нужны имена кандидата и вакансии в объекте Interviews,
                // добавьте соответствующие поля в SubjectAreaOrg.Interviews.java и присвойте их здесь.)
                interviewList.add(interview);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interviewList;
    }

    @Override
    public ArrayList<Interviews> find(Interviews c) {
        // Аналогично измените и здесь, если хотите видеть ID при поиске
        String query = "SELECT i.id_interview, i.date, i.place, i.id_candidate, i.id_job, c.name, c.surname, v.name AS vacancy_name " + // <-- ИЗМЕНЕНО: добавлены i.id_candidate, i.id_job
                "FROM Interviews i " +
                "JOIN Candidates c ON c.id_candidate = i.id_candidate " +
                "JOIN Vacancies v ON v.id_job = i.id_job " +
                "WHERE i.place LIKE '%" + c.getPlace() + "%';"; // Changed to LIKE for partial search

        ArrayList<Interviews> interviewList = new ArrayList<>();
        try (
                Statement statement = ConnectionDB.dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Interviews interview = new Interviews();
                interview.setId(resultSet.getInt("id_interview"));
                interview.setDate(resultSet.getDate("date").toLocalDate());
                interview.setPlace(resultSet.getString("place"));
                interview.setCandidateId(resultSet.getInt("id_candidate")); // <-- ДОБАВЛЕНО
                interview.setVacancyId(resultSet.getInt("id_job"));       // <-- ДОБАВЛЕНО
                interviewList.add(interview);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interviewList;
    }

    @Override
    public boolean insertInterview(Interviews obj) {
        String proc = "{call insert_interview(?,?,?,?)}"; // Теперь ожидаем 4 параметра
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setDate(1, Date.valueOf(obj.getDate()));
            callableStatement.setString(2, obj.getPlace());
            callableStatement.setInt(3, obj.getCandidateId()); // ID кандидата
            callableStatement.setInt(4, obj.getVacancyId());   // ID вакансии
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при вставке интервью: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean registration(Interviews obj) {
        String proc = "{call register_interview(?,?,?,?)}"; // Эта процедура в SQL ожидает 4 параметра
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getCandidateId()); // ID кандидата
            callableStatement.setInt(2, obj.getVacancyId());   // ID вакансии
            callableStatement.setDate(3, Date.valueOf(obj.getDate()));
            callableStatement.setString(4, obj.getPlace());
            // Последний параметр `p_unique_code` из SQL-процедуры не используется в объекте Interviews.
            // Если он нужен, его нужно добавить в Interviews.java.
            // Здесь я его не передаю, так как он не соответствует полям obj.
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при регистрации интервью: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean change(Interviews obj) {
        String proc = "{call change_interview(?,?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getId());
            callableStatement.setDate(2, Date.valueOf(obj.getDate()));
            callableStatement.setString(3, obj.getPlace());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при изменении интервью: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}