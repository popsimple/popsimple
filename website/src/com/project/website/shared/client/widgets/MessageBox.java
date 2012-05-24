package com.project.website.shared.client.widgets;

import java.util.HashMap;

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
import com.project.shared.data.Pair;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.MapUtils;

// TODO create a shared styles package and move this to Shared project
public class MessageBox<T> extends Composite {

    private static MessageBoxUiBinder uiBinder = GWT.create(MessageBoxUiBinder.class);

    interface MessageBoxUiBinder extends UiBinder<Widget, MessageBox<?>> {
    }
    
    @UiField
    Label labelTitle;
    
    @UiField
    Label labelContent;
    
    @UiField
    FlowPanel buttonsPanel;
    
    @UiField
    FormPanel messageBoxForm;

    private final SimpleEvent<T> resultEvent = new SimpleEvent<T>();
    private final HashMap<Widget, T> _buttonValues = new HashMap<Widget, T>();
    private final T _defaultValue;
    private final Button _defaultButton;

    public MessageBox(Pair<T, String>[] buttonValueLabels, T defaultValue) {
        initWidget(uiBinder.createAndBindUi(this));

        Button defaultButton = null;
        this._defaultValue = defaultValue;
        MapUtils.create(buttonValueLabels);
        for (Pair<T, String> entry : buttonValueLabels) {
            final Button button;
            if (entry.getA().equals(defaultValue)) {
                button = new SubmitButton(entry.getB());
                defaultButton = button;
            }
            else {
                button = new Button(entry.getB());
            }
            button.addStyleName("gwt-Button");
            this._buttonValues.put(button, entry.getA());
            button.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    event.preventDefault();
                    resultEvent.dispatch(_buttonValues.get(button));
                }
            });
            this.buttonsPanel.add(button);
        }
        this._defaultButton = defaultButton;
        this.registerFormHandlers();
    }
    
    public void focusDefault()
    {
        this._defaultButton.setFocus(true);
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
    
    public static <T> AsyncFunc<Void, T> getShowFunc(final String title, final String content, final Pair<T, String>[] buttonValueLabels, final T defaultValue)
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
                    messageBox.focusDefault();
                }
                catch (Throwable e)
                {
                    errorHandler.apply(e);
                }
            }};
    }
}
