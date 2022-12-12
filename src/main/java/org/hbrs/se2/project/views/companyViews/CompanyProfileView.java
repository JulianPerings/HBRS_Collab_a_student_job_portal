package org.hbrs.se2.project.views.companyViews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hbrs.se2.project.control.ProfileControl;
import org.hbrs.se2.project.control.UserControl;
import org.hbrs.se2.project.dtos.impl.CompanyDTOImpl;
import org.hbrs.se2.project.util.Globals;
import org.hbrs.se2.project.views.AppView;
import org.hbrs.se2.project.views.ProfileView;

import java.util.stream.Stream;

@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(value = "./themes/mytheme/styles.css", include = "lumo-badge")
@Route(value = Globals.Pages.COMPANY_PROFILE_VIEW, layout = AppView.class, registerAtStartup = false)
@PageTitle("Profile")
public class CompanyProfileView extends ProfileView {

    private boolean banned;
    private final TextField name = new TextField("Name Ihres Unternehmens:");
    private final TextField industry = new TextField("Industrie:");

    private final Binder<CompanyDTOImpl> companyBinder = new BeanValidationBinder<>(CompanyDTOImpl.class);

    public CompanyProfileView(ProfileControl profileControl, UserControl userControl) {
        this.profileControl = profileControl;
        this.userControl = userControl;

        setSizeFull();
        add(formLayout);
        setUserBinder();
        setCompanyBinder();
        viewLayout();
    }

    private void viewLayout() {
        HorizontalLayout status = new HorizontalLayout();
        status.add("Accountstatus:");
        status.add(statusBadge());
        status.setPadding(true);
        formLayout.add(status);
        // set value of text fields to read only for profile view
        Stream.of(username, name, email, industry).forEach(
                field -> {
                    field.setReadOnly(true);
                    formLayout.add(field);
                }
        );
        button = new Button("Profil bearbeiten");
        formLayout.add(button, changePasswd, delete);
        button.addClickListener(buttonClickEvent -> {
            formLayout.remove(button, changePasswd, delete);
            editLayout();
        });
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    }

    private void editLayout() {
        Stream.of(username, name, email, industry).forEach(
                field -> field.setReadOnly(false)
        );
        button = new Button("Profil speichern");
        formLayout.add(button);
        button.addClickListener(buttonClickEvent -> {
            if (userBinder.isValid() && companyBinder.isValid()) {
                ui.makeConfirm("Möchten Sie die Änderungen an Ihrem Profil speichern?",
                        event -> {profileControl.saveCompanyData(userBinder.getBean(), companyBinder.getBean());UI.getCurrent().getPage().reload();});
            } else {
                ui.makeDialog("Überprüfen Sie bitte Ihre Angaben auf Korrektheit");
            }
        });
    }

    private void setCompanyBinder() {
        companyBinder.setBean(mapper.map(userControl.getCompanyProfile(userControl.getCurrentUser().getUserid()), CompanyDTOImpl.class));
        companyBinder.bindInstanceFields(this);
        banned = companyBinder.getBean().getBanned();
    }

    private Span statusBadge() {
        Icon icon;
        Span info, badge;
        if (banned) {
            icon = new Icon(VaadinIcon.INFO_CIRCLE);
            icon.getStyle().set("padding", "var(--lumo-space-xs");
            info = new Span("Gesperrt");
            badge = new Span(info, icon);
            badge.getElement().getThemeList().add("badge error");
        } else {
            icon = new Icon(VaadinIcon.CHECK);
            icon.getStyle().set("padding", "var(--lumo-space-xs");
            info = new Span("OK");
            badge = new Span(info, icon);
            badge.getElement().getThemeList().add("badge success");
        }
        return badge;
    }

}
