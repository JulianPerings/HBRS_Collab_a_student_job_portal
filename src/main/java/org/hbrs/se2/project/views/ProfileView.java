package org.hbrs.se2.project.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import org.hbrs.se2.project.control.ProfileControl;
import org.hbrs.se2.project.dtos.UserDTO;
import org.hbrs.se2.project.dtos.impl.UserDTOImpl;
import org.hbrs.se2.project.services.ui.CommonUIElementProvider;
import org.hbrs.se2.project.util.Utils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ProfileView extends Div {

    private final Logger logger = Utils.getLogger(this.getClass().getName());

    protected ProfileControl profileControl;

    @Autowired
    protected CommonUIElementProvider ui;

    protected final UserDTO CURRENT_USER = Utils.getCurrentUser();

    protected TextField username = new TextField("Benutzername:");
    protected TextField email = new TextField("EMail-Adresse:");
    protected PasswordField password = new PasswordField("Passwort:");
    protected PasswordField passwordConfirm = new PasswordField("Passwort bestätigen:");


    protected Button button;
    protected Button delete = new Button("Account löschen");
    protected Button changePasswd = new Button("Passwort ändern");
    protected FormLayout formLayout = new FormLayout();

    protected Binder<UserDTOImpl> userBinder = new BeanValidationBinder<>(UserDTOImpl.class);
    protected Binder<UserDTOImpl> passwordBinder = new BeanValidationBinder<>(UserDTOImpl.class);


    protected final ModelMapper mapper = new ModelMapper();

    public ProfileView() {
        delete.addClickListener(buttonClickEvent -> ui.makeDeleteConfirm("Bitte gib deinen Accountnamen zur Bestätigung ein:", event -> {
            try {
                profileControl.deleteUser(CURRENT_USER);
                this.getUI().ifPresent(ui -> {
                    ui.getSession().close();
                    ui.getPage().setLocation("/");
                });
            } catch (Exception e) {
                logger.error("Something went wrong when deleting student from DB");
            }
        }));

        changePasswd.addClickListener(buttonClickEvent -> {
            VerticalLayout vLayout = new VerticalLayout();
            Dialog dialog = new Dialog();
            Button close = new Button("Abbrechen");
            close.addClickListener(event -> dialog.close());
            Button save = new Button("Speichern");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            save.addClickListener(event -> {
                if (passwordBinder.isValid()) {
                    try {
                        profileControl.changeUserPassword(passwordBinder.getBean());
                        ui.makeDialog("Passwort erfolgreich geändert");
                    } catch (Exception e) {
                        logger.error("Something went wrong while changing the password", e);
                        ui.makeDialog("Passwort NICHT geändert");
                    }
                    dialog.close();
                } else {
                    ui.makeDialog("Bitte überprüfe deine Eingaben");
                }
            });
            HorizontalLayout hLayout = new HorizontalLayout();
            hLayout.add(close, save);
            vLayout.add(new Text("Bitte gib dein neues Passwort ein und bestätige es. Mindestens 8 Zeichen bestehend aus Buchstaben, Zahlen und Sonderzeichen."), password, passwordConfirm, hLayout);
            vLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            dialog.add(vLayout);
            dialog.open();
        });
    }

    protected void setUserBinder() {
        passwordBinder.setBean(mapper.map(CURRENT_USER, UserDTOImpl.class));
        passwordBinder
                .forField(password)
                .asRequired()
                .withValidator(pw -> pw.matches("^(?=.+[a-zA-Z])(?=.+\\d)(?=.+\\W).{8,}$"),"Dein Passwort ist wahrscheinlich nicht sicher genug. Halte dich bitte an die Vorgaben")
                .bind(UserDTOImpl::getPassword, UserDTOImpl::setPassword);
        passwordBinder
                .forField(passwordConfirm)
                .asRequired()
                .withValidator(pw -> pw.equals(password.getValue()), "Passwörter stimmen nicht überein")
                .bind(UserDTOImpl::getPassword, UserDTOImpl::setPassword);
        password.clear();
        passwordConfirm.clear();

        userBinder.setBean(mapper.map(CURRENT_USER, UserDTOImpl.class));
        userBinder
                .forField(username)
                .asRequired("Darf nicht leer sein")
                .withValidator(user -> user.equals(CURRENT_USER.getUsername())
                        || profileControl.checkUsernameUnique(user), "Benutzername existiert bereits")
                .bind(UserDTOImpl::getUsername, UserDTOImpl::setUsername);
        userBinder
                .forField(email)
                .asRequired("Darf nicht leer sein")
                .withValidator(new EmailValidator("Keine gültige EMail Adresse"))
                .withValidator(email -> email.equals(CURRENT_USER.getEmail())
                        || profileControl.checkEmailUnique(email), "Email existiert bereits")
                .bind(UserDTOImpl::getEmail, UserDTOImpl::setEmail);
    }
}
