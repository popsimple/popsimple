package com.tutorials.hellosomeone.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainWidget extends Composite {

	private static MainWidgetUiBinder uiBinder = GWT
			.create(MainWidgetUiBinder.class);

	interface MainWidgetUiBinder extends UiBinder<Widget, MainWidget> {
	}
	
	@UiField 
	TextBox textName;
	
	@UiField
	InlineLabel labelName;
	
	@UiField 
	Button btnApply;
	
	@UiField
	HTMLPanel rootPanel;
	
	boolean isClicked = false;

	ImageProvider imageProvider = new ImageProvider();
		
	public MainWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		rootPanel.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				OnRootClick(event);
			}
		}, ClickEvent.getType());
						
		btnApply.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				labelName.setText(textName.getText());
			}
		});
	}
	
	
	
	private void OnRootClick(ClickEvent event)
	{
		if (event.isControlKeyDown())
		{
			this.OnClickText(event);
		}
		else
		{
			this.OnClickTaskList(event);
		}
	}
	
	private void OnClickTaskList(ClickEvent event)
	{
	}
	
	private void OnClickText(ClickEvent event)
	{
		final TextBox textBox = new TextBox();
		textBox.addStyleName("ClickWriteBox");
		textBox.getElement().getStyle().setTop(event.getClientY(), Unit.PX);
		textBox.getElement().getStyle().setLeft(event.getClientX(), Unit.PX);
		textBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// TODO Auto-generated method stub
				if ('~' != event.getCharCode())
				{
					return;
				}
				CreateLabel(textBox);
			}
		});
		
		textBox.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				// TODO Auto-generated method stub
				CreateLabel(textBox);
			}
		});
		
		this.rootPanel.add(textBox);
		textBox.setFocus(true);
	}
	
	private void CreateLabel(TextBox textBox)
	{
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("ClickPanel");
		panel.getElement().getStyle().setTop(textBox.getAbsoluteTop() , Unit.PX);
		panel.getElement().getStyle().setLeft(textBox.getAbsoluteLeft(), Unit.PX);
		
		Image image = new Image(this.imageProvider.GetImageUrl(textBox.getText()));
		image.addStyleName("ClickImage");
		panel.add(image);
		
		InlineLabel label = new InlineLabel(textBox.getText());
		label.addStyleName("ClickLabel");
		panel.add(label);
		
		this.rootPanel.remove(textBox);
		this.rootPanel.add(panel);
	}
}
