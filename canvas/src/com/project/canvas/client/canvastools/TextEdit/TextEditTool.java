package com.project.canvas.client.canvastools.TextEdit;


import com.axeiya.gwtckeditor.client.CKConfig;
import com.axeiya.gwtckeditor.client.CKConfig.TOOLBAR_OPTIONS;
import com.axeiya.gwtckeditor.client.CKEditor;
import com.axeiya.gwtckeditor.client.Toolbar;
import com.axeiya.gwtckeditor.client.ToolbarLine;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementWrapper;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> {
	private final CKEditor editBox;
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private TextData data = new TextData();
	private int index;
	private boolean initialUnhideToolbars;
	
	private static final CKConfig editBoxConfig = new CKConfig();
	private static final Toolbar toolbar = new Toolbar();
	private static final ToolbarLine toolBarLine = new ToolbarLine(CKConfig.LINE_TYPE.NORMAL);
	
	public static void ensureResourcesLoaded() {
		final TextEditTool tool = new TextEditTool();
		tool.addStyleName(CanvasResources.INSTANCE.main().outOfBounds());
		tool.editBox.addInitializeHandler(new InitializeHandler() {
			@Override
			public void onInitialize(InitializeEvent event) {
				RootPanel.get().remove(tool);
			}
		});
		RootPanel.get().add(tool);
	}
	
	private static boolean configInited = false;
	private static void initConfig() {
		if (configInited) {
			return;
		}
		configInited = true;
		toolBarLine.addAll(new TOOLBAR_OPTIONS[] {
				TOOLBAR_OPTIONS.Bold,
				TOOLBAR_OPTIONS.Italic,
				TOOLBAR_OPTIONS.Underline,
				TOOLBAR_OPTIONS.Font,
				TOOLBAR_OPTIONS.FontSize,
				TOOLBAR_OPTIONS.TextColor,
				TOOLBAR_OPTIONS.BGColor,
				TOOLBAR_OPTIONS.Smiley,
				TOOLBAR_OPTIONS._,
				TOOLBAR_OPTIONS.Link,
				TOOLBAR_OPTIONS.Unlink,
				TOOLBAR_OPTIONS._,
				TOOLBAR_OPTIONS.NumberedList,
				TOOLBAR_OPTIONS.BulletedList,
				TOOLBAR_OPTIONS.Indent,
				TOOLBAR_OPTIONS._,
				TOOLBAR_OPTIONS.JustifyLeft,
				TOOLBAR_OPTIONS.JustifyCenter,
				TOOLBAR_OPTIONS.JustifyRight,
				TOOLBAR_OPTIONS.JustifyBlock,
				TOOLBAR_OPTIONS._,
				TOOLBAR_OPTIONS.RemoveFormat,
		});

		toolbar.add(toolBarLine);
		editBoxConfig.setToolbar(toolbar);
		editBoxConfig.setRemovePlugins("elementspath,scayt,menubutton,contextmenu,showborders");
		editBoxConfig.setExtraPlugins("autogrow");
		editBoxConfig.setResizeEnabled(false);
		editBoxConfig.setAutoGrowMinHeight(10);
		editBoxConfig.setAutoGrowMaxWidth(0);
		editBoxConfig.setToolbarLocation("bottom");
	}
	
	static {
		ensureResourcesLoaded();
	}
	
	public TextEditTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		
		initConfig();
		this.editBox = new CKEditor(editBoxConfig);
		this.editBox.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		// Hide toolbars until the user starts typing or clicks
		this.addStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
		this.initialUnhideToolbars = false;
		registerHandlers();
		this.add(editBox);
	}

	private void registerHandlers() {
		this.editBox.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateSizeFromEditor();
			}
		});
		this.editBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				setSelfFocus(true);
			}
		});
		this.editBox.addInitializeHandler(new InitializeHandler() {
			@Override
			public void onInitialize(InitializeEvent event) {
				editBox.setHTML("");
				editBox.resize(getOffsetWidth(), getOffsetHeight(), false, true);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						registerInitialUnhideToolbars();
						editBox.getElement().getStyle().setVisibility(Visibility.VISIBLE);
					}
				});
			}
		});
	}

	public void registerInitialUnhideToolbars() {
		final RegistrationsManager showToolbarsRegs = new RegistrationsManager();
		ElementWrapper wrappedInnerEditor = new ElementWrapper(this.editBox.getEditorElement()); 
		showToolbarsRegs.add(this.editBox.addKeyDownHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				showToolbars();
				showToolbarsRegs.clear();
			}}));
		showToolbarsRegs.add(this.addDomHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				showToolbars();
				showToolbarsRegs.clear();
			}}, ClickEvent.getType()));
		showToolbarsRegs.add(wrappedInnerEditor.addDomHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				showToolbars();
				showToolbarsRegs.clear();
			}}, ClickEvent.getType()));
	}

	@Override
	public void setActive(final boolean isActive) {
		setSelfFocus(isActive);
		this.editBox.setFocus(isActive);
	}

	private void setSelfFocus(boolean isFocused) {
		
		if (isFocused) {
			this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
			this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
			if (initialUnhideToolbars) {
				this.removeStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
			}
		}
		else {
			this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
			this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
			this.addStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
			checkNeedsKilling();
		}
		this.updateSizeFromEditor();
	}

	public void checkNeedsKilling() {
		if (this.editBox.isEditorAttached()) {
			// use getText rather than getHTML, so that if
			// there is no text in the box - it will be destroyed
			HTML editorHTML = new HTML(editBox.getHTML());
			String text = editorHTML.getText().trim();
			text = text.replace(new String(new char[]{(char)160}), "");
			
			// Call resetSelection LAST because it makes the getHTML return wrong results.
			//this.editBox.resetSelection();
			if (text.isEmpty()) {
				killRequestEvent.dispatch("Empty");
			}
		}
		else {
			killRequestEvent.dispatch("Empty");
		}
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestEvent;
	}

	public int getTabIndex() {
		return this.index;
	}

	public void setAccessKey(char key) {
	}

	public void setTabIndex(int index) {
		this.index = index;
	}

	@Override
	public TextData getValue() {
		this.data._text = this.editBox.getHTML();
		return this.data;
	}

	@Override
	public void setValue(TextData data) {
		this.data = data;
		this.editBox.setHTML(this.data._text);
		this.updateSizeFromEditor();
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((TextData) data);
	}

	public void updateSizeFromEditor() {
		this.setWidth(editBox.getOffsetWidth() + "px");
		this.setHeight(editBox.getOffsetHeight() + "px");
	}

	public void showToolbars() {
		this.removeStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
	}
}
