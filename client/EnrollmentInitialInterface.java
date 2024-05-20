import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EnrollmentInitialInterface extends Remote{
    void initializeStudents(int id,String name,String program) throws RemoteException;
    void initializeCourses(int id, String name,String description) throws RemoteException;
    void initializeEnrolledStudent(int std_id,int c_id) throws RemoteException;
    void insertStudentIntoDatabase(int id, String name, String program) throws RemoteException ;
    void insertCourseIntoDatabase(int courseId, String name, String description) throws RemoteException;
    void truncateTable(String tableName) throws RemoteException ;
    void insertEnrolledStudent(int student_id,int course_id)throws RemoteException;
}
