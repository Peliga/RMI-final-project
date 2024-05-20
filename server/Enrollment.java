import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.crypto.dsig.Transform;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Enrollment implements EnrollmentInterface, EnrollmentInitialInterface{
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();
    private static final String DB_URL = "jdbc:postgresql://localhost:12345/Student";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";
    // Initialize database connection
    private Connection connection;

    public Enrollment(){
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
   
    public void initializeStudents(int id,String name,String program)throws RemoteException{
        students.add(new Student(id,name,program));
    }
    public void initializeCourses(int id, String name,String description)throws RemoteException{
        courses.add(new Course(id,name,description));
    }


    public String displayStudents() throws RemoteException {
        String allStudents = "\n\nAll Students:\n";
        for (int i = 0; i < students.size(); i++) {
            if (students.size() != 0) {
                allStudents += "\nID: " + students.get(i).getId() + "\n";
                allStudents += "Full Name: " + students.get(i).getFullName() + "\n";
                allStudents += "Program: " + students.get(i).getProgram() + "\n";
                if (students.get(i).getCourseEnrolled().isEmpty()) {
                    allStudents += "Courses Enrolled: No courses enrolled yet.\n---------------";
                } else {
                    allStudents += "Courses Enrolled: " + students.get(i).getCourseEnrolled() + "\n---------------";
                }
            }
        }
        System.out.println("A request from unknown client has been processed: Displaying all students in the client...");
        return allStudents;
    }

    // Display All courses
    public String displayCourses() throws RemoteException {
        String allCourses = "\n\nAll Courses:\n";
        for (int i = 0; i < courses.size(); i++) {
            if (courses.size() != 0) {
                allCourses += "\nCourse Code: " + courses.get(i).getCCode() + "\n";
                allCourses += "Name: " + courses.get(i).getCName() + "\n";
                allCourses += "Description: " + courses.get(i).getCDescription() + "\n---------------";
            }
        }
        System.out.println("A request from unknown client has been processed: Displaying all courses in the client...");
        return allCourses;
    }

    // Enroll Student Course
    public int enrollCourse(int studentID, int studentCourseCode) throws  RemoteException, SQLException {
        int haveStudID = 0;
        int haveCourse = 0;
        for (int i = 0; i < students.size(); i++) {
            if (studentID == students.get(i).getId()){
                haveStudID = 1;
            }
        }
        for (int i = 0; i < courses.size(); i++) {
            if (studentCourseCode==courses.get(i).getCCode()) {
                haveCourse = 1;
            }
        }
        if (haveStudID == 0 || haveCourse == 0) {
            System.out.println("A Client attempted to Enroll student. Error found to be an invalid student or course...");
            return 0;
        }
        for (int i = 0; i < students.size(); i++) {
            if (studentID == students.get(i).getId()) {

                students.get(i).setCourse(studentCourseCode);
            }
        }
        System.out.println("A Client successfully enrolled one student...");
        return 1;
    }

    // Insert Enrolled Student  to the Enrolled XML FILE

    // Remove Spaces in XML FILE
    public void clearArrayList() throws RemoteException{
        students.clear();
        courses.clear();
    }

    public void insertStudentIntoDatabase(int id, String name, String program) throws RemoteException {
		String query = "INSERT INTO students (id, name, program) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, id);
			statement.setString(2, name);
			statement.setString(3, program);
			statement.executeUpdate();
		}catch(SQLException e){
            e.printStackTrace();
        }
	}

	// Insert Course to the Database course table
	public void insertCourseIntoDatabase(int courseId, String name, String description) throws RemoteException {
		String query = "INSERT INTO courses (id, name, description) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, courseId);
			statement.setString(2, name);
			statement.setString(3, description);
			statement.executeUpdate();
		}catch(SQLException e){
            e.printStackTrace();
        }
	}

	// Truncate Database table 
	public void truncateTable(String tableName) throws RemoteException {
		String query = "TRUNCATE TABLE " + tableName + " CASCADE";
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(query);
		}catch(SQLException e){
            e.printStackTrace();
        }
	}
	public void insertEnrolledStudent(int student_id,int course_id)throws RemoteException{
		String query = "INSERT INTO enrolled_students (student_id, course_id) VALUES (?, ?)";
				try (PreparedStatement statement = connection.prepareStatement(query)) {
					statement.setInt(1, student_id);
					statement.setInt(2, course_id);
					statement.executeUpdate();
				}catch (Exception e) {
					System.out.println("Error" + e);
				}
	}
    public void initializeEnrolledStudent(int std_id,int c_id) throws RemoteException{
        for (int j = 0; j < students.size(); j++) {
            if (std_id == students.get(j).getId()) {

                students.get(j).setCourse(c_id);
                break; 
            }
        }
    }
    public int addStudent(int std_id,String fname,String program)throws RemoteException{    
        int hasId = 0;
        for(int i =0;i< students.size();i++){
            if(std_id == students.get(i).getId()){
                    hasId ++;
            }
        }

        if(hasId > 0){
            System.out.println("Error Unknown client try to add already exist student ");
            return 0;
        }else{
                students.add(new Student(std_id,fname,program));

                insertStudentIntoDatabase(std_id, fname, program);
            }
            System.out.println("Unknown client added new student");
            return 1;
    }

    
}
