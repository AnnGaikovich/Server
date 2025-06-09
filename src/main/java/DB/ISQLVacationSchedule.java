// Example/Server/src/main/java/DB/ISQLVacationSchedule.java
package DB;

import SubjectAreaOrg.Vacation;
import java.util.ArrayList;

public interface ISQLVacationSchedule {
    boolean insertVacation(Vacation obj);
    ArrayList<Vacation> get(); // Для HR-менеджера (все отпуска)
    ArrayList<Vacation> getByEmployeeId(int employeeId); // НОВЫЙ МЕТОД для сотрудника
}