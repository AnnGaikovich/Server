package DB;

import SubjectAreaOrg.Employee;
import SubjectAreaOrg.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SQLEmployeesTest {

    private static SQLEmployees sqlEmployees;

    @BeforeAll
    static void setUp() throws SQLException, ClassNotFoundException {
        sqlEmployees = SQLEmployees.getInstance();
    }

    @Test
    void testGetAllEmployees() {
        ArrayList<Employee> list = sqlEmployees.get();
        assertNotNull(list);
    }

    @Test
    void testInsertAndGetEmployee() {
        Employee e = new Employee();
        e.setName("Test");
        e.setSurname("User");
        e.setMobile("1234567890");
        e.setEmail("testuser@example.com");
        e.setLogin("testuser_login");
        e.setPassword("password123");

        Role r = sqlEmployees.insertEmployee(e);
        assertNotNull(r);
        assertTrue(r.getId() > 0);
        assertEquals("Employee", r.getRole());
    }

    @Test
    void testGetEmployeeById() throws SQLException {
        ArrayList<Employee> employees = sqlEmployees.get();
        if (!employees.isEmpty()) {
            Employee e = sqlEmployees.getEmployeeById(employees.get(0).getId());
            assertNotNull(e);
            assertEquals(employees.get(0).getId(), e.getId());
        } else {
            assertTrue(true);
        }
    }

    @Test
    void testUpdateProfileWithoutPassword() {
        ArrayList<Employee> employees = sqlEmployees.get();
        if (!employees.isEmpty()) {
            Employee e = employees.get(0);
            e.setName("UpdatedName");
            boolean result = sqlEmployees.updateProfile(e, false);
            assertTrue(result);
        } else {
            assertTrue(true);
        }
    }

    @Test
    void testUpdateProfileWithPassword() {
        ArrayList<Employee> employees = sqlEmployees.get();
        if (!employees.isEmpty()) {
            Employee e = employees.get(0);
            e.setPassword("newPassword123");
            boolean result = sqlEmployees.updateProfile(e, true);
            assertTrue(result);
        } else {
            assertTrue(true);
        }
    }
}
