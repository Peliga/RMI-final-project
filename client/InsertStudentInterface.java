import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InsertStudentInterface extends Remote {
    void insertCourseIntoDatabase(int courseId, String name, String description) throws RemoteException;
    void insertStudentIntoDatabase(int id, String name, String program) throws RemoteException;
}
