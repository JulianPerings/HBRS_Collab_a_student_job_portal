package org.hbrs.se2.project.services;

import org.hbrs.se2.project.control.exception.DatabaseUserException;
import org.hbrs.se2.project.dtos.*;

import java.util.List;

public interface ProfileServiceInterface {
    void saveStudentData(UserDTO user, StudentDTO student, List<String> major, List<String> topic, List<String> skill) throws DatabaseUserException;

    List<MajorDTO> getMajorOfStudent(int userid);

    List<TopicDTO> getTopicOfStudent(int userid);

    List<SkillDTO> getSkillOfStudent(int userid);

    StudentDTO getStudentProfile(int userid);

    void removeMajor(int userid, int majorid);

    void removeTopic(int userid, int topicid);

    void removeSkill(int userid, int skillid);
}
