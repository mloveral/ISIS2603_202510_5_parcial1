package uniandes.dse.examen1.services;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.modeler.OperationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class RecordService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    RecordRepository recordRepository;

    public RecordEntity createRecord(String loginStudent, String courseCode, Double grade, String semester)
            throws InvalidRecordException {
        log.info("Inicia el proceso de crear un nuevo registro");
        //Verificar que el estudiante exista
        Optional<StudentEntity> estudiante = studentRepository.findByLogin(loginStudent);
        if (estudiante.isEmpty())
        {
            throw new InvalidRecordException("El estudiante con el login "+loginStudent+" no existe");
        }

        //Verificar que el curso exista
        Optional<CourseEntity> curso = courseRepository.findByCourseCode(courseCode);
        if (curso.isEmpty())
        {
            throw new InvalidRecordException("El curso con el código "+courseCode+" no existe");
        }

        StudentEntity student = estudiante.get();
        CourseEntity course = curso.get();

        if (!student.getCourses().contains(course))
        {
            student.getCourses().add(course);
        }
        else
        {
            boolean notFound = true;
            Iterator<RecordEntity> i = student.getRecords().iterator();
            while (i.hasNext() || notFound) 
            {
                RecordEntity r = i.next();
                if (r.getCourse().equals(course))
                {
                    notFound = false;
                    if (r.getFinalGrade() >= 3.0)
                    {
                        throw new InvalidRecordException("El estudiante ya aprobo el curso");
                    }
                }
            }
        }

        if (!course.getStudents().contains(student))
        {
            course.getStudents().add(student);
        }

        RecordEntity record = new RecordEntity();
        record.setCourse(course);
        record.setStudent(student);
        record.setFinalGrade(grade);
        record.setSemester(semester);

        log.info("Finaliza el proceso de crear un nuevo registro");
        return recordRepository.save(record);
    }
}
