package org.hbrs.se2.project.views.companyViews;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hbrs.se2.project.control.InboxControl;
import org.hbrs.se2.project.control.UserControl;
import org.hbrs.se2.project.util.Globals;
import org.hbrs.se2.project.views.AppView;
import org.hbrs.se2.project.views.InboxView;

@Route(value = Globals.Pages.COMPANY_INBOX_VIEW, layout = AppView.class, registerAtStartup = false)
@PageTitle("Posteingang")
public class CompanyInboxView extends InboxView {

    public CompanyInboxView(InboxControl inboxControl, UserControl userControl) {
        this.inboxControl = inboxControl;
        this.userControl = userControl;

        conversationGrid = conversationGrid(inboxControl.getConversationsOfCompany(userControl.getCompanyProfile(userControl.getCurrentUser().getUserid()).getCompanyid()));
        conversationGrid.addComponentColumn(conversation -> conversationComponent(conversation,
                inboxControl.getNameOfStudentFromConversation(conversation))).setWidth("70%").setHeader("Posteingang");
        conversationGrid.addComponentColumn(this::unreadMessages).setWidth("10%").setTextAlign(ColumnTextAlign.END);
        conversationGrid.addComponentColumn(this::latestMessage).setWidth("20%").setTextAlign(ColumnTextAlign.END);
        conversationGrid.addSelectionListener(select -> select.getFirstSelectedItem().ifPresent(conversation -> {
            chatLayout.replace(chatLayout.getComponentAt(0), conversationHeader(conversation));
            chatLayout.replace(chatLayout.getComponentAt(1), conversationLayout(inboxControl.getMessagesOfCompany(conversation), conversation));
        }));
        setSizeFull();
        setHeightFull();
        add(mainLayout);
    }

}
