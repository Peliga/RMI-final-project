import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Enrollment extends DatabaseConnection implements EnrollmentInterface, EnrollmentInitialInterface {
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();

    public Enrollment() {
        super(); 
    }

    public void initializeStudents(int id, String name, String program) throws RemoteException {
        students.add(new Student(id, name, program));
    }

    public void initializeCourses(int id, String name, String description) throws RemoteException {
        courses.add(new Course(id, name, description));
    }

    public String displayStudents() throws RemoteException {
        StringBuilder allStudents = new StringBuilder("\n\nAll Students:\n");
        for (Student student : students) {
            allStudents.append("\nID: ").append(student.getId()).append("\n")
                       .append("Full Name: ").append(student.getFullName()).append("\n")
                       .append("Program: ").append(student.getProgram()).append("\n");
            if (student.getCourseEnrolled().isEmpty()) {
                allStudents.append("Courses Enrolled: No courses enrolled yet.\n---------------");
            } else {
                allStudents.append("Courses Enrolled: ").append(student.getCourseEnrolled()).append("\n---------------");
            }
        }
        System.out.println("A request from unknown client has been processed: Displaying all students in the client...");
        return allStudents.toString();
    }

    public String displayCourses() throws RemoteException {
        StringBuilder allCourses = new StringBuilder("\n\nAll Courses:\n");
        for (Course course : courses) {
            allCourses.append("\nCourse Code: ").append(course.getCCode()).append("\n")
                      .append("Name: ").append(course.getCName()).append("\n")
                      .append("Description: ").append(course.getCDescription()).append("\n---------------");
        }
        System.out.println("A request from unknown client has been processed: Displaying all courses in the client...");
        return allCourses.toString();
    }

    public int enrollCourse(int studentID, int studentCourseCode) throws RemoteException, SQLException {
        boolean haveStudID = false;
        boolean haveCourse = false;
        for (Student student : students) {
            if (studentID == student.getId()) {
                haveStudID = true;
                break;
            }
        }
        for (Course course : courses) {
            if (studentCourseCode == course.getCCode()) {
                haveCourse = true;
                break;
            }
        }
        if (!haveStudID || !haveCourse) {
            System.out.println("A Client attempted to Enroll student. Error found to be an invalid student or course...");
            return 0;
        }
        for (Student student : students) {
            if (studentID == student.getId()) {
                student.setCourse(studentCourseCode);
                break;
            }
        }
        System.out.println("A Client successfully enrolled one student...");
        return 1;
    }

    public void clearArrayList() throws RemoteException {
        students.clear();
        courses.clear();
    }

    public void truncateTable(String tableName) throws RemoteException {
        String query = "TRUNCATE TABLE " + tableName + " CASCADE";
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertEnrolledStudent(int student_id, int course_id) throws RemoteException {
        String query = "INSERT INTO enrolled_students (student_id, course_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, student_id);
            statement.setInt(2, course_id);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void initializeEnrolledStudent(int std_id, int c_id) throws RemoteException {
        for (Student student : students) {
            if (std_id == student.getId()) {
                student.setCourse(c_id);
                break;
            }
        }
    }

    public int addStudent(int std_id, String fname, String program) throws RemoteException {
        for (Student student : students) {
            if (std_id == student.getId()) {
                System.out.println("Error: Unknown client tried to add an already existing student.");
                return 0;
            }
        }
        students.add(new Student(std_id, fname, program));
        System.out.println("Unknown client added a new student");
        return 1;
    }
}
