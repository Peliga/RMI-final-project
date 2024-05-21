import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface EnrollmentInterface extends Remote{
	String displayStudents() throws RemoteException;
    String displayCourses() throws RemoteException;
    int enrollCourse(int studentID, int studentCourseCode) throws RemoteException, SQLException;
	void clearArrayList() throws RemoteException;
    int addStudent(int std_id,String fname,String program)throws RemoteException;
}