package uniandes.dse.examen1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.repositories.StudentRepository;

@Slf4j
@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    public StudentEntity createStudent(StudentEntity newStudent) throws RepeatedStudentException {
        log.info("Inicia el proceso de crear un nuevo estudiante");

        //Se verifica que el login no este repetido
        Optional<StudentEntity> estudianteObtenido = studentRepository.findByLogin(newStudent.getLogin());
        if (!estudianteObtenido.isEmpty())
        {
            throw new RepeatedStudentException(newStudent.getLogin());
        }

        log.info("Finaliza el proceso de crear un nuevo estudiante");
        return studentRepository.save(newStudent);
    }
}
