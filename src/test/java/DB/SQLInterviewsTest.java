package DB;

import SubjectAreaOrg.Interviews;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLInterviewsTest {

    private SQLInterviews sqlInterviews;
    private Connection mockConnection;
    private Statement mockStatement;
    private CallableStatement mockCallableStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Mock JDBC objects
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockCallableStatement = mock(CallableStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Configure mock behavior
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);

        // Set the mock connection in your ConnectionDB for testing
        ConnectionDB.setTestConnection(mockConnection);

        // Get the singleton instance of SQLInterviews
        sqlInterviews = SQLInterviews.getInstance();
    }

    @Test
    void testInsertInterview_success() throws SQLException {
        Interviews interview = new Interviews(1, LocalDate.of(2025, 6, 10), "Zoom", 101, 201);

        // Mock the execute method of CallableStatement to return true for success
        when(mockCallableStatement.execute()).thenReturn(true);
        // Ensure no exceptions when setting parameters
        doNothing().when(mockCallableStatement).setDate(anyInt(), any(Date.class));
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());

        boolean result = sqlInterviews.insert(interview);

        assertTrue(result);
        // Verify that execute was called once
        verify(mockCallableStatement, times(1)).execute();
        // Verify parameters were set correctly
        verify(mockCallableStatement).setDate(1, Date.valueOf(interview.getDate()));
        verify(mockCallableStatement).setString(2, interview.getPlace());
        verify(mockCallableStatement).setInt(3, interview.getCandidateId());
        verify(mockCallableStatement).setInt(4, interview.getVacancyId());
    }

    @Test
    void testInsertInterview_SQLIntegrityConstraintViolationException() throws SQLException {
        Interviews interview = new Interviews(1, LocalDate.of(2025, 6, 10), "Zoom", 101, 201);

        // Simulate an SQLIntegrityConstraintViolationException
        when(mockCallableStatement.execute()).thenThrow(new SQLIntegrityConstraintViolationException("Duplicate entry"));

        boolean result = sqlInterviews.insert(interview);

        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testInsertInterview_GenericSQLException() throws SQLException {
        Interviews interview = new Interviews(1, LocalDate.of(2025, 6, 10), "Zoom", 101, 201);

        // Simulate a generic SQLException
        when(mockCallableStatement.execute()).thenThrow(new SQLException("Database error"));

        boolean result = sqlInterviews.insert(interview);

        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testGetInterviews_success() throws SQLException {
        // Mock ResultSet behavior
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Two rows
        when(mockResultSet.getInt("id_interview"))
                .thenReturn(1).thenReturn(2);
        when(mockResultSet.getDate("date"))
                .thenReturn(Date.valueOf(LocalDate.of(2025, 1, 1))).thenReturn(Date.valueOf(LocalDate.of(2025, 2, 2)));
        when(mockResultSet.getString("place"))
                .thenReturn("Office A").thenReturn("Online B");
        when(mockResultSet.getInt("id_candidate"))
                .thenReturn(101).thenReturn(102);
        when(mockResultSet.getInt("id_job"))
                .thenReturn(201).thenReturn(202);

        ArrayList<Interviews> result = sqlInterviews.get();

        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify first interview
        Interviews interview1 = result.get(0);
        assertEquals(1, interview1.getId());
        assertEquals(LocalDate.of(2025, 1, 1), interview1.getDate());
        assertEquals("Office A", interview1.getPlace());
        assertEquals(101, interview1.getCandidateId());
        assertEquals(201, interview1.getVacancyId());

        // Verify second interview
        Interviews interview2 = result.get(1);
        assertEquals(2, interview2.getId());
        assertEquals(LocalDate.of(2025, 2, 2), interview2.getDate());
        assertEquals("Online B", interview2.getPlace());
        assertEquals(102, interview2.getCandidateId());
        assertEquals(202, interview2.getVacancyId());

        verify(mockStatement, times(1)).executeQuery(anyString());
        verify(mockResultSet, times(3)).next(); // Called 3 times (true, true, false)
    }

    @Test
    void testGetInterviews_emptyResult() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No rows

        ArrayList<Interviews> result = sqlInterviews.get();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockStatement, times(1)).executeQuery(anyString());
        verify(mockResultSet, times(1)).next();
    }

    @Test
    void testGetInterviews_SQLException() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("DB connection lost"));

        ArrayList<Interviews> result = sqlInterviews.get();

        assertNotNull(result);
        assertTrue(result.isEmpty()); // Should return empty list on error
        verify(mockStatement, times(1)).executeQuery(anyString());
    }


    @Test
    void testFindInterviews_success() throws SQLException {
        Interviews searchCriteria = new Interviews();
        searchCriteria.setPlace("Office"); // Searching for place containing "Office"

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One row found
        when(mockResultSet.getInt("id_interview")).thenReturn(3);
        when(mockResultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.of(2025, 3, 10)));
        when(mockResultSet.getString("place")).thenReturn("Main Office");
        when(mockResultSet.getInt("id_candidate")).thenReturn(103);
        when(mockResultSet.getInt("id_job")).thenReturn(203);

        ArrayList<Interviews> result = sqlInterviews.find(searchCriteria);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
        assertEquals("Main Office", result.get(0).getPlace());
        assertEquals(103, result.get(0).getCandidateId());
        assertEquals(203, result.get(0).getVacancyId());

        // Verify that the query contains the LIKE clause with the search term
        verify(mockStatement).executeQuery(argThat(sql -> sql.contains("WHERE i.place LIKE '%Office%'")));
    }

    @Test
    void testFindInterviews_noMatch() throws SQLException {
        Interviews searchCriteria = new Interviews();
        searchCriteria.setPlace("NonExistent");

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        ArrayList<Interviews> result = sqlInterviews.find(searchCriteria);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockStatement).executeQuery(argThat(sql -> sql.contains("WHERE i.place LIKE '%NonExistent%'")));
    }

    @Test
    void testFindInterviews_SQLException() throws SQLException {
        Interviews searchCriteria = new Interviews();
        searchCriteria.setPlace("Error");

        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Find error"));

        ArrayList<Interviews> result = sqlInterviews.find(searchCriteria);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockStatement, times(1)).executeQuery(anyString());
    }

    @Test
    void testInsertInterviewMethod_success() throws SQLException {
        // This tests the `insertInterview` method, which seems to be a duplicate of `insert`.
        // Ensure it behaves correctly if used.
        Interviews interview = new Interviews(4, LocalDate.of(2025, 7, 1), "Video Call", 104, 204);

        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setDate(anyInt(), any(Date.class));
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());

        boolean result = sqlInterviews.insertInterview(interview);

        assertTrue(result);
        verify(mockCallableStatement, times(1)).execute();
        verify(mockCallableStatement).setDate(1, Date.valueOf(interview.getDate()));
        verify(mockCallableStatement).setString(2, interview.getPlace());
        verify(mockCallableStatement).setInt(3, interview.getCandidateId());
        verify(mockCallableStatement).setInt(4, interview.getVacancyId());
    }

    @Test
    void testRegistration_success() throws SQLException {
        Interviews interview = new Interviews();
        interview.setCandidateId(105);
        interview.setVacancyId(205);
        interview.setDate(LocalDate.of(2025, 8, 1));
        interview.setPlace("Conference Room");

        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockCallableStatement).setDate(anyInt(), any(Date.class));
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());

        boolean result = sqlInterviews.registration(interview);

        assertTrue(result);
        verify(mockCallableStatement, times(1)).execute();
        verify(mockCallableStatement).setInt(1, interview.getCandidateId());
        verify(mockCallableStatement).setInt(2, interview.getVacancyId());
        verify(mockCallableStatement).setDate(3, Date.valueOf(interview.getDate()));
        verify(mockCallableStatement).setString(4, interview.getPlace());
    }

    @Test
    void testRegistration_SQLIntegrityConstraintViolationException() throws SQLException {
        Interviews interview = new Interviews();
        interview.setCandidateId(105);
        interview.setVacancyId(205);
        interview.setDate(LocalDate.of(2025, 8, 1));
        interview.setPlace("Conference Room");

        when(mockCallableStatement.execute()).thenThrow(new SQLIntegrityConstraintViolationException("Reg error"));

        boolean result = sqlInterviews.registration(interview);

        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testRegistration_GenericSQLException() throws SQLException {
        Interviews interview = new Interviews();
        interview.setCandidateId(105);
        interview.setVacancyId(205);
        interview.setDate(LocalDate.of(2025, 8, 1));
        interview.setPlace("Conference Room");

        when(mockCallableStatement.execute()).thenThrow(new SQLException("Generic reg error"));

        boolean result = sqlInterviews.registration(interview);

        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testChangeInterview_success() throws SQLException {
        Interviews interview = new Interviews();
        interview.setId(99);
        interview.setDate(LocalDate.of(2025, 10, 10));
        interview.setPlace("New Location");

        when(mockCallableStatement.execute()).thenReturn(true);
        doNothing().when(mockCallableStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockCallableStatement).setDate(anyInt(), any(Date.class));
        doNothing().when(mockCallableStatement).setString(anyInt(), anyString());

        boolean result = sqlInterviews.change(interview);

        assertTrue(result);
        verify(mockCallableStatement, times(1)).execute();
        verify(mockCallableStatement).setInt(1, interview.getId());
        verify(mockCallableStatement).setDate(2, Date.valueOf(interview.getDate()));
        verify(mockCallableStatement).setString(3, interview.getPlace());
    }

    @Test
    void testChangeInterview_SQLIntegrityConstraintViolationException() throws SQLException {
        Interviews interview = new Interviews();
        interview.setId(99);
        interview.setDate(LocalDate.of(2025, 10, 10));
        interview.setPlace("New Location");

        when(mockCallableStatement.execute()).thenThrow(new SQLIntegrityConstraintViolationException("Change error"));

        boolean result = sqlInterviews.change(interview);

        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }

    @Test
    void testChangeInterview_GenericSQLException() throws SQLException {
        Interviews interview = new Interviews();
        interview.setId(99);
        interview.setDate(LocalDate.of(2025, 10, 10));
        interview.setPlace("New Location");

        when(mockCallableStatement.execute()).thenThrow(new SQLException("Generic change error"));

        boolean result = sqlInterviews.change(interview);

        assertFalse(result);
        verify(mockCallableStatement, times(1)).execute();
    }
}