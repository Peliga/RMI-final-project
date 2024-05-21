import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertStudent extends DatabaseConnection implements InsertStudentInterface {

    @Override
    public void insertStudentIntoDatabase(int id, String name, String program) throws RemoteException {
        String query = "INSERT INTO students (id, name, program) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, program);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error inserting student into database", e);
        }
    }

    @Override
    public void insertCourseIntoDatabase(int courseId, String name, String description) throws RemoteException {
        String query = "INSERT INTO courses (id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, courseId);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error inserting course into database", e);
        }
    }
}
