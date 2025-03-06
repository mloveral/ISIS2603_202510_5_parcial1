package uniandes.dse.examen1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class StatsService {

    @Autowired
    StudentRepository estudianteRepository;

    @Autowired
    CourseRepository cursoRepository;

    @Autowired
    RecordRepository inscripcionRepository;

    public Double calculateStudentAverage(String login) {
        Optional<StudentEntity> estudiante = estudianteRepository.findByLogin(login);
        if (estudiante.isEmpty())
        {
            
        }
        StudentEntity student = estudiante.get();

        Double count = 0.0;
        Double notasAcumuladas = 0.0;
        for (RecordEntity record: student.getRecords())
        {
            count += 1;
            notasAcumuladas += record.getFinalGrade();
        }

        return notasAcumuladas/count;
    }

    public Double calculateCourseAverage(String courseCode) {
        Optional<CourseEntity> curso = cursoRepository.findByCourseCode(courseCode);
        if (curso.isEmpty())
        {

        }

        CourseEntity course = curso.get();

        Double count = 0.0;
        Double notasAcumuladas = 0.0;
        for (RecordEntity record: course.getRecords())
        {
            count += 1;
            notasAcumuladas += record.getFinalGrade();
        }

        return notasAcumuladas/count;
    }

}
