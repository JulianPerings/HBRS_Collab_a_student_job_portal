package org.hbrs.se2.project.repository;

import org.hbrs.se2.project.dtos.MajorDTO;
import org.hbrs.se2.project.entities.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {

    MajorDTO findByMajor(String major);

    MajorDTO findByMajorid(int majorid);

}