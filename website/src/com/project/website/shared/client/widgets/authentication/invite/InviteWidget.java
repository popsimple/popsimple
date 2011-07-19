package com.project.website.shared.client.widgets.authentication.invite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;

public class InviteWidget extends Composite {

    private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);

    interface InviteWidgetUiBinder extends UiBinder<Widget, InviteWidget> {
    }

    @UiField
    TextBox textName;

    @UiField
    TextArea textMessage;

    @UiField
    TextBox textEmail;

    @UiField
    Label emailErrorLabel;

    @UiField
    Button buttonInvite;

    @UiField
    Button buttonCancel;

    @UiField
    FormPanel inviteForm;

    private SimpleEvent<InviteRequestData> inviteRequestEvent = new SimpleEvent<InviteRequestData>();
    private SimpleEvent<Void> cancelRequestEvent = new SimpleEvent<Void>();

    public class InviteRequestData {
        private final String email;
        private final String message;
        private final String name;

        public InviteRequestData(String email, String name, String message)
        {
            this.email = email;
            this.name = name;
            this.message = message;
        }

        public String getName()
        {
            return name;
        }

        public String getEmail()
        {
            return email;
        }
        public String getMessage()
        {
            return message;
        }
    }

    public InviteWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        this.registerFormHandlers();

        this.buttonInvite.setStylePrimaryName("gwt-Button");
    }

    public HandlerRegistration addInviteRequestHandler(Handler<InviteRequestData> handler) {
        return this.inviteRequestEvent.addHandler(handler);
    }

    public HandlerRegistration addCancelRequestHandler(Handler<Void> handler) {
        return this.cancelRequestEvent.addHandler(handler);
    }

    private void registerFormHandlers() {

        //NOTE: Due to a bug in GWT we need to manually handle the submit click otherwise
        //NOTE: it throws an exception that the gwt module might need to be recompiled.
        //NOTE: refer to http://code.google.com/p/google-web-toolkit/issues/detail?id=5067
        this.buttonInvite.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                event.preventDefault();
                inviteForm.submit();
            }
        });

        this.inviteForm.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
                submitInvite(textEmail.getText(), textName.getText(), textMessage.getText());
            }
        });

        this.buttonCancel.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent event)
            {
                cancelRequestEvent.dispatch(null);
            }
         });
    }

    private void submitInvite(String email, String name, String message) {
        if (false == this.validateFields()) {
            return;
        }
        inviteRequestEvent.dispatch(new InviteRequestData(email, name, message));
    }


    private void clearError(Label label){
        label.setText("");
        label.setVisible(false);
    }

    private void setError(Label label, String error){
        label.setText(error);
        label.setVisible(true);
    }

    //TODO: use some kind of validator.
    private boolean validateFields()
    {
        boolean isValid = true;

        this.clearError(emailErrorLabel);
        if (false == this.textEmail.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"))
        {
            isValid = false;
            this.setError(emailErrorLabel, "Invalid email address");
        }
        return isValid;
    }
}
