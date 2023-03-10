package org.hbrs.se2.project.services.impl;

import org.hbrs.se2.project.control.exception.DatabaseUserException;
import org.hbrs.se2.project.dtos.CompanyDTO;
import org.hbrs.se2.project.dtos.StudentDTO;
import org.hbrs.se2.project.dtos.UserDTO;
import org.hbrs.se2.project.repository.CompanyRepository;
import org.hbrs.se2.project.repository.StudentRepository;
import org.hbrs.se2.project.repository.UserRepository;
import org.hbrs.se2.project.services.RegistrationServiceInterface;
import org.hbrs.se2.project.services.factory.EntityCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService implements RegistrationServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EntityCreationService entityCreationService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerStudent(UserDTO user, StudentDTO student) throws DatabaseUserException {
        if (!validationService.checkUsernameUnique(user.getUsername())) {
            throw new DatabaseUserException("Username already exists");
        } else if (!validationService.checkEmailUnique(user.getEmail())) {
            throw new DatabaseUserException("Email already exists");
        } else if (!validationService.checkMatrikelnumberUnique(student.getMatrikelnumber())) {
            throw new DatabaseUserException("Matrikelnumber already exists");
        }

        // create new user of role "student"
        this.createAccount(user);
        // get User id from new saved user in db, so we can assign the userid to the data in student table
        UserDTO newSavedUser = userRepository.findByUsername(user.getUsername());
        // create student profile
        this.createStudentProfile(student, newSavedUser);
    }

    @Override
    public void registerCompany(UserDTO user, CompanyDTO company) throws DatabaseUserException {
        if (!validationService.checkUsernameUnique(user.getUsername())) {
            throw new DatabaseUserException("Username already exists");
        } else if (!validationService.checkEmailUnique(user.getEmail())) {
            throw new DatabaseUserException("Email already exists");
        }

        // create new user of role "company"
        this.createAccount(user);
        // get User id from new saved user in db
        UserDTO newSavedUser = userRepository.findByUsername(user.getUsername());
        // create company profile
        this.createCompanyProfile(company, newSavedUser);
    }


    /**
     * Create a new account in the user database
     *
     * @param userDTO User DTO to create user for
     * @throws DatabaseUserException Something went wrong in the database
     */
    private void createAccount(UserDTO userDTO) throws DatabaseUserException {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        try {
            //Saving user in db
            this.userRepository.save(entityCreationService.userFactory().createEntity(userDTO));
        } catch (org.springframework.dao.DataAccessResourceFailureException e) {
            throw new DatabaseUserException("A Failure occurred while saving a user account in the database at createAccount");
        }
    }

    /**
     * Create a new account in the student database and connect it to the user
     *
     * @param studentDTO Student DTO to be saved
     * @param userDTO    User DTO to be saved
     * @throws DatabaseUserException Something went wrong in the database
     */
    private void createStudentProfile(StudentDTO studentDTO, UserDTO userDTO) throws DatabaseUserException {
        try {
            studentDTO.setUserid(userDTO.getUserid());
            this.studentRepository.save(entityCreationService.studentFactory().createEntity(studentDTO));
        } catch (org.springframework.dao.DataAccessResourceFailureException e) {
            throw new DatabaseUserException("A Failure occurred while saving a Student Profile into the database at createStudentProfile");
        }
    }

    /**
     * Create a new Company profile and links it to the company owner
     *
     * @param companyDTO Company DTO to be saved
     * @param userDTO    User DTO of company Owner to be saved
     * @throws DatabaseUserException Something went wrong in the database
     */
    private void createCompanyProfile(CompanyDTO companyDTO, UserDTO userDTO) throws DatabaseUserException {
        try {
            companyDTO.setUserid(userDTO.getUserid());
            this.companyRepository.save(entityCreationService.companyFactory().createEntity(companyDTO));
        } catch (org.springframework.dao.DataAccessResourceFailureException e) {
            throw new DatabaseUserException("A Failure occurred while saving a Company Profile into the database at createCompanyProfile");
        }
    }
}
