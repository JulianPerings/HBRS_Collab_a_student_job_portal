package org.hbrs.se2.project.services;

import org.hbrs.se2.project.control.exception.DatabaseUserException;
import org.hbrs.se2.project.dtos.*;

import java.util.Set;

public interface ProfileServiceInterface {
    void saveStudentData(UserDTO user, StudentDTO student, Set<String> major, Set<String> topic, Set<String> skill) throws DatabaseUserException;

    void saveCompanyData(UserDTO user, CompanyDTO company);

    Set<MajorDTO> getMajorOfStudent(int userid);

    Set<TopicDTO> getTopicOfStudent(int userid);

    Set<SkillDTO> getSkillOfStudent(int userid);

    void removeMajor(int userid, int majorid);

    void removeTopic(int userid, int topicid);

    void removeSkill(int userid, int skillid);
}
