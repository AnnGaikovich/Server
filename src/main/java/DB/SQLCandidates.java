package DB;

import SubjectAreaOrg.Candidates;
import SubjectAreaOrg.Role;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class SQLCandidates implements ISQLCandidates{
    private static SQLCandidates instance;
    private ConnectionDB dbConnection;

    private SQLCandidates() throws SQLException, ClassNotFoundException {
        dbConnection = ConnectionDB.getInstance();
    }

    public static synchronized SQLCandidates getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new SQLCandidates();
        }
        return instance;
    }



    @Override
    public ArrayList<Candidates> findCandidate(Candidates obj) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("SELECT k.login, c.id_candidate, c.name, c.surname, c.mobile, c.birthday, c.email, c.status\n" +
                "FROM Candidates c\n" +
                "JOIN `keys` k ON k.id_keys = c.id_candidate\n" + // Changed JOIN condition to use id_keys
                "WHERE 1=1"); // Base condition, always true

        // Добавляем условия для поиска, только если соответствующие поля заполнены
        if (obj.getLogin() != null && !obj.getLogin().isEmpty()) {
            queryBuilder.append(" AND k.login LIKE '%").append(obj.getLogin()).append("%'");
        }
        if (obj.getName() != null && !obj.getName().isEmpty()) {
            queryBuilder.append(" AND c.name LIKE '%").append(obj.getName()).append("%'");
        }
        if (obj.getSurname() != null && !obj.getSurname().isEmpty()) {
            queryBuilder.append(" AND c.surname LIKE '%").append(obj.getSurname()).append("%'");
        }
        if (obj.getStatus() != null && !obj.getStatus().isEmpty() && !obj.getStatus().equals("Все")) { // Добавил проверку на "Все"
            queryBuilder.append(" AND c.status = '").append(obj.getStatus()).append("'");
        }
        queryBuilder.append(";");

        String str = queryBuilder.toString();
        System.out.println("DEBUG SQL: " + str); // Отладочный вывод запроса

        ArrayList<String[]> result = dbConnection.getArrayResult(str);
        ArrayList<Candidates> candidateList = new ArrayList<>();

        try {
            for (String[] items : result) {
                Candidates candidate = new Candidates();
                candidate.setLogin(items[0]);
                candidate.setId(Integer.parseInt(items[1]));
                candidate.setName(items[2]);
                candidate.setSurname(items[3]);
                candidate.setMobile(items[4]);
                candidate.setBirthday(LocalDate.parse(items[5]));
                candidate.setEmail(items[6]);
                candidate.setStatus(items[7]);
                candidateList.add(candidate);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            System.err.println("Error parsing candidate data in findCandidate: " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in SQLCandidates.findCandidate(): " + e.getMessage());
            return null;
        }

        return candidateList;
    }

    @Override
    public boolean deleteCand(Candidates obj) {
        String proc = "{call delete_candidate(?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getId());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при удалении кандидата: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Candidates> get() throws SQLException {
        String str = "SELECT k.login, c.id_candidate, c.name, c.surname, c.mobile, c.birthday, c.email, c.status " +
                "FROM Candidates c " +
                "JOIN `keys` k ON k.id_keys = c.id_candidate;";

        ArrayList<String[]> result = dbConnection.getArrayResult(str);
        ArrayList<Candidates> candidateList = new ArrayList<>();

        try {
            for (String[] items : result) {
                Candidates candidate = new Candidates();
                candidate.setLogin(items[0]);
                candidate.setId(Integer.parseInt(items[1]));
                candidate.setName(items[2]);
                candidate.setSurname(items[3]);
                candidate.setMobile(items[4]);
                candidate.setBirthday(LocalDate.parse(items[5]));
                candidate.setEmail(items[6]);
                candidate.setStatus(items[7]);
                candidateList.add(candidate);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            System.err.println("Error parsing candidate data in get(): " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in SQLCandidates.get(): " + e.getMessage());
            return null;
        }

        return candidateList;
    }

    @Override
    public Candidates getCandidate(Role r) throws SQLException {
        String str = "SELECT k.login, c.id_candidate, c.name, c.surname, c.mobile, c.birthday, c.email, c.status " +
                "FROM Candidates c " +
                "JOIN `keys` k ON k.id_keys = c.id_candidate " +
                "WHERE k.id_keys = " + r.getId();

        ArrayList<String[]> result = dbConnection.getArrayResult(str);
        Candidates candidate = new Candidates();

        if (!result.isEmpty()) {
            String[] items = result.get(0);
            candidate.setLogin(items[0]);
            candidate.setId(Integer.parseInt(items[1]));
            candidate.setName(items[2]);
            candidate.setSurname(items[3]);
            candidate.setMobile(items[4]);
            candidate.setBirthday(LocalDate.parse(items[5]));
            candidate.setEmail(items[6]);
            candidate.setStatus(items[7]); // Устанавливаем статус
        }

        return candidate;
    }

    @Override
    public Candidates getCandidateById(int candidateId) throws SQLException {
        String str = "SELECT k.login, c.id_candidate, c.name, c.surname, c.mobile, c.birthday, c.email, c.status " +
                "FROM Candidates c " +
                "JOIN `keys` k ON k.id_keys = c.id_candidate " +
                "WHERE c.id_candidate = " + candidateId;

        ArrayList<String[]> result = dbConnection.getArrayResult(str);
        Candidates candidate = null; // Changed to null to indicate no candidate found

        if (!result.isEmpty()) {
            String[] items = result.get(0);
            candidate = new Candidates(); // Initialize only if found
            candidate.setLogin(items[0]);
            candidate.setId(Integer.parseInt(items[1]));
            candidate.setName(items[2]);
            candidate.setSurname(items[3]);
            candidate.setMobile(items[4]);
            candidate.setBirthday(LocalDate.parse(items[5]));
            candidate.setEmail(items[6]);
            candidate.setStatus(items[7]);
        }
        return candidate;
    }

    @Override
    public boolean updateProfile(Candidates obj, boolean updatePassword) {
        String proc = "{call update_candidate_profile(?,?,?,?,?,?,?,?)}"; // New stored procedure for update
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, obj.getId());
            callableStatement.setString(2, obj.getName());
            callableStatement.setString(3, obj.getSurname());
            callableStatement.setString(4, obj.getMobile());
            callableStatement.setDate(5, Date.valueOf(obj.getBirthday()));
            callableStatement.setString(6, obj.getEmail());
            // Пароль обновляется только если updatePassword = true
            callableStatement.setString(7, updatePassword ? obj.getPassword() : null); // Передаем null, если не нужно обновлять
            callableStatement.setBoolean(8, updatePassword); // Флаг для процедуры, указывает, обновлять ли пароль

            callableStatement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при обновлении профиля кандидата: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Candidates> getEmail(Role r) throws SQLException {
        String str = "SELECT k.login, c.name, c.surname, c.email, c.status " +
                "FROM Candidates c " +
                "JOIN `keys` k ON k.id_keys = c.id_candidate " +
                "WHERE k.id_keys = " + r.getId() + ";";

        ArrayList<String[]> result = dbConnection.getArrayResult(str);
        ArrayList<Candidates> candidateList = new ArrayList<>();

        for (String[] items : result) {
            Candidates candidate = new Candidates();
            candidate.setLogin(items[0]);
            candidate.setName(items[1]);
            candidate.setSurname(items[2]);
            candidate.setEmail(items[3]);
            candidate.setStatus(items[4]); // Устанавливаем статус
            candidateList.add(candidate);
        }

        return candidateList;
    }

    @Override
    public Candidates getEmail(Candidates s, Role r) {
        String proc = "{call get_email(?,?,?)}";
        Candidates candidate = new Candidates();

        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, r.getId());
            callableStatement.setString(2, s.getLogin());
            callableStatement.registerOutParameter(3, Types.VARCHAR);

            callableStatement.execute();
            candidate.setEmail(callableStatement.getString(3));
            candidate.setLogin(s.getLogin());

            System.out.println(candidate.toString());

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при получении email: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return candidate;
    }

    @Override
    public void setEmail(Candidates s) {
        String proc = "{call set_email(?,?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, s.getId());
            callableStatement.setString(2, s.getEmail());
            callableStatement.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при установке email: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Candidates getNumber(Role r) {
        String proc = "{call get_number(?,?)}";
        Candidates candidate = new Candidates();

        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, r.getId());
            callableStatement.registerOutParameter(2, Types.VARCHAR);

            callableStatement.execute();
            candidate.setMobile(callableStatement.getString(2));

            System.out.println(candidate.toString());

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при получении номера: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return candidate;
    }

    @Override
    public Role insert(Candidates obj) {
        String proc = "{call insert_candidate(?,?,?,?,?,?,?, ?,?)}";
        Role r = new Role();

        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setString(1, obj.getName());
            callableStatement.setString(2, obj.getSurname());
            callableStatement.setString(3, obj.getMobile());
            callableStatement.setDate(4, Date.valueOf(obj.getBirthday()));
            callableStatement.setString(5, obj.getEmail());
            callableStatement.setString(6, obj.getLogin());
            callableStatement.setString(7, obj.getPassword()); // Исправлено: password
            callableStatement.registerOutParameter(8, Types.INTEGER);
            callableStatement.registerOutParameter(9, Types.VARCHAR);

            callableStatement.execute();
            r.setId(callableStatement.getInt(8));
            r.setRole(callableStatement.getString(9));
            r.setLogin(obj.getLogin());

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных: Пользователь с таким логином уже существует.");
            r.setId(0);
            r.setRole("");
        } catch (Exception e) {
            e.printStackTrace();
            r.setId(0);
            r.setRole("");
        }

        return r;
    }

    @Override
    public boolean updateCandidateStatus(int candidateId, String newStatus) {
        String proc = "{call update_candidate_status(?, ?)}";
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, candidateId);
            callableStatement.setString(2, newStatus);
            callableStatement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при обновлении статуса кандидата: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // NEW METHOD: Submit resume to a vacancy
    public boolean submitResume(int candidateId, int vacancyId, String resumeText, String coverLetter) {
        // This is a placeholder. Ideally, you'd have a 'SubmittedResumes' table and a corresponding SQL class.
        // For the purpose of this project, we'll just "simulate" saving by printing or using a simple insert
        // into a conceptual table.
        // If you have a 'SubmittedResumes' table, you would write an INSERT statement or call a stored procedure here.
        System.out.println("SQLCandidates: Simulating submission of resume for Candidate ID: " + candidateId +
                ", Vacancy ID: " + vacancyId + ". Resume: " + resumeText);

        // Example of how you might insert into a 'SubmittedResumes' table using a stored procedure:
        String proc = "{call insert_submitted_resume(?,?,?,?)}"; // Assuming such a procedure exists
        try (CallableStatement callableStatement = ConnectionDB.dbConnection.prepareCall(proc)) {
            callableStatement.setInt(1, candidateId);
            callableStatement.setInt(2, vacancyId);
            callableStatement.setString(3, resumeText);
            callableStatement.setString(4, coverLetter);
            callableStatement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка целостности данных при отправке резюме: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in submitResume: " + e.getMessage());
            return false;
        }
        // return true; // If no DB interaction is required for this MVP, just return true
    }
}