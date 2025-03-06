package uniandes.dse.examen1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.repositories.CourseRepository;

@Slf4j
@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    public CourseEntity createCourse(CourseEntity newCourse) throws RepeatedCourseException {
        log.info("Inicia el proceso de crear un nuevo curso");
        //Verificamos que el código no se repita
        Optional<CourseEntity> cursoExistente = courseRepository.findByCourseCode(newCourse.getCourseCode());
        if (!cursoExistente.isEmpty())
        {
            throw new RepeatedCourseException(newCourse.getCourseCode());
        }
        log.info("Finaliza el proceso de crear un nuevo curso");
        return courseRepository.save(newCourse);
    }
}
