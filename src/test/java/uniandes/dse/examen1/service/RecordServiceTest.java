package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.StudentService;
import uniandes.dse.examen1.services.RecordService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    private PodamFactory factory = new PodamFactoryImpl();

    private String login;
    private String courseCode;

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();

        StudentEntity newStudent = factory.manufacturePojo(StudentEntity.class);
        newStudent = studentService.createStudent(newStudent);
        login = newStudent.getLogin();
    }

    /**
     * Tests the normal creation of a record for a student in a course
          * @throws InvalidRecordException 
          */
    @Test
    void testCreateRecord() throws InvalidRecordException {
        RecordEntity record = recordService.createRecord(login, courseCode, 4.0, "1");

        assertEquals(courseRepository.findByCourseCode(courseCode).get(), record.getCourse(), "El curso en el record no coincide con el curso esperado");
        assertEquals(studentRepository.findByLogin(login).get(), record.getStudent(), "El estudiante del record no es el esperado");
        assertEquals(4.0, record.getFinalGrade(), "La nota del record no es la esperada");
        assertEquals("1", record.getSemester(), "El semestre del record no es el esperado");
    }

    /**
     * Tests the creation of a record when the login of the student is wrong
     */
    @Test
    void testCreateRecordMissingStudent() {
        assertThrows(InvalidRecordException.class,()->{
            recordService.createRecord("", courseCode, 4.0, "3");
        });
    }

    /**
     * Tests the creation of a record when the course code is wrong
     */
    @Test
    void testCreateInscripcionMissingCourse() {
        assertThrows(InvalidRecordException.class,()->{
            recordService.createRecord(login,"", 4.0, "3");
        });
    }

    /**
     * Tests the creation of a record when the grade is not valid
     */
    @Test
    void testCreateInscripcionWrongGrade() {
        assertThrows(InvalidRecordException.class,()->{
            recordService.createRecord(login,courseCode, 1.0, "3");
        });
    }

    /**
     * Tests the creation of a record when the student already has a passing grade
     * for the course
     */
    @Test
    void testCreateInscripcionRepetida1() {
        assertThrows(InvalidRecordException.class,()->{
            recordService.createRecord(login,courseCode, 4.0, "3");
            recordService.createRecord(login,courseCode, 3.0, "4");
        });
    }

    /**
     * Tests the creation of a record when the student already has a record for the
     * course, but he has not passed the course yet.
          * @throws InvalidRecordException 
          */
    @Test
    void testCreateInscripcionRepetida2() throws InvalidRecordException {
        recordService.createRecord(login, courseCode, 1.9, "1");
        RecordEntity record = recordService.createRecord(login, courseCode, 4.0, "1");

        assertEquals(courseRepository.findByCourseCode(courseCode).get(), record.getCourse(), "El curso en el record no coincide con el curso esperado");
        assertEquals(studentRepository.findByLogin(login).get(), record.getStudent(), "El estudiante del record no es el esperado");
        assertEquals(4.0, record.getFinalGrade(), "La nota del record no es la esperada");
        assertEquals("1", record.getSemester(), "El semestre del record no es el esperado");
    }
}
