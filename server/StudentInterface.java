import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface StudentInterface extends Remote{
	// Lets us define API
	public int getId() throws RemoteException;
	public String getFullName() throws RemoteException;
	public String getProgram() throws RemoteException;
	public  ArrayList<Integer> getCourseEnrolled() throws RemoteException;
	public void setCourse(int courseCode) throws RemoteException;
}