package DB;

import SubjectAreaOrg.Authorization;
import SubjectAreaOrg.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLAuthorizationTest {

    private SQLAuthorization sqlAuthorization;
    private Connection mockConnection;
    private CallableStatement mockCallableStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private Statement mockStatement;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        mockConnection = mock(Connection.class);
        mockCallableStatement = mock(CallableStatement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockStatement = mock(Statement.class);

        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet); // Добавлено: настройка для PreparedStatement
        when(mockCallableStatement.executeQuery()).thenReturn(mockResultSet); // Добавлено: если callableStatement возвращает ResultSet

        ConnectionDB.setTestConnection(mockConnection);
        sqlAuthorization = SQLAuthorization.getInstance();
    }

    @Test
    void testGetRole_successActiveUser() throws SQLException {
        // Arrange
        Authorization auth = new Authorization();
        auth.setLogin("userlogin");
        auth.setPassword("hashedpassword");

        // Настраиваем CallableStatement для find_login
        when(mockCallableStatement.execute()).thenReturn(true);
        when(mockCallableStatement.getInt(3)).thenReturn(1); // user ID
        when(mockCallableStatement.getString(4)).thenReturn("Candidate"); // role name
        when(mockCallableStatement.getResultSet()).thenReturn(mockResultSet); // Если CallableStatement возвращает ResultSet напрямую

        // Настраиваем ResultSet для проверки is_active
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Один результат
        when(mockResultSet.getBoolean("is_active")).thenReturn(true);

        // Act
        Role role = sqlAuthorization.getRole(auth);

        // Assert
        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("Candidate", role.getRole());
        assertTrue(role.isActive());
        assertEquals("userlogin", role.getLogin());

        verify(mockCallableStatement, times(1)).setString(1, "userlogin");
        verify(mockCallableStatement, times(1)).setString(2, "hashedpassword");
        verify(mockCallableStatement, times(1)).execute();
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetRole_blockedUser() throws SQLException {
        // Arrange
        Authorization auth = new Authorization();
        auth.setLogin("blockeduser");
        auth.setPassword("hashedpassword");

        // Настраиваем CallableStatement для find_login
        when(mockCallableStatement.execute()).thenReturn(true);
        when(mockCallableStatement.getInt(3)).thenReturn(2); // user ID
        when(mockCallableStatement.getString(4)).thenReturn("HRmanager"); // role name
        when(mockCallableStatement.getResultSet()).thenReturn(mockResultSet);

        // Настраиваем ResultSet для проверки is_active (пользователь заблокирован)
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getBoolean("is_active")).thenReturn(false);

        // Act
        Role role = sqlAuthorization.getRole(auth);

        // Assert
        assertNotNull(role);
        assertEquals(2, role.getId());
        assertEquals("HRmanager", role.getRole());
        assertFalse(role.isActive()); // Ожидаем, что пользователь неактивен
        assertEquals("blockeduser", role.getLogin());

        verify(mockCallableStatement, times(1)).execute();
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetRole_noData() throws SQLException {
        // Arrange
        Authorization auth = new Authorization();
        auth.setLogin("nonexistent");
        auth.setPassword("wrongpass");

        // Настраиваем CallableStatement для find_login (не находит пользователя)
        when(mockCallableStatement.execute()).thenReturn(true);
        when(mockCallableStatement.getInt(3)).thenReturn(0); // ID 0 указывает, что пользователь не найден
        when(mockCallableStatement.getString(4)).thenReturn(null); // Role null
        when(mockCallableStatement.getResultSet()).thenReturn(mockResultSet);


        // Act
        Role role = sqlAuthorization.getRole(auth);

        // Assert
        assertNotNull(role); // Объект Role должен быть создан, но с пустыми/дефолтными значениями
        assertEquals(0, role.getId());
        assertNull(role.getRole()); // Роль должна быть null или пустой
        assertFalse(role.isActive()); // По умолчанию неактивен, если не найден
        assertTrue(role.getLogin().isEmpty()); // Логин должен быть пустым

        verify(mockCallableStatement, times(1)).execute();
        verify(mockPreparedStatement, never()).executeQuery(); // PreparedStatement не должен вызываться, если id=0
    }

    @Test
    void testUpdateUserStatus_success() throws SQLException {
        // Arrange
        int userId = 1;
        boolean isActive = false; // Блокировка

        // Настраиваем CallableStatement
        when(mockCallableStatement.execute()).thenReturn(true);

        // Act
        boolean result = sqlAuthorization.updateUserStatus(userId, isActive);

        // Assert
        assertTrue(result);
        verify(mockCallableStatement, times(1)).setInt(1, userId);
        verify(mockCallableStatement, times(1)).setBoolean(2, isActive);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testUpdateUserStatus_failure() throws SQLException {
        // Arrange
        int userId = 1;
        boolean isActive = true;

        // Имитируем ошибку
        when(mockCallableStatement.execute()).thenThrow(new SQLException("Update error"));

        // Act
        boolean result = sqlAuthorization.updateUserStatus(userId, isActive);

        // Assert
        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testGetAllUsersWithRoles_success() throws SQLException {
        // Arrange
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(4); // id_keys, login, role, is_active

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Настраиваем ResultSet для двух пользователей
        when(mockResultSet.next())
                .thenReturn(true) // Первый пользователь
                .thenReturn(true) // Второй пользователь
                .thenReturn(false); // Больше нет пользователей

        // Настраиваем getString для каждого вызова next()
        when(mockResultSet.getString(1))
                .thenReturn("1") // id_keys для user1
                .thenReturn("2"); // id_keys для user2
        when(mockResultSet.getString(2))
                .thenReturn("admin") // login для user1
                .thenReturn("user"); // login для user2
        when(mockResultSet.getString(3))
                .thenReturn("Admin") // role для user1
                .thenReturn("Candidate"); // role для user2
        when(mockResultSet.getString(4))
                .thenReturn("1") // is_active для user1 (как строка для getArrayResult)
                .thenReturn("0"); // is_active для user2 (как строка)

        // Act
        ArrayList<Role> users = sqlAuthorization.getAllUsersWithRoles();

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());

        Role user1 = users.get(0);
        assertEquals(1, user1.getId());
        assertEquals("admin", user1.getLogin());
        assertEquals("Admin", user1.getRole());
        assertTrue(user1.isActive());

        Role user2 = users.get(1);
        assertEquals(2, user2.getId());
        assertEquals("user", user2.getLogin());
        assertEquals("Candidate", user2.getRole());
        assertFalse(user2.isActive());

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetAllUsersWithRoles_empty() throws SQLException {
        // Arrange
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Нет пользователей

        // Act
        ArrayList<Role> users = sqlAuthorization.getAllUsersWithRoles();

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetAllUsersWithRoles_sqlException() throws SQLException {
        // Arrange
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("DB error"));

        // Act
        ArrayList<Role> users = sqlAuthorization.getAllUsersWithRoles();

        // Assert
        assertNull(users); // Возвращаем null при ошибке
        verify(mockStatement, times(1)).executeQuery(anyString());
    }
}