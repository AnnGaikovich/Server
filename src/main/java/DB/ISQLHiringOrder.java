// Example/Server/src/main/java/DB/ISQLHiringOrder.java
package DB;

import SubjectAreaOrg.HiringOrder;
import java.util.ArrayList;

public interface ISQLHiringOrder {
    boolean insert(HiringOrder obj, int hrManagerId);
    ArrayList<HiringOrder> get(); // Для HR-менеджера (все приказы)
    ArrayList<HiringOrder> getByEmployeeId(int employeeId); // НОВЫЙ МЕТОД для сотрудника
}