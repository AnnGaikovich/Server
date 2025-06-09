// Example/Server/src/main/java/ServerWork/Worker.java
package ServerWork;

import DB.SQLFactory;
import SubjectAreaOrg.*;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.AbstractMap;
import java.util.ArrayList;

public class Worker implements Runnable {
    protected Socket clientSocket = null;
    ObjectInputStream sois;
    ObjectOutputStream soos;

    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            soos = new ObjectOutputStream(clientSocket.getOutputStream()); // Инициализируем soos первым
            sois = new ObjectInputStream(clientSocket.getInputStream());   // Затем sois

            System.out.println("Получение команды от клиента...");
            String choice = sois.readObject().toString();
            System.out.println(choice);
            System.out.println("Команда получена");
            switch (choice) {
                case "adminInf" -> {
                    System.out.println("Запрос к БД на получение информации об администраторе: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Admin> infList = sqlFactory.getAdmin().get();
                    System.out.println(infList != null ? infList.toString() : "Список администраторов null");
                    soos.writeObject("OK");
                    soos.flush();
                    soos.writeObject(infList);
                    soos.flush();
                }
                case "registrationHRmanager" -> {
                    System.out.println("Запрос к БД на проверку пользователя(таблица HRmanagers), клиент: " + clientSocket.getInetAddress().toString());
                    HRmanager hRmanager = (HRmanager) sois.readObject();
                    System.out.println(hRmanager.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getHRManager().insert(hRmanager)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Incorrect Data");
                        soos.flush();
                    }
                }
                case "registrationCandidate" -> {
                    System.out.println("Запрос к БД на регистрацию кандидата, клиент: " + clientSocket.getInetAddress().toString());
                    Candidates candidate = (Candidates) sois.readObject();
                    System.out.println(candidate.toString());

                    SQLFactory sqlFactory = new SQLFactory();
                    Role r = sqlFactory.getCandidates().insert(candidate);

                    if (r.getId() != 0 && r.getRole() != null && !r.getRole().isEmpty()) {
                        soos.writeObject("OK");
                        soos.flush();
                        soos.writeObject(r);
                        soos.flush();
                    } else {
                        soos.writeObject("This user is already existed");
                        soos.flush();
                    }
                }

                case "employeeInf" -> {
                    System.out.println("Запрос к БД на получение списка сотрудников: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Employee> employeeList = sqlFactory.getEmployees().get();
                    System.out.println(employeeList != null ? employeeList.toString() : "Список сотрудников null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(employeeList);
                    soos.flush();
                }
                case "teacherInf" -> {
                    System.out.println("Запрос к БД на проверку HR-менеджера (таблица HRmanagers), клиент: " + clientSocket.getInetAddress().toString());
                    Role r = (Role) sois.readObject();
                    System.out.println(r.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    ArrayList<HRmanager> hrManager = sqlFactory.getHRManager().getHRmanager(r);
                    System.out.println(hrManager != null ? hrManager.toString() : "Список HR-менеджеров null");
                    soos.writeObject("OK");
                    soos.flush();
                    soos.writeObject(hrManager);
                    soos.flush();
                }
                case "addVacancy" -> {
                    System.out.println("Выполняется добавление вакансии...");
                    Vacancies vacancy = (Vacancies) sois.readObject();
                    System.out.println(vacancy.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getVacancies().insertVacancy(vacancy)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи вакансии");
                        soos.flush();
                    }
                }
                case "addMarks" -> {
                    System.out.println("Выполняется добавление оценки и комментария...");
                    Marks marks = (Marks) sois.readObject();
                    System.out.println(marks.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getMarks().insertMark(marks)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи оценки");
                        soos.flush();
                    }
                }
                case "addInterviews" -> {
                    System.out.println("Выполняется добавление собеседования...");
                    Interviews interviews = (Interviews) sois.readObject();
                    System.out.println(interviews.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getInterviews().insertInterview(interviews)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи собеседования");
                        soos.flush();
                    }
                }
                case "deleteCand" -> {
                    System.out.println("Выполняется удаление студента...");
                    Candidates cn = (Candidates) sois.readObject();
                    System.out.println(cn.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getCandidates().deleteCand(cn)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при удалении студента");
                        soos.flush();
                    }
                }
                case "changeHRmanager" -> {
                    System.out.println("Запрос к БД на изменение пользователя(таблица HRmanagers), клиент: " + clientSocket.getInetAddress().toString());
                    HRmanager hRmanager = (HRmanager) sois.readObject();
                    System.out.println(hRmanager.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getHRManager().changeHRmanager(hRmanager)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Incorrect Data");
                        soos.flush();
                    }
                }
                case "changeInterviews" -> {
                    System.out.println("Выполняется изменение собеседования...");
                    Interviews interviews = (Interviews) sois.readObject();
                    System.out.println(interviews.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getInterviews().change(interviews)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при изменении собеседования");
                        soos.flush();
                    }
                }
                case "changeVacancies" -> {
                    System.out.println("Выполняется изменение вакансии...");
                    Vacancies vacancies = (Vacancies) sois.readObject();
                    System.out.println(vacancies.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getVacancies().change(vacancies)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при изменении вакансии");
                        soos.flush();
                    }
                }
                case "showInterviews" -> {
                    System.out.println("Запрос к БД на получение списка собеседований: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Interviews> interviewList = sqlFactory.getInterviews().get();
                    System.out.println(interviewList != null ? interviewList.toString() : "Список собеседований null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(interviewList);
                    soos.flush();
                }
                case "addHiringOrder" -> {
                    System.out.println("Выполняется добавление приказа о найме...");
                    SubjectAreaOrg.HiringOrder hiringOrder = (SubjectAreaOrg.HiringOrder) sois.readObject();
                    System.out.println(hiringOrder.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    int hrManagerId = (int) sois.readObject();

                    SubjectAreaOrg.HiringOrder orderToInsert = new SubjectAreaOrg.HiringOrder(
                            0,
                            hiringOrder.getEmployeeId(),
                            hiringOrder.getOrderDate(),
                            hiringOrder.getPosition(),
                            hiringOrder.getSalary(),
                            hiringOrder.getProbationPeriodMonths(),
                            ""
                    );

                    if (sqlFactory.getHiringOrders().insert(orderToInsert, hrManagerId)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи приказа о найме");
                        soos.flush();
                    }
                }
                case "showJobPositions" -> {
                    System.out.println("Запрос к БД на получение списка должностей: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<SubjectAreaOrg.JobPosition> positionList = sqlFactory.getJobPositions().get();
                    System.out.println(positionList != null ? positionList.toString() : "Список должностей null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(positionList);
                    soos.flush();
                }
                case "showHiringOrders" -> {
                    System.out.println("Запрос к БД на получение списка приказов о найме: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<SubjectAreaOrg.HiringOrder> orderList = sqlFactory.getHiringOrders().get();
                    System.out.println(orderList != null ? orderList.toString() : "Список приказов о найме null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(orderList);
                    soos.flush();
                }
                case "getAllHRManagers" -> {
                    System.out.println("Запрос к БД на получение списка всех HR-менеджеров: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<HRmanager> hrManagerList = sqlFactory.getHRManager().getAll();
                    System.out.println(hrManagerList != null ? hrManagerList.toString() : "Список HR-менеджеров null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(hrManagerList);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getAllHRManagers.");
                }
                case "showMarks" -> {
                    System.out.println("Запрос к БД на получение списка оценок и комментариев от HR-менеджера: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Marks> marksList = sqlFactory.getMarks().get();
                    System.out.println(marksList != null ? marksList.toString() : "Список оценок null");
                    soos.writeObject("OK");
                    soos.flush();
                    soos.writeObject(marksList);
                    soos.flush();
                }
                case "showRecommendations" -> {
                    System.out.println("Запрос к БД на просмотр рекомендаций: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Recommendations> recommendationsList = sqlFactory.getRecommendations().get();
                    System.out.println(recommendationsList != null ? recommendationsList.toString() : "Список рекомендаций null");
                    soos.writeObject("OK");
                    soos.flush();
                    soos.writeObject(recommendationsList);
                    soos.flush();
                }
                case "showVacancies" -> {
                    System.out.println("Запрос к БД на получение доступных вакансий: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Vacancies> vacancyList = sqlFactory.getVacancies().get();
                    System.out.println(vacancyList != null ? vacancyList.toString() : "Список вакансий null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(vacancyList);
                    soos.flush();
                }
                case "findCandidates" -> {
                    System.out.println("Запрос к БД на поиск кандидатов: " + clientSocket.getInetAddress().toString());
                    Candidates candi = (Candidates) sois.readObject();
                    System.out.println(candi.toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Candidates> candidates = sqlFactory.getCandidates().findCandidate(candi);

                    // ИСПРАВЛЕНИЕ НАЧИНАЕТСЯ ЗДЕСЬ
                    if (candidates == null) {
                        // Если findCandidate вернул null (из-за ошибки или не найдено), отправляем пустой список.
                        // Это предотвращает EOFException на клиенте, так как клиент всегда получает объект.
                        candidates = new ArrayList<>();
                        System.out.println("Server: Отправляю пустой список кандидатов, так как findCandidate вернул null.");
                    } else {
                        System.out.println(candidates.toString());
                    }
                    soos.writeObject("LIST_SENT_OK"); // Убедитесь, что статус соответствует ожиданию клиента
                    soos.flush();
                    soos.writeObject(candidates);
                    soos.flush();
                    // ИСПРАВЛЕНИЕ ЗАКАНЧИВАЕТСЯ ЗДЕСЬ
                }
                case "findInterviews" -> {
                    System.out.println("Запрос к БД на поиск собеседования: " + clientSocket.getInetAddress().toString());
                    Interviews in = (Interviews) sois.readObject();
                    System.out.println(in.toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Interviews> interviewList = sqlFactory.getInterviews().find(in);
                    System.out.println(interviewList != null ? interviewList.toString() : "Список найденных собеседований null");
                    soos.writeObject("OK");
                    soos.flush();
                    soos.writeObject(interviewList);
                    soos.flush();
                }
                case "findVacancies" -> {
                    System.out.println("Запрос к БД на поиск вакансий: " + clientSocket.getInetAddress().toString());
                    Vacancies v = (Vacancies) sois.readObject();
                    System.out.println(v.toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Vacancies> groupsList = sqlFactory.getVacancies().find(v);
                    System.out.println(groupsList != null ? groupsList.toString() : "Список найденных вакансий null");
                    soos.writeObject("OK");
                    soos.flush();
                    soos.writeObject(groupsList);
                    soos.flush();
                }
                case "authorization" -> {
                    System.out.println("Выполняется авторизация пользователя....");
                    Authorization auth = (Authorization) sois.readObject();
                    System.out.println(auth.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    Role r = sqlFactory.getRole().getRole(auth);
                    System.out.println(r.toString());

                    if (r.getId() != 0 && r.getRole() != null && !r.getRole().isEmpty()) {
                        if (r.isActive()) {
                            soos.writeObject("OK");
                            soos.flush();
                            soos.writeObject(r);
                            soos.flush();
                        } else {
                            soos.writeObject("AccountBlocked");
                            soos.flush();
                        }
                    } else {
                        soos.writeObject("There is no data!");
                        soos.flush();
                    }
                }
                case "updateUserStatus" -> {
                    System.out.println("Запрос к БД на изменение статуса пользователя: " + clientSocket.getInetAddress().toString());
                    int userId = (int) sois.readObject();
                    boolean isActive = (boolean) sois.readObject();

                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getRole().updateUserStatus(userId, isActive)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Error updating user status");
                        soos.flush();
                    }
                }
                case "regInterviews" -> {
                    System.out.println("Выполняется регистрация пользователя на собеседование...");
                    Interviews interview = (Interviews) sois.readObject();
                    System.out.println(interview.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getInterviews().registration(interview)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи на курс");
                        soos.flush();
                    }
                }
                case "addVacation" -> {
                    System.out.println("Выполняется добавление отпуска...");
                    Vacation vacation = (Vacation) sois.readObject();
                    System.out.println(vacation.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getVacationSchedule().insertVacation(vacation)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи отпуска");
                        soos.flush();
                    }
                }
                case "registrationEmployee" -> {
                    System.out.println("Запрос к БД на регистрацию сотрудника, клиент: " + clientSocket.getInetAddress().toString());
                    Employee employee = (Employee) sois.readObject();
                    System.out.println(employee.toString());

                    SQLFactory sqlFactory = new SQLFactory();
                    Role r = sqlFactory.getEmployees().insertEmployee(employee);

                    if (r.getId() != 0 && r.getRole() != null && !r.getRole().isEmpty()) {
                        soos.writeObject("OK");
                        soos.flush();
                        soos.writeObject(r);
                        soos.flush();
                    } else {
                        soos.writeObject("This user is already existed");
                        soos.flush();
                    }
                }
                case "showVacations" -> {
                    System.out.println("Запрос к БД на получение списка отпусков: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Vacation> vacationList = sqlFactory.getVacationSchedule().get();
                    System.out.println(vacationList != null ? vacationList.toString() : "Список отпусков null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(vacationList);
                    soos.flush();
                }
                case "candidateInf" -> {
                    System.out.println("Запрос к БД на получение информации о кандидатах: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Candidates> candidateList = sqlFactory.getCandidates().get();
                    System.out.println(candidateList != null ? candidateList.toString() : "Список кандидатов null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(candidateList);
                    soos.flush();
                }
                case "findCandidate" -> { // Это дублирующий случай, который можно удалить или объединить с верхним 'findCandidates'
                    System.out.println("Запрос к БД на поиск кандидата: " + clientSocket.getInetAddress().toString());
                    Candidates cn = (Candidates) sois.readObject();
                    System.out.println(cn.toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Candidates> candidateList = sqlFactory.getCandidates().findCandidate(cn);
                    System.out.println(candidateList != null ? candidateList.toString() : "Список найденных кандидатов null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(candidateList);
                    soos.flush();
                }
                case "updateCandidateStatus" -> {
                    System.out.println("Запрос к БД на обновление статуса кандидата: " + clientSocket.getInetAddress().toString());
                    int candidateId = (int) sois.readObject();
                    String newStatus = (String) sois.readObject();
                    System.out.println("Updating candidate " + candidateId + " to status: " + newStatus);

                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getCandidates().updateCandidateStatus(candidateId, newStatus)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при обновлении статуса кандидата");
                        soos.flush();
                    }
                }
                case "getAllUsersWithRoles" -> {
                    System.out.println("Запрос к БД на получение списка всех пользователей с ролями: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Role> allUsers = sqlFactory.getRole().getAllUsersWithRoles();
                    System.out.println("Server: Sending list of users: " + (allUsers != null ? allUsers.size() : "null") + " users.");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(allUsers);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getAllUsersWithRoles.");
                }
                case "addRecommendations" -> {
                    System.out.println("Выполняется добавление рекомендации...");
                    Recommendations recommendations = (Recommendations) sois.readObject();
                    System.out.println(recommendations.toString());

                    SQLFactory sqlFactory = new SQLFactory();

                    if (sqlFactory.getRecommendations().insertRecommendation(recommendations)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при записи рекомендации");
                        soos.flush();
                    }
                }
                case "sendResumeToVacancy" -> {
                    System.out.println("Выполняется отправка резюме на вакансию...");
                    SubjectAreaOrg.SubmittedResume sr = (SubjectAreaOrg.SubmittedResume) sois.readObject();

                    System.out.println("Candidate ID: " + sr.getCandidateId());
                    System.out.println("Employee ID: " + sr.getEmployeeId()); // Добавлено для отладки
                    System.out.println("Vacancy ID: " + sr.getVacancyId());
                    System.out.println("Resume: " + sr.getResumeText());
                    System.out.println("Cover Letter: " + sr.getCoverLetter());

                    SQLFactory sqlFactory = new SQLFactory();

                    try {
                        if (sqlFactory.getSubmittedResumes().insert(sr)) {
                            System.out.println("SQLSubmittedResumes: Резюме успешно вставлено. Отправляю 'OK' клиенту.");
                            soos.writeObject("OK");
                            soos.flush();
                            System.out.println("Server: 'OK' отправлен клиенту.");
                        } else {
                            System.out.println("SQLSubmittedResumes: Ошибка при вставке резюме. Отправляю ошибку клиенту.");
                            soos.writeObject("Ошибка при отправке резюме");
                            soos.flush();
                            System.out.println("Server: 'Ошибка при отправке резюме' отправлена клиенту.");
                        }
                    } catch (SQLException e) {
                        System.err.println("Server: SQL Error during sendResumeToVacancy: " + e.getMessage());
                        e.printStackTrace();
                        soos.writeObject("SQL_ERROR: " + e.getMessage());
                        soos.flush();
                    } catch (Exception e) {
                        System.err.println("Server: Unexpected Error during sendResumeToVacancy: " + e.getMessage());
                        e.printStackTrace();
                        soos.writeObject("UNEXPECTED_ERROR: " + e.getMessage());
                        soos.flush();
                    }
                }
                case "getSubmittedResumes" -> {
                    System.out.println("Запрос к БД на получение списка отправленных резюме для соискателя: " + clientSocket.getInetAddress().toString());
                    int candidateId = (int) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<SubjectAreaOrg.SubmittedResume> submittedResumes = sqlFactory.getSubmittedResumes().getByCandidateId(candidateId);
                    System.out.println(submittedResumes != null ? submittedResumes.toString() : "Список отправленных резюме null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(submittedResumes);
                    soos.flush();
                }
                case "getCandidateById" -> {
                    System.out.println("Запрос к БД на получение профиля кандидата по ID: " + clientSocket.getInetAddress().toString());
                    int candidateId = (int) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    SubjectAreaOrg.Candidates candidate = sqlFactory.getCandidates().getCandidateById(candidateId);

                    if (candidate != null) {
                        soos.writeObject("OK");
                        soos.flush();
                        soos.writeObject(candidate);
                        soos.flush();
                    } else {
                        soos.writeObject("NOT_FOUND");
                        soos.flush();
                    }
                }
                case "getEmployeeById" -> {
                    System.out.println("Запрос к БД на получение профиля сотрудника по ID: " + clientSocket.getInetAddress().toString());
                    int employeeId = (int) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    SubjectAreaOrg.Employee employee = sqlFactory.getEmployees().getEmployeeById(employeeId);
                    System.out.println(employee != null ? employee.toString() : "Сотрудник не найден");
                    if (employee != null) {
                        soos.writeObject("OK");
                        soos.flush();
                        soos.writeObject(employee);
                        soos.flush();
                    } else {
                        soos.writeObject("NOT_FOUND");
                        soos.flush();
                    }
                }

                case "updateEmployeeProfile" -> {
                    System.out.println("Выполняется обновление профиля сотрудника...");
                    SubjectAreaOrg.Employee employee = (SubjectAreaOrg.Employee) sois.readObject();
                    boolean updatePassword = (boolean) sois.readObject();
                    System.out.println(employee.toString());

                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getEmployees().updateProfile(employee, updatePassword)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при обновлении профиля сотрудника");
                        soos.flush();
                    }
                }
                case "getHiringOrdersForEmployee" -> { // НОВЫЙ CASE
                    System.out.println("Запрос к БД на получение приказов о найме для сотрудника: " + clientSocket.getInetAddress().toString());
                    int employeeId = (int) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<SubjectAreaOrg.HiringOrder> orderList = sqlFactory.getHiringOrders().getByEmployeeId(employeeId);
                    System.out.println(orderList != null ? orderList.toString() : "Список приказов о найме для сотрудника null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(orderList);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getHiringOrdersForEmployee.");
                }

                case "getAllSubmittedResumes" -> { // НОВЫЙ CASE для HR-менеджера
                    System.out.println("Запрос к БД на получение списка ВСЕХ отправленных резюме: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<SubjectAreaOrg.SubmittedResume> submittedResumes = sqlFactory.getSubmittedResumes().get(); // Используем get() без параметров
                    System.out.println(submittedResumes != null ? submittedResumes.toString() : "Список всех отправленных резюме null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(submittedResumes);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getAllSubmittedResumes.");
                }
                case "getAllTestResults" -> { // НОВЫЙ CASE
                    System.out.println("Запрос к БД на получение списка ВСЕХ результатов тестов: " + clientSocket.getInetAddress().toString());
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<TestResult> testResults = sqlFactory.getTestResults().get();
                    System.out.println(testResults != null ? testResults.toString() : "Список всех результатов тестов null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(testResults);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getAllTestResults.");
                }
                case "updateTestResultStatusAndComment" -> { // НОВЫЙ CASE
                    System.out.println("Запрос к БД на обновление статуса и комментария результата теста: " + clientSocket.getInetAddress().toString());
                    int testResultId = (int) sois.readObject();
                    String newStatus = (String) sois.readObject();
                    String hrComment = (String) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getTestResults().updateTestResultStatusAndComment(testResultId, newStatus, hrComment)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при обновлении статуса и комментария результата теста");
                        soos.flush();
                    }
                }
                case "updateSubmittedResumeStatusAndFeedback" -> { // НОВЫЙ CASE для HR-менеджера
                    System.out.println("Запрос к БД на обновление статуса и отзыва резюме: " + clientSocket.getInetAddress().toString());
                    int resumeId = (int) sois.readObject();
                    String newStatus = (String) sois.readObject();
                    String hrFeedback = (String) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getSubmittedResumes().updateStatusAndFeedback(resumeId, newStatus, hrFeedback)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при обновлении статуса и отзыва резюме");
                        soos.flush();
                    }
                }
                case "getSubmittedResumesByEmployeeId" -> { // НОВЫЙ CASE
                    System.out.println("Запрос к БД на получение списка отправленных резюме для сотрудника: " + clientSocket.getInetAddress().toString());
                    int employeeId = (int) sois.readObject();

                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<SubjectAreaOrg.SubmittedResume> submittedResumes = sqlFactory.getSubmittedResumes().getByEmployeeId(employeeId);
                    System.out.println(submittedResumes != null ? submittedResumes.toString() : "Список отправленных резюме для сотрудника null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(submittedResumes);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getSubmittedResumesByEmployeeId.");
                }
                case "getVacationScheduleByEmployeeId" -> { // НОВЫЙ CASE
                    System.out.println("Запрос к БД на получение графика отпусков для сотрудника по ID: " + clientSocket.getInetAddress().toString());
                    int employeeId = (int) sois.readObject();
                    SQLFactory sqlFactory = new SQLFactory();
                    ArrayList<Vacation> vacationList = sqlFactory.getVacationSchedule().getByEmployeeId(employeeId);
                    System.out.println(vacationList != null ? vacationList.toString() : "Список отпусков для сотрудника null");
                    soos.writeObject("LIST_SENT_OK");
                    soos.flush();
                    soos.writeObject(vacationList);
                    soos.flush();
                    System.out.println("Server: Sent LIST_SENT_OK for getVacationScheduleByEmployeeId.");
                }
                case "updateCandidateProfile" -> {
                    System.out.println("Выполняется обновление профиля кандидата...");
                    SubjectAreaOrg.Candidates candidate = (SubjectAreaOrg.Candidates) sois.readObject();
                    boolean updatePassword = (boolean) sois.readObject();
                    System.out.println(candidate.toString());

                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getCandidates().updateProfile(candidate, updatePassword)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при обновлении профиля кандидата");
                        soos.flush();
                    }
                }
                case "submitTestResult" -> {
                    System.out.println("Выполняется отправка результата теста...");
                    SubjectAreaOrg.TestResult testResult = (SubjectAreaOrg.TestResult) sois.readObject();
                    System.out.println(testResult.toString());

                    SQLFactory sqlFactory = new SQLFactory();
                    if (sqlFactory.getTestResults().insertTestResult(testResult)) {
                        soos.writeObject("OK");
                        soos.flush();
                    } else {
                        soos.writeObject("Ошибка при отправке результата теста");
                        soos.flush();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException | DateTimeParseException e) {
            System.out.println("Client disconnected or server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                // Эти потоки должны быть закрыты после обработки ОДНОГО запроса.
                // ВАЖНО: Закрываем в обратном порядке от создания, чтобы flush() гарантированно отработал.
                if (soos != null) soos.close();
                if (sois != null) sois.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}