package org.hbrs.se2.project.views.studentViews;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hbrs.se2.project.control.JobControl;
import org.hbrs.se2.project.control.UserControl;
import org.hbrs.se2.project.control.ReportsControl;
import org.hbrs.se2.project.control.RatingControl;
import org.hbrs.se2.project.dtos.JobDTO;
import org.hbrs.se2.project.dtos.RatingDTO;
import org.hbrs.se2.project.dtos.impl.RatingDTOImpl;
import org.hbrs.se2.project.dtos.impl.ReportsDTOImpl;
import org.hbrs.se2.project.services.ui.CommonUIElementProvider;
import org.hbrs.se2.project.util.Globals;
import org.hbrs.se2.project.views.AppView;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Student - Jobs list View
 */
@Route(value = Globals.Pages.JOBS_VIEW, layout = AppView.class, registerAtStartup = false)
@PageTitle("Jobs")
public class JobsView extends Div {
    private final CommonUIElementProvider ui;
    private final UserControl userControl;
    private final RatingControl ratingControl;
    // interactive search field
    private final TextField searchField = new TextField();
    private final Button searchButton = new Button(getTranslation("view.job.button.search"));
    // Create a Grid bound to the list
    private final Grid<JobDTO> grid = new Grid<>();

    public JobsView(JobControl jobControl, UserControl userControl, CommonUIElementProvider ui, ReportsControl reportsControl, RatingControl ratingControl) {
        this.userControl = userControl;
        this.ui = ui;
        this.ratingControl = ratingControl;

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        searchField.setClearButtonVisible(true); // possibility to delete filter words

        // changing width of textField, buttonFilter and buttonAllJobs to improve on usability
        searchField.setWidth("25");
        searchButton.setWidth("25%");
        Button buttonAllJobs = new Button("Alle Jobs");
        buttonAllJobs.setWidth("25%");

        searchField.setPlaceholder(getTranslation("view.job.text.search"));

        HorizontalLayout topLayout = new HorizontalLayout();

        searchField.getStyle().set("margin-center", "auto");
        searchButton.getStyle().set("margin-center", "auto");
        buttonAllJobs.getStyle().set("margin-center", "auto");

        // Center Alignment
        topLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        topLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // adding TextField and 2 buttons
        topLayout.add(searchField);
        topLayout.add(searchButton);
        topLayout.add(buttonAllJobs);

        layout.add(topLayout);
        layout.add(new Label());
        layout.setWidth("100%");

        // Header
        grid.addColumn(JobDTO::getTitle).setHeader(getTranslation("view.job.text.title")).setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(job -> new Text(jobControl.getCompanyOfJob(job))).setHeader("Unternehmen");
        grid.addColumn(JobDTO::getSalary).setHeader(getTranslation("view.job.text.salary")).setSortable(true);
        grid.setAllRowsVisible(true);
        searchField.addKeyPressListener(Key.ENTER, event -> searchButton.clickInClient());
        // pass relevant job list to grid
        searchButton.addClickListener(event -> {grid.setItems(jobControl.getJobsMatchingKeyword(searchField.getValue())); searchField.clear();});
        // set items details renderer
        grid.setItemDetailsRenderer(new ComponentRenderer<>(job -> {
            VerticalLayout vLayout = new VerticalLayout();
            FormLayout formLayout = new FormLayout();
            FormLayout buttons = new FormLayout();

            final TextField companyName = new TextField(getTranslation("view.job.text.company"));
            final Span rating = getRating(job.getCompanyid());
            final TextField jobLocation = new TextField(getTranslation("view.job.text.location"));
            final TextField companyContactDetails = new TextField(getTranslation("view.job.text.contactDetails"));
            final TextArea jobDescription = new TextArea(getTranslation("view.job.text.description"));

            // set all fields with job details
            companyName.setValue(jobControl.getCompanyOfJob(job));
            jobLocation.setValue(job.getLocation());
            companyContactDetails.setValue(job.getContactdetails());
            jobDescription.setValue(job.getDescription());

            // add textFields to FormLayout
            formLayout.add(companyName, rating, jobLocation, companyContactDetails, jobDescription);
            Stream.of(companyName, jobLocation, companyContactDetails, jobDescription).forEach(
                    field -> field.setReadOnly(true)
            );

            Button contact = new Button("Kontaktieren");
            contact.addClickListener(event -> ui.makeConversationDialogStudent(job.getCompanyid(), userControl.getStudentProfile(
                    userControl.getCurrentUser().getUserid()).getStudentid(), job.getJobid()));
            buttons.add(contact);
            if(!reportsControl.studentHasReportedCompany(job.getCompanyid(),  userControl.getStudentProfile(
                    userControl.getCurrentUser().getUserid()).getStudentid())) {
                Button report = new Button("Melden");
                report.addClickListener(event -> {
                    Binder<ReportsDTOImpl> binder = new BeanValidationBinder<>(ReportsDTOImpl.class);
                    binder.setBean(new ReportsDTOImpl(job.getCompanyid(), userControl.getStudentProfile(
                            userControl.getCurrentUser().getUserid()).getStudentid()));
                    reportsControl.createReport(binder.getBean());
                    UI.getCurrent().getPage().reload();
                });
                buttons.add(report);
            } else {
                Button report = new Button("Bereits gemeldet");
                report.setEnabled(false);
                buttons.add(report);
            }

            if(!ratingControl.studentHasRatedCompany(job.getCompanyid(),  userControl.getStudentProfile(
                    userControl.getCurrentUser().getUserid()).getStudentid())) {
                Button rate = new Button("Bewerten");
                rate.addClickListener(event -> rateDialog(job));
                buttons.add(rate);
            } else {
                Button rate = new Button("Bereits bewertet");
                rate.setEnabled(false);
                buttons.add(rate);
            }

            formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            formLayout.setColspan(jobDescription, 2);
            buttons.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));

            vLayout.add(formLayout, buttons);
            return vLayout;
        }));

        grid.setItems(jobControl.getAllJobs());

        // buttonAllJobs will show all Job Ads without any filter

        buttonAllJobs.addClickListener(event -> {
            List<JobDTO> jobDetails = jobControl.getAllJobs();
            grid.setItems(jobDetails);
        });

        // Adding column Borders and Row Stripes for better visibility
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        /*
        Adding a text with the number of available Jobs underneath the grid
        To achieve that I used a workaround instead of working with the size of the grid
        */

        List<JobDTO> jobDetails = jobControl.getAllJobs();

        int jobCount = jobDetails.size();
        String s = "Es sind momentan " + jobCount + " Jobs f??r Sie verf??gbar! ";
        H2 jobCountText = new H2(s);

        jobCountText.getElement().getStyle().set("font-size","15px");
        jobCountText.getElement().getStyle().set("text-align","center");

        // adding text, topLayout, grid and the text for showing the total number of jobs available

        /*
        with introductionText() below the career page will show information and help the user
        guide through the option of either filtering the job ads or showing all available job ads
        */
        add(ui.introductionText("Willkommen auf Ihrer Karriereseite!", "Sie k??nnen nach Jobs filtern oder sich direkt alle anzeigen lassen!"));
        add(topLayout);
        add(grid);
        add(jobCountText);
    }

    private Span getRating(int companyid){
        Span rating = new Span("Unternehmensreputation: ");
        Float avg = ratingControl.getRating(companyid);
        if (avg == null) {
            avg = 0.0F;
        }
        for (int i = 0; i < 5; i++) {
            if (avg >= 1) {
                rating.add(new Icon(VaadinIcon.STAR));
            } else if (avg > 0.25 && avg < 0.75) {
                rating.add(new Icon(VaadinIcon.STAR_HALF_LEFT_O));
            } else {
                rating.add(new Icon(VaadinIcon.STAR_O));
            }
            avg = avg - 1;
        }
        return rating;
    }

    private void rateDialog(JobDTO job){
        Dialog dialog = new Dialog();
        AtomicInteger rating = new AtomicInteger();
        Button close = new Button("Abbrechen");
        close.addClickListener(event -> dialog.close());
        AtomicReference<Icon> oneStar = new AtomicReference<>(new Icon(VaadinIcon.STAR_O));
        AtomicReference<Icon> twoStars = new AtomicReference<>(new Icon(VaadinIcon.STAR_O));
        AtomicReference<Icon> threeStars = new AtomicReference<>(new Icon(VaadinIcon.STAR_O));
        AtomicReference<Icon> fourStars = new AtomicReference<>(new Icon(VaadinIcon.STAR_O));
        AtomicReference<Icon> fiveStars = new AtomicReference<>(new Icon(VaadinIcon.STAR_O));
        HorizontalLayout stars = new HorizontalLayout(oneStar.get(), twoStars.get(), threeStars.get(), fourStars.get(), fiveStars.get());
        Button confirm = new Button("Bewertung abgeben");
        confirm.addClickListener(event -> ui.makeYesNoDialog("M??chtest du die Bewertung so einreichen?", click -> {
            RatingDTO ratingDTO = new RatingDTOImpl(userControl.getStudentProfile(userControl.getCurrentUser().getUserid()).getStudentid(),
                    job.getCompanyid(), rating.get());
            ratingControl.createRating(ratingDTO);
            UI.getCurrent().getPage().reload();
        }));
        confirm.setEnabled(false);
        HorizontalLayout buttons = new HorizontalLayout(close, confirm);
        VerticalLayout layout = new VerticalLayout(new Text("Wie zufrieden bist du mit dem Unternehmen von einem Stern (nicht zufrieden) bis f??nf Sternen (sehr zufrieden)?"),
                stars, buttons);
        layout.setWidth("400px");
        oneStar.get().addClickListener(event -> {
            oneStar.set(new Icon(VaadinIcon.STAR));
            rating.set(1);
            stars.removeAll();
            stars.add(oneStar.get(), twoStars.get(), threeStars.get(), fourStars.get(), fiveStars.get());
            confirm.setEnabled(true);
        });
        twoStars.get().addClickListener(event -> {
            oneStar.set(new Icon(VaadinIcon.STAR));
            twoStars.set(new Icon(VaadinIcon.STAR));
            rating.set(2);
            stars.removeAll();
            stars.add(oneStar.get(), twoStars.get(), threeStars.get(), fourStars.get(), fiveStars.get());
            confirm.setEnabled(true);
        });
        threeStars.get().addClickListener(event -> {
            oneStar.set(new Icon(VaadinIcon.STAR));
            twoStars.set(new Icon(VaadinIcon.STAR));
            threeStars.set(new Icon(VaadinIcon.STAR));
            rating.set(3);
            stars.removeAll();
            stars.add(oneStar.get(), twoStars.get(), threeStars.get(), fourStars.get(), fiveStars.get());
            confirm.setEnabled(true);
        });
        fourStars.get().addClickListener(event -> {
            oneStar.set(new Icon(VaadinIcon.STAR));
            twoStars.set(new Icon(VaadinIcon.STAR));
            threeStars.set(new Icon(VaadinIcon.STAR));
            fourStars.set(new Icon(VaadinIcon.STAR));
            rating.set(4);
            stars.removeAll();
            stars.add(oneStar.get(), twoStars.get(), threeStars.get(), fourStars.get(), fiveStars.get());
            confirm.setEnabled(true);
        });
        fiveStars.get().addClickListener(event -> {
            oneStar.set(new Icon(VaadinIcon.STAR));
            twoStars.set(new Icon(VaadinIcon.STAR));
            threeStars.set(new Icon(VaadinIcon.STAR));
            fourStars.set(new Icon(VaadinIcon.STAR));
            fiveStars.set(new Icon(VaadinIcon.STAR));
            rating.set(5);
            stars.removeAll();
            stars.add(oneStar.get(), twoStars.get(), threeStars.get(), fourStars.get(), fiveStars.get());
            confirm.setEnabled(true);
        });
        dialog.add(layout);
        dialog.open();
    }

}
