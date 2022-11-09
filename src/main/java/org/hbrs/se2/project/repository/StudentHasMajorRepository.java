package org.hbrs.se2.project.repository;

import org.hbrs.se2.project.dtos.StudentHasMajorDTO;
import org.hbrs.se2.project.entities.StudentHasMajor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StudentHasMajorRepository extends JpaRepository<StudentHasMajor, Integer> {

    // return a list of StudentHasMajor DTOs that match the passed student id
    List<StudentHasMajorDTO> findByStudentid(int studentid);
}
