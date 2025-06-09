package DB;

import SubjectAreaOrg.HRmanager;
import SubjectAreaOrg.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLHRmanagerTest {

    private SQLHRmanager sqlHrManager;
    private Connection mockConnection;
    private Statement mockStatement;
    private CallableStatement mockCallableStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Создаем mock объекты для JDBC
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockCallableStatement = mock(CallableStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Настраиваем поведение mockConnection
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);

        // Устанавливаем mockConnection в статическое поле ConnectionDB.dbConnection
        // Это позволяет SQLHRmanager использовать наш мок вместо реального соединения.
        ConnectionDB.setTestConnection(mockConnection);

        // Получаем экземпляр SQLHRmanager. Он будет использовать ConnectionDB с нашим mockConnection.
        sqlHrManager = SQLHRmanager.getInstance();
    }

    // --- Тесты для insert ---
    @Test
    void testInsert_Success() throws SQLException {
        // Arrange
        HRmanager hrManager = new HRmanager();
        hrManager.setFirstname("Test");
        hrManager.setLastname("User");
        hrManager.setMobile("1234567890");
        hrManager.setEmail("test@example.com");
        hrManager.setLogin("testuser");
        hrManager.setPassword("password");

        // Убедимся, что execute() вызывается и не выбрасывает исключений
        when(mockCallableStatement.execute()).thenReturn(true);
        // doNothing() для void методов-сеттеров CallableStatement
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());

        // Act
        boolean result = sqlHrManager.insert(hrManager);

        // Assert
        assertTrue(result);
        // Проверяем, что метод execute был вызван
        verify(mockCallableStatement, times(1)).execute();
        // Проверяем, что параметры были установлены
        verify(mockCallableStatement, times(1)).setString(1, "Test");
        verify(mockCallableStatement, times(1)).setString(6, "password");
    }

    @Test
    void testInsert_SQLIntegrityConstraintViolationException() throws SQLException {
        // Arrange
        HRmanager hrManager = new HRmanager();
        hrManager.setLogin("duplicate_login");

        // Имитируем ошибку целостности данных
        when(mockCallableStatement.execute()).thenThrow(new SQLIntegrityConstraintViolationException("Duplicate entry for key 'login_UNIQUE'"));

        // Act
        boolean result = sqlHrManager.insert(hrManager);

        // Assert
        assertFalse(result); // Ожидаем false при ошибке
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testInsert_OtherSQLException() throws SQLException {
        // Arrange
        HRmanager hrManager = new HRmanager();
        hrManager.setLogin("someuser");

        // Имитируем другую SQLException
        when(mockCallableStatement.execute()).thenThrow(new SQLException("Generic DB Error"));

        // Act
        boolean result = sqlHrManager.insert(hrManager);

        // Assert
        assertFalse(result); // Ожидаем false при ошибке
        verify(mockCallableStatement, times(1)).execute();
    }

    // --- Тесты для changeHRmanager ---
    @Test
    void testChangeHRmanager_Success() throws SQLException {
        // Arrange
        HRmanager hrManager = new HRmanager();
        hrManager.setLastlogin("2023-01-01 12:00:00");
        hrManager.setFirstname("NewName");
        hrManager.setLastname("NewSurname");
        hrManager.setMobile("0987654321");
        hrManager.setLogin("newlogin");
        hrManager.setPassword("newpass");

        // Убедимся, что execute() вызывается без исключений
        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());

        // Act
        boolean result = sqlHrManager.changeHRmanager(hrManager);

        // Assert
        assertTrue(result);
        verify(mockCallableStatement, times(1)).execute();
        verify(mockCallableStatement, times(1)).setString(1, "2023-01-01 12:00:00"); // lastlogin
        verify(mockCallableStatement, times(1)).setString(6, "newpass"); // password
    }

    @Test
    void testChangeHRmanager_SQLException() throws SQLException {
        // Arrange
        HRmanager hrManager = new HRmanager();

        // Имитируем SQLException
        when(mockCallableStatement.execute()).thenThrow(new SQLException("Update failed"));

        // Act
        boolean result = sqlHrManager.changeHRmanager(hrManager);

        // Assert
        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    // --- Тесты для getHRmanager ---
    @Test
    void testGetHRmanager_found() throws SQLException {
        // Arrange
        Role r = new Role();
        r.setId(1);

        // Мокируем ResultSetMetaData, так как getArrayResult его использует
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(5); // 5 колонок в запросе getHRmanager

        // Мокируем поведение ResultSet для getArrayResult
        // ConnectionDB.getArrayResult вызывает Statement.executeQuery, который возвращает ResultSet.
        // Затем getArrayResult читает этот ResultSet.
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next())
                .thenReturn(true) // Первая строка
                .thenReturn(false); // Больше нет строк

        when(mockResultSet.getString(1)).thenReturn("hrlogin"); // login
        when(mockResultSet.getString(2)).thenReturn("HRName"); // name
        when(mockResultSet.getString(3)).thenReturn("HRSurname"); // surname
        when(mockResultSet.getString(4)).thenReturn("12345"); // mobile
        when(mockResultSet.getString(5)).thenReturn("hr@example.com"); // email

        // Act
        ArrayList<HRmanager> result = sqlHrManager.getHRmanager(r);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        HRmanager foundHr = result.get(0);
        assertEquals("hrlogin", foundHr.getLogin());
        assertEquals("HRName", foundHr.getFirstname());
        assertEquals("HRSurname", foundHr.getLastname());
        assertEquals("12345", foundHr.getMobile());
        assertEquals("hr@example.com", foundHr.getEmail());

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetHRmanager_notFound() throws SQLException {
        // Arrange
        Role r = new Role();
        r.setId(99);

        // Мокируем ResultSetMetaData (даже если нет данных)
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(5);

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Нет результатов

        // Act
        ArrayList<HRmanager> result = sqlHrManager.getHRmanager(r);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetHRmanager_SQLException() throws SQLException {
        // Arrange
        Role r = new Role();
        r.setId(1);

        // Имитируем SQLException при выполнении запроса
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Query failed for getHRmanager"));

        // Act
        ArrayList<HRmanager> result = sqlHrManager.getHRmanager(r);

        // Assert
        assertNull(result); // Ожидаем null при ошибке
        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    // --- Тесты для getIdByHRmanager ---
    @Test
    void testGetIdByHRmanager_Success() throws SQLException {
        // Arrange
        Role obj = new Role();
        obj.setId(10); // Входное ID для поиска

        // Настраиваем CallableStatement для обработки OUT параметра
        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockCallableStatement).registerOutParameter(eq(2), eq(Types.INTEGER));
        when(mockCallableStatement.getInt(2)).thenReturn(50); // Возвращаемое ID

        // Act
        Role resultRole = sqlHrManager.getIdByHRmanager(obj);

        // Assert
        assertNotNull(resultRole);
        assertEquals(50, resultRole.getId()); // Проверяем, что ID обновилось
        verify(mockCallableStatement, times(1)).setInt(1, 10);
        verify(mockCallableStatement, times(1)).execute();
        verify(mockCallableStatement, times(1)).registerOutParameter(2, Types.INTEGER);
        verify(mockCallableStatement, times(1)).getInt(2);
    }

    @Test
    void testGetIdByHRmanager_SQLException() throws SQLException {
        // Arrange
        Role obj = new Role();
        obj.setId(10);

        // Имитируем SQLException при выполнении
        when(mockCallableStatement.execute()).thenThrow(new SQLException("Procedure error for getIdByHRmanager"));

        // Act
        Role resultRole = sqlHrManager.getIdByHRmanager(obj);

        // Assert
        assertNotNull(resultRole);
        assertEquals(10, resultRole.getId()); // ID не должно измениться при ошибке
        verify(mockCallableStatement, times(1)).execute();
    }

    // --- Тесты для getAll ---
    @Test
    void testGetAll_Success() throws SQLException {
        // Arrange
        // Мокируем ResultSetMetaData (хотя ConnectionDB.getArrayResult может его не использовать)
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(6); // id_HR, name, surname, mobile, email, login

        // Мокируем поведение ResultSet.next() и getString/getInt
        when(mockResultSet.next())
                .thenReturn(true) // Первая запись
                .thenReturn(true) // Вторая запись
                .thenReturn(false); // Конец записей

        when(mockResultSet.getInt("id_HR")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("name")).thenReturn("Alice").thenReturn("Bob");
        when(mockResultSet.getString("surname")).thenReturn("Smith").thenReturn("Johnson");
        when(mockResultSet.getString("mobile")).thenReturn("111-222").thenReturn("333-444");
        when(mockResultSet.getString("email")).thenReturn("alice@test.com").thenReturn("bob@test.com");
        when(mockResultSet.getString("login")).thenReturn("alice_login").thenReturn("bob_login");

        // Act
        ArrayList<HRmanager> result = sqlHrManager.getAll();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        HRmanager hr1 = result.get(0);
        assertEquals(1, hr1.getId());
        assertEquals("Alice", hr1.getFirstname());
        assertEquals("Smith", hr1.getLastname());
        assertEquals("111-222", hr1.getMobile());
        assertEquals("alice@test.com", hr1.getEmail());
        assertEquals("alice_login", hr1.getLogin());

        HRmanager hr2 = result.get(1);
        assertEquals(2, hr2.getId());
        assertEquals("Bob", hr2.getFirstname());
        assertEquals("Johnson", hr2.getLastname());
        assertEquals("333-444", hr2.getMobile());
        assertEquals("bob@test.com", hr2.getEmail());
        assertEquals("bob_login", hr2.getLogin());

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetAll_NoRecords() throws SQLException {
        // Arrange
        // Мокируем ResultSetMetaData (даже если нет данных)
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(6);

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Нет записей

        // Act
        ArrayList<HRmanager> result = sqlHrManager.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetAll_SQLException() throws SQLException {
        // Arrange
        // Имитируем SQLException при выполнении запроса
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Query failed for getAll"));

        // Act
        ArrayList<HRmanager> result = sqlHrManager.getAll();

        // Assert
        assertNull(result); // Ожидаем null при ошибке
        verify(mockStatement, times(1)).executeQuery(anyString());
    }
}