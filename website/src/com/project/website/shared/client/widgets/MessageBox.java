package com.project.website.shared.client.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.widgets.DialogWithZIndex;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;

// TODO create a shared styles package and move this to Shared project
public class MessageBox<T> extends Composite {

    private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);

    interface InviteWidgetUiBinder extends UiBinder<Widget, MessageBox> {
    }
    
    @UiField
    Label labelTitle;
    
    @UiField
    Label labelContent;
    
    @UiField
    FlowPanel buttonsPanel;
    
    @UiField
    FormPanel messageBoxForm;

    private final Map<T, String> _buttonValueLabels;
    private final SimpleEvent<T> resultEvent = new SimpleEvent<T>();
    private final HashMap<Widget, T> _buttonValues = new HashMap<Widget, T>();
    private final T _defaultValue;

    public MessageBox(Map<T, String> buttonValueLabels, T defaultValue) {
        initWidget(uiBinder.createAndBindUi(this));

        this._defaultValue = defaultValue;
        this._buttonValueLabels = new HashMap<T,String>(buttonValueLabels);
        for (Entry<T, String> entry : this._buttonValueLabels.entrySet()) {
            final Button button;
            if (entry.getKey().equals(defaultValue)) {
                button = new SubmitButton(entry.getValue());
            }
            else {
                button = new Button(entry.getValue());
            }
            button.addStyleName("gwt-Button");
            this._buttonValues.put(button, entry.getKey());
            button.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    event.preventDefault();
                    resultEvent.dispatch(_buttonValues.get(button));
                }
            });
            this.buttonsPanel.add(button);
        }
        this.registerFormHandlers();
    }

    public HandlerRegistration addResultHandler(Handler<T> handler) {
        return this.resultEvent.addHandler(handler);
    }

    private void registerFormHandlers() {
        //NOTE: Due to a bug in GWT we need to manually handle the submit click otherwise
        //NOTE: it throws an exception that the gwt module might need to be recompiled.
        //NOTE: refer to http://code.google.com/p/google-web-toolkit/issues/detail?id=5067
        this.messageBoxForm.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
                resultEvent.dispatch(_defaultValue);
            }
        });
    }
    
    public static <T> AsyncFunc<Void, T> getShowFunc(final String title, final String content, final Map<T, String> buttonValueLabels, final T defaultValue)
    {
        return new AsyncFunc<Void, T>() {
            @Override protected <S, E> void run(Void arg, final Func<T, S> successHandler, Func<Throwable, E> errorHandler) 
            {
                try {
                    final DialogWithZIndex dialog = new DialogWithZIndex(false, true);
                    final MessageBox<T> messageBox = new MessageBox<T>(buttonValueLabels, defaultValue);
                    dialog.add(messageBox);
                    messageBox.labelTitle.setText(title);
                    messageBox.labelContent.setText(content);
                    messageBox.addResultHandler(new Handler<T>(){
                        @Override public void onFire(T arg) {
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
