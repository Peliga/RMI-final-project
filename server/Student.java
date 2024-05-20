import java.rmi.RemoteException;
import java.util.ArrayList;

public class Student implements StudentInterface{
	// Attributes of product
	private int id;
	private String fullName;
	private String program;
	ArrayList<Integer> course = new ArrayList<>();

	Student(int id, String fullName, String program) throws RemoteException{
		this.id = id;
		this.fullName = fullName;
		this.program = program;
	}
	public int getId() throws RemoteException{
		return this.id;
	}
	public String getFullName() throws RemoteException{
		return this.fullName;
	}
	public String getProgram() throws RemoteException{
		return this.program;
	}
	public ArrayList<Integer> getCourseEnrolled() throws RemoteException{
		return this.course;
	}
	public void setCourse(int courseCode) throws RemoteException{
		this.course.add(courseCode);
	}

}