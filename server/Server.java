import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{
	public static void main(String[] args){
		try{
			Enrollment serve = new Enrollment();

			System.setProperty("java.rmi.server.hostname", "127.0.0.1");
			// You don't have to run in console > start rmiregistry 9100
			Registry startRMI = LocateRegistry.createRegistry(9100);
			System.out.println("Server has been started...");

			EnrollmentInterface enrollment = (EnrollmentInterface) UnicastRemoteObject.exportObject(serve, 0);

			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);

			registry.rebind("access_enrollment", enrollment);
			System.out.println("Exporting and binding of Objects has been completed...\nServer is now successfully running...");
		}catch(Exception e){
			System.out.println("Error while exporting and binding objects..." + e);
		}
	}
}
