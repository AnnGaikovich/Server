// Example/Server/src/main/java/DB/ISQLAuthorization.java
package DB;
import SubjectAreaOrg.Authorization;
import SubjectAreaOrg.Role;

import java.util.ArrayList; // Добавлено

public interface ISQLAuthorization {
    Role getRole(Authorization obj);
    boolean updateUserStatus(int userId, boolean isActive);
    ArrayList<Role> getAllUsersWithRoles(); // НОВЫЙ МЕТОД
}