package DB;

import SubjectAreaOrg.HiringOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SQLHiringOrderTest {

    private static SQLHiringOrder sqlHiringOrder;

    @BeforeAll
    static void setUp() throws SQLException, ClassNotFoundException {
        sqlHiringOrder = SQLHiringOrder.getInstance();
    }

    @Test
    void testInsertHiringOrder() {
        HiringOrder order = new HiringOrder();
        order.setEmployeeId(1); // предполагаем, что сотрудник с таким ID есть
        order.setOrderDate(LocalDate.now());
        order.setPosition("Junior Developer");
        order.setSalary(1200.00);
        order.setProbationPeriodMonths(3);

        boolean result = sqlHiringOrder.insert(order, 1); // предполагаем, что HR с ID 1 существует
        assertTrue(result);
    }

    @Test
    void testGetAllHiringOrders() {
        ArrayList<HiringOrder> list = sqlHiringOrder.get();
        assertNotNull(list);
    }

    @Test
    void testGetHiringOrdersByEmployeeId() {
        ArrayList<HiringOrder> allOrders = sqlHiringOrder.get();
        if (!allOrders.isEmpty()) {
            int employeeId = allOrders.get(0).getEmployeeId();
            ArrayList<HiringOrder> ordersById = sqlHiringOrder.getByEmployeeId(employeeId);
            assertNotNull(ordersById);
            assertFalse(ordersById.isEmpty());
            assertEquals(employeeId, ordersById.get(0).getEmployeeId());
        } else {
            assertTrue(true);
        }
    }
}
