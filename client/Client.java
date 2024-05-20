import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
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

public class Client{
	private static final String DB_URL = "jdbc:postgresql://localhost:12345/Student";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "admin";
	private static Connection connection;

	public static void main(String[] args){
		try{

			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			// Get the references of exported object from RMI Registry...

			//locate the registry.
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);

			// Get the references of exported object from the RMI Registry...
			EnrollmentInitialInterface p1 = (EnrollmentInitialInterface) registry.lookup("access_enrollment");
			EnrollmentInterface p2 = (EnrollmentInterface) registry.lookup("access_enrollment");

			p1.truncateTable("Students");
			p1.truncateTable("courses");
			p1.truncateTable("enrolled_students");
			p2.clearArrayList();

			File xmlFile = new File("XML-Files/Student.xml");
			File xml = new File("XML-Files/Course.xml");
			File enrolledXML = new File("XML-Files/Enrolled.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document1 = dBuilder.parse(xmlFile);
			document1.getDocumentElement().normalize();
			Document document2 = dBuilder.parse(xml);
			document2.getDocumentElement().normalize();
			Document document3 = dBuilder.parse(enrolledXML);
			document3.getDocumentElement().normalize();
			
			
			// Parse Student XML
			NodeList studentList = document1.getElementsByTagName("student");

			for (int i = 0; i < studentList.getLength(); i++) {
				Element studentElement = (Element) studentList.item(i);

				int id = Integer.parseInt(studentElement.getElementsByTagName("id").item(0).getTextContent());
				String name = studentElement.getElementsByTagName("name").item(0).getTextContent();
				String program = studentElement.getElementsByTagName("program").item(0).getTextContent();
				
				p1.initializeStudents(id,name,program);

				// Insert student data into the database
				p1.insertStudentIntoDatabase(id, name, program);
			}

			// Parse Course XML
			NodeList courseList = document2.getElementsByTagName("course");

			for (int i = 0; i < courseList.getLength(); i++) {
				Element courseElement = (Element) courseList.item(i);

				int courseId = Integer.parseInt(courseElement.getElementsByTagName("course-id").item(0).getTextContent());
				String name = courseElement.getElementsByTagName("title").item(0).getTextContent();
				String description = courseElement.getElementsByTagName("description").item(0).getTextContent();
				 
				p1.initializeCourses(courseId,name,description);
				p1.insertCourseIntoDatabase(courseId, name, description);
			}

			// Parse Enroll XML
			NodeList enrolledList = document3.getElementsByTagName("enrolled");
			
			for (int i = 0; i < enrolledList.getLength(); i++) {
				Element student = (Element) enrolledList.item(i);

				int std_id = Integer.parseInt(student.getElementsByTagName("student_id").item(0).getTextContent());
				int course_id = Integer.parseInt(student.getElementsByTagName("course").item(0).getTextContent());

				p1.insertEnrolledStudent(std_id, course_id);
				p1.initializeEnrolledStudent(std_id,course_id);
							
			}
			
			Scanner scanner = new Scanner(System.in);
			int userSelection = 0;
			// Start Menu
			do{
				System.out.println("\n\n ==== Menu ==== \n");
				System.out.println("Key [1] - Display All Students");
				System.out.println("Key [2] - Display All Courses");
				System.out.println("Key [3] - Enroll Student");
				System.out.println("Key [4] - Add new student");
				System.out.println("Key [0] - Exit");
				System.out.print("Type here: ");
				userSelection = scanner.nextInt();
				if(userSelection == 1){
					System.out.println(p2.displayStudents());
				}else if(userSelection == 2){
					System.out.println(p2.displayCourses());
				}else if(userSelection == 3){
					System.out.print("\n\nEnter Student ID: ");
					int studentID = scanner.nextInt();
					System.out.print("Enter Course Code: ");
					int studentCourseCode = scanner.nextInt();
					int result = 0;
					result = p2.enrollCourse(studentID, studentCourseCode);
					if(result == 0){
						System.out.println("Invalid student or course");
					}else{
						
						System.out.println("Student has been successfully enrolled a course with a code "+studentCourseCode);
						// Need
						p1.insertEnrolledStudent(studentID,studentCourseCode);

						// final
						insertEnrollStudentXML(studentID,studentCourseCode);

					}
				}else if(userSelection == 4){
					System.out.println("Enter Student");
					System.out.print("Student Id: ");
					int id = scanner.nextInt();
					scanner.nextLine();
					System.out.print("Student name:  ");
					String name = scanner.nextLine();
					System.out.print("Student program:  ");
					String program = scanner.nextLine();

					int std = p2.addStudent(id,name,program);
					if(std == 0){
						System.out.println("Student id already exist, please try again");
					}else{

						System.out.println("New Student added");
						p1.insertStudentIntoDatabase(id,name,program);
						insertStudentXML(id,name,program);
						
					}

				}else{
					System.out.println("\n\nInvalid key!");
				}
			}while(userSelection != 0);
			p2.clearArrayList();
			System.out.println("\n\nProgram successfully exited.");
		}catch(Exception e){
			System.out.println("Client side error..." + e);
		}
	}

	private static void  insertEnrollStudentXML(int std_id, int course_id) throws SAXException, IOException, TransformerException{
        File enrollXML = new File("XML-Files/Enrolled.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(enrollXML);
            
            document.getDocumentElement().normalize();

            Node root = document.getDocumentElement();

            Element enroll_student = document.createElement("enrolled");

            String str_id = String.valueOf(std_id);
            Element id = document.createElement("student_id");
            id.appendChild(document.createTextNode(str_id));
            enroll_student.appendChild(id);

            String str_course = String.valueOf(course_id);
            Element enroll_course = document.createElement("course");
            enroll_course.appendChild(document.createTextNode(str_course));
            enroll_student.appendChild(enroll_course);

            root.appendChild(enroll_student);

            removeWhitespaceNodes(document);

            TransformerFactory  transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");


            DOMSource domSource = new DOMSource(document);

            StreamResult streamResult = new StreamResult(enrollXML);

            transformer.transform(domSource,streamResult);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    
    }

	private static void removeWhitespaceNodes(Node node) {
        Node child = node.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();

            if(child.getNodeType() == Node.TEXT_NODE && child.getNodeValue().trim().isEmpty()){
                node.removeChild(child);
            }else if(child.getNodeType() == Node.ELEMENT_NODE){
                removeWhitespaceNodes(child);
            };
            child = next;
        }
    }
	private static void insertStudentXML(int std_id,String fname, String stdPorgram){
        File studentXML = new File("XML-Files/Student.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        
       try{
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document document = builder.parse(studentXML);

            document.getDocumentElement().normalize();

            // my root element in my xml file
            Node root = document.getDocumentElement();

            // this is my new student element created
            Element student = document.createElement("student");

            String str_id = String.valueOf(std_id);

            // create new element id
            Element student_id = document.createElement("id");
            student_id.appendChild(document.createTextNode(str_id));
            student.appendChild(student_id);

            //create element name
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(fname));
            student.appendChild(name);

            // create program element

            Element programELem = document.createElement("program");
            programELem.appendChild(document.createTextNode(stdPorgram));
            student.appendChild(programELem);

            // append new student NOde to Root NOde
            root.appendChild(student);
            removeWhitespaceNodes(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2"); //xalan

            DOMSource domSource = new DOMSource(document);

            StreamResult stream = new StreamResult(studentXML);

            transformer.transform(domSource,stream);
        
       }catch(ParserConfigurationException | SAXException | IOException | TransformerException e){
        e.printStackTrace();
       }
    }
	
	

}