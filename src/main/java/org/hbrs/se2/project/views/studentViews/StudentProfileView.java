package org.hbrs.se2.project.views.studentViews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextField;
import org.hbrs.se2.project.control.ProfileControl;
import org.hbrs.se2.project.control.UserControl;
import org.hbrs.se2.project.control.exception.DatabaseUserException;
import org.hbrs.se2.project.dtos.*;
import org.hbrs.se2.project.dtos.impl.StudentDTOImpl;
import org.hbrs.se2.project.util.Globals;
import org.hbrs.se2.project.util.Utils;
import org.hbrs.se2.project.views.AppView;
import org.hbrs.se2.project.views.ProfileView;
import org.slf4j.Logger;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Route(value = Globals.Pages.STUDENT_PROFILE_VIEW, layout = AppView.class, registerAtStartup = false)
@PageTitle("Profile")
public class StudentProfileView extends ProfileView {

    private final Logger logger = Utils.getLogger(this.getClass().getName());

    private final TextField major = new TextField("Füge neuen Major hinzu:");
    private final TextField topic = new TextField("Füge neuen Topic hinzu:");
    private final TextField skill = new TextField("Füge neuen Skill hinzu:");
    private final TextField firstname = new TextField("Vorname:");
    private final TextField lastname = new TextField("Nachname:");
    private final TextField university = new TextField("Universität:");
    private final TextField matrikelnumber = new TextField("Matrikelnummer:");

    private final Grid<MajorDTO> gridMajors = new Grid<>();
    private final Grid<TopicDTO> gridTopics = new Grid<>();
    private final Grid<SkillDTO> gridSkills = new Grid<>();
    private final Set<MajorDTO> majors;
    private final Set<TopicDTO> topics;
    private final Set<SkillDTO> skills;
    private Set<String> newMajors, newTopics, newSkills;
    private final Set<MajorDTO> removeMajors = new HashSet<>();
    private final Set<TopicDTO> removeTopics = new HashSet<>();
    private final Set<SkillDTO> removeSkills = new HashSet<>();

    private final Binder<StudentDTOImpl> studentBinder = new BeanValidationBinder<>(StudentDTOImpl.class);

    public StudentProfileView(ProfileControl profileControl, UserControl userControl) {
        this.profileControl = profileControl;
        this.userControl = userControl;

        majors = profileControl.getMajorOfStudent(userControl.getCurrentUser().getUserid());
        topics = profileControl.getTopicOfStudent(userControl.getCurrentUser().getUserid());
        skills = profileControl.getSkillOfStudent(userControl.getCurrentUser().getUserid());

        setSizeFull();
        setAllGrids();
        setUserBinder();
        setStudentBinder();
        viewLayout();
    }

    private void setAllGrids() {
        // Create grids for skills, topics and majors
        gridMajors.setAllRowsVisible(true);
        gridMajors.addColumn(MajorDTO::getMajor).setHeader("Majors:");
        gridMajors.setItems(majors);
        gridTopics.setAllRowsVisible(true);
        gridTopics.addColumn(TopicDTO::getTopic).setHeader("Topics:");
        gridTopics.setItems(topics);
        gridSkills.setAllRowsVisible(true);
        gridSkills.addColumn(SkillDTO::getSkill).setHeader("Skills:");
        gridSkills.setItems(skills);
    }

    private void setEditGrids() {
        Grid<String> newMajorsGrid, newTopicsGrid, newSkillsGrid;
        gridMajors.addComponentColumn(major -> {
            Button deleteButton = new Button("Entfernen");
            deleteButton.addClickListener(e -> {
                removeMajors.add(major);
                majors.remove(major);
                gridMajors.setItems(majors);
            });
            return deleteButton;
        });
        formLayout.add(gridMajors, newMajorsGrid = newEntriesGrid(newMajors = new HashSet<>()), newEntryLayout(major, newMajors, newMajorsGrid));
        gridTopics.addComponentColumn(topic -> {
            Button deleteButton = new Button("Entfernen");
            deleteButton.addClickListener(e -> {
                removeTopics.add(topic);
                topics.remove(topic);
                gridTopics.setItems(topics);
            });
            return deleteButton;
        });
        formLayout.add(gridTopics, newTopicsGrid = newEntriesGrid(newTopics = new HashSet<>()), newEntryLayout(topic, newTopics, newTopicsGrid));
        gridSkills.addComponentColumn(skill -> {
            Button deleteButton = new Button("Entfernen");
            deleteButton.addClickListener(e -> {
                removeSkills.add(skill);
                skills.remove(skill);
                gridSkills.setItems(skills);
            });
            return deleteButton;
        });
        formLayout.add(gridSkills, newSkillsGrid = newEntriesGrid(newSkills = new HashSet<>()), newEntryLayout(skill, newSkills, newSkillsGrid));
    }

    private void editLayout() {
        Stream.of(username, firstname, lastname, email, university, matrikelnumber).forEach(field ->
                field.setReadOnly(false)
        );
        setEditGrids();

        button = new Button("Profil speichern");
        formLayout.add(button);
        button.addClickListener(buttonClickEvent -> {
            if (userBinder.isValid() && studentBinder.isValid()) {
                ui.makeConfirm("Möchtest du die Änderungen an deinem Profil speichern?",
                        event -> {
                            try {
                                if (!userBinder.getBean().getUsername().equals(userControl.getCurrentUser().getUsername())) {
                                    authorizationControl.logoutUser();
                                } else {
                                    // reload page to get updated view
                                    UI.getCurrent().getPage().reload();
                                }
                                profileControl.saveStudentData(
                                        userBinder.getBean(), studentBinder.getBean(),
                                        newMajors, newTopics, newSkills);
                            } catch (DatabaseUserException e) {
                                logger.error("Something went wrong with saving student data");
                            }
                            removeMajors.forEach(major -> profileControl.removeMajor(userControl.getCurrentUser().getUserid(), major.getMajorid()));
                            removeTopics.forEach(topic -> profileControl.removeTopic(userControl.getCurrentUser().getUserid(), topic.getTopicid()));
                            removeSkills.forEach(skill -> profileControl.removeSkill(userControl.getCurrentUser().getUserid(), skill.getSkillid()));
                        });

            } else {
                ui.makeDialog("Überprüfe bitte deine Angaben auf Korrektheit");
            }
        });
    }

    private void viewLayout() {
        // set value of text fields to read only for profile view
        Stream.of(username, email, firstname, lastname, university, matrikelnumber).forEach(field -> {
            formLayout.add(field);
            field.setReadOnly(true);
        });
        formLayout.add(username, email, firstname, lastname, university, matrikelnumber);
        formLayout.setColspan(gridSkills, 2);
        formLayout.setColspan(gridTopics, 2);
        formLayout.setColspan(gridMajors, 2);
        //Rahmen noch setzen
        //alles zentrieren und abstand links und rechts
        //zwischen den grids ein bisschen abstand

        button = new Button("Profil bearbeiten");
        formLayout.add(gridMajors, gridTopics, gridSkills, button, changePasswd, delete);
        button.addClickListener(buttonClickEvent -> {
            formLayout.remove(gridMajors, gridTopics, gridSkills, button, changePasswd, delete);
            editLayout();
        });
    }

    private Grid<String> newEntriesGrid(Set<String> entries) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(String::valueOf);
        grid.setAllRowsVisible(true);
        grid.addComponentColumn(newEntry -> {
            Button deleteButton = new Button("Entfernen");
            deleteButton.addClickListener(e -> {
                entries.remove(newEntry);
                grid.setItems(entries);
            });
            return deleteButton;
        });
        return grid;
    }

    private FormLayout newEntryLayout(TextField input, Set<String> entries, Grid<String> grid) {
        FormLayout entryForm = new FormLayout();
        Button saveButton = new Button("Hinzufügen");
        saveButton.addClickListener(e -> {
            if (!input.getValue().isBlank()) {
                entries.add(input.getValue());
                grid.setItems(entries);
            }
            input.clear();
        });
        entryForm.add(input);
        entryForm.add(saveButton);
        return entryForm;
    }

    private void setStudentBinder() {
        studentBinder.setBean(mapper.map(userControl.getStudentProfile(userControl.getCurrentUser().getUserid()), StudentDTOImpl.class));
        studentBinder
                .forField(matrikelnumber)
                .asRequired("Darf nicht leer sein")
                .withValidator(matrikelnumber -> matrikelnumber.matches("-?\\d+")
                        && matrikelnumber.length() <= 7, "Keine gültige Matrikelnummer")
                .withValidator(matrikelnumber -> matrikelnumber.equals(userControl.getStudentProfile(userControl.getCurrentUser().getUserid()).getMatrikelnumber())
                        || userControl.checkMatrikelnumberUnique(matrikelnumber), "Matrikelnummer existiert bereits")
                .bind(StudentDTOImpl::getMatrikelnumber, StudentDTOImpl::setMatrikelnumber);
        studentBinder.bindInstanceFields(this);
    }
}
