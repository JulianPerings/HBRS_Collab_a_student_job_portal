package org.hbrs.se2.project.control.factories;

import org.hbrs.se2.project.dtos.StudentDTO;
import org.hbrs.se2.project.dtos.UserDTO;
import org.hbrs.se2.project.dtos.impl.StudentDTOImpl;
import org.hbrs.se2.project.dtos.impl.UserDTOImpl;
import org.hbrs.se2.project.entities.Student;
import org.hbrs.se2.project.util.Globals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentFactoryTest {
    UserDTO testUser;
    StudentDTO testStudent;

    @BeforeEach
    void setUp() {
        testUser = new UserDTOImpl("JUnitTest", "SicheresPasswort", "testUser@JUnitTest.de", Globals.Roles.student);
        testUser.setUserid(99999);

        testStudent = new StudentDTOImpl(testUser.getUserid(), "Test", "Student", "1234", "HRBS");
    }

    @Test
    @DisplayName("Checking if createStudent-Method works as expected.")
    void createStudent() {
        Student studentFromFactory = StudentFactory.createStudent(testStudent, testUser);
        assertNotNull(studentFromFactory, "createStudent Method should return a Instance of Student and not null.");

        assertEquals(testStudent.getMatrikelnumber(), studentFromFactory.getMatrikelnumber(), "Matrikelnumber should match.");
        assertEquals(testStudent.getFirstname(), studentFromFactory.getFirstname(), "First name should match.");
        assertEquals(testStudent.getLastname(), studentFromFactory.getLastname(), "Last name should match.");
        assertEquals(testStudent.getStudentid(), studentFromFactory.getStudentid(), "StudentID should match.");
        assertEquals(testStudent.getUserid(), studentFromFactory.getUserid(), "UserID should match.");
        assertEquals(testStudent.getUniversity(), studentFromFactory.getUniversity(), "University should match.");
    }
}