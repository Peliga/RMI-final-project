import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CourseInterface extends Remote{
	// Lets us define API
	public int getCCode() throws RemoteException;
	public String getCName() throws RemoteException;
	public String getCDescription() throws RemoteException;
}