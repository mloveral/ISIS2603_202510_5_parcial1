package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.services.CourseService;

@DataJpaTest
@Transactional
@Import(CourseService.class)
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private CourseEntity curso;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData()
    {
        entityManager.getEntityManager().createQuery("delete from CourseEntity").executeUpdate();
    }

    private void insertData()
    {
        curso = factory.manufacturePojo(CourseEntity.class);
        curso.setCourseCode("001");
        entityManager.persist(curso);
    }

    @Test
    void testCreateCourse() throws RepeatedCourseException {
        CourseEntity nuevoCurso = factory.manufacturePojo(CourseEntity.class);
        nuevoCurso.setCourseCode("002");
        
        CourseEntity cursoObtenido = courseService.createCourse(nuevoCurso);

        assertEquals(nuevoCurso.getCourseCode(), cursoObtenido.getCourseCode());
        assertEquals(nuevoCurso.getName(), cursoObtenido.getName());
        assertEquals(nuevoCurso.getCredits(), cursoObtenido.getCredits());
    }

    @Test
    void testCreateRepeatedCourse() {
        assertThrows(RepeatedCourseException.class, ()-> {
            CourseEntity nuevoCurso = factory.manufacturePojo(CourseEntity.class);
            nuevoCurso.setCourseCode(curso.getCourseCode());
            courseService.createCourse(nuevoCurso);
        });
    }
}
