package com.project.website.shared.client.widgets;

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
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.widgets.DialogWithZIndex;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;

public class MessageBox extends Composite {

    private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);

    interface InviteWidgetUiBinder extends UiBinder<Widget, MessageBox> {
    }

    public enum Result {
        YES,
        NO
    }
    
    @UiField
    Label labelTitle;
    
    @UiField
    Label labelContent;
    
    @UiField
    Button buttonYes;
    
    @UiField
    Button buttonNo;
    
    @UiField
    FormPanel messageBoxForm;

    private SimpleEvent<MessageBox.Result> resultEvent = new SimpleEvent<MessageBox.Result>();

    public MessageBox() {
        initWidget(uiBinder.createAndBindUi(this));

        this.registerFormHandlers();
        this.buttonNo.setStylePrimaryName("gwt-Button");
        this.buttonYes.setStylePrimaryName("gwt-Button");
    }

    public HandlerRegistration addResultHandler(Handler<MessageBox.Result> handler) {
        return this.resultEvent.addHandler(handler);
    }

    private void registerFormHandlers() {

        //NOTE: Due to a bug in GWT we need to manually handle the submit click otherwise
        //NOTE: it throws an exception that the gwt module might need to be recompiled.
        //NOTE: refer to http://code.google.com/p/google-web-toolkit/issues/detail?id=5067
        this.buttonYes.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                event.preventDefault();
                messageBoxForm.submit();
            }
        });

        this.messageBoxForm.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
                resultEvent.dispatch(Result.YES);
            }
        });

        this.buttonNo.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent event)
            {
                resultEvent.dispatch(Result.NO);
            }
         });
    }
    
    public static AsyncFunc<Void, MessageBox.Result> getShowFunc(final String title, final String content)
    {
        return new AsyncFunc<Void, MessageBox.Result>() {
            @Override protected <S, E> void run(Void arg, final Func<MessageBox.Result, S> successHandler, Func<Throwable, E> errorHandler) 
            {
                try {
                    final DialogWithZIndex dialog = new DialogWithZIndex(false, true);
                    final MessageBox messageBox = new MessageBox();
                    dialog.add(messageBox);
                    messageBox.labelTitle.setText(title);
                    messageBox.labelContent.setText(content);
                    messageBox.addResultHandler(new Handler<MessageBox.Result>(){
                        @Override public void onFire(Result arg) {
                            dialog.hide();
                            successHandler.apply(arg);
                        }});
                    dialog.center();
                }
                catch (Throwable e)
                {
                    errorHandler.apply(e);
                }
            }};
    }
}
