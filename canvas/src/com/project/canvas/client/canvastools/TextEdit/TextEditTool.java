package com.project.canvas.client.canvastools.TextEdit;


import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> {
	//private final CKEditor editBox;
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private TextData data = new TextData();
	private final TextArea textArea = new TextArea();
	private int index;
	
//	private static final CKConfig editBoxConfig = new CKConfig();
//	private static final Toolbar toolbar = new Toolbar();
//	private static final ToolbarLine[] toolBarLines = new ToolbarLine[] {
//		new ToolbarLine(CKConfig.LINE_TYPE.NORMAL),
//		new ToolbarLine(CKConfig.LINE_TYPE.NORMAL)
//	};
//	
//	public static void ensureResourcesLoaded() {
//		final TextEditTool tool = new TextEditTool();
//		tool.addStyleName(CanvasResources.INSTANCE.main().outOfBounds());
//		tool.editBox.addInitializeHandler(new InitializeHandler() {
//			@Override
//			public void onInitialize(InitializeEvent event) {
//				RootPanel.get().remove(tool);
//			}
//		});
//		RootPanel.get().add(tool);
//	}
//	
//	private static boolean configInited = false;
//	private static void initConfig() {
//		if (configInited) {
//			return;
//		}
//		configInited = true;
//		toolBarLines[0].addAll(new TOOLBAR_OPTIONS[] {
//				TOOLBAR_OPTIONS.Bold,
//				TOOLBAR_OPTIONS.Italic,
//				TOOLBAR_OPTIONS.Underline,
//				TOOLBAR_OPTIONS.Font,
//				TOOLBAR_OPTIONS.FontSize,
//				TOOLBAR_OPTIONS.TextColor,
//				TOOLBAR_OPTIONS.BGColor,
//		});
//		toolBarLines[1].addAll(new TOOLBAR_OPTIONS[] {
//				TOOLBAR_OPTIONS.Link,
//				TOOLBAR_OPTIONS.Unlink,
//				TOOLBAR_OPTIONS._,
//				TOOLBAR_OPTIONS.NumberedList,
//				TOOLBAR_OPTIONS.BulletedList,
//				TOOLBAR_OPTIONS.Indent,
//				TOOLBAR_OPTIONS._,
//				TOOLBAR_OPTIONS.JustifyLeft,
//				TOOLBAR_OPTIONS.JustifyCenter,
//				TOOLBAR_OPTIONS.JustifyRight,
//				TOOLBAR_OPTIONS.JustifyBlock,
//				TOOLBAR_OPTIONS._,
//				TOOLBAR_OPTIONS.RemoveFormat,
//		});
//
//		toolbar.add(toolBarLines[0]);
//		toolbar.add(toolBarLines[1]);
//		editBoxConfig.setToolbar(toolbar);
//		editBoxConfig.setRemovePlugins("elementspath,scayt,menubutton,contextmenu,showborders");
//		//editBoxConfig.setExtraPlugins("autogrow");
//		editBoxConfig.setResizeEnabled(false);
//		editBoxConfig.setHeight("39px");
//		//editBoxConfig.setAutoGrowMinHeight(10);
//		editBoxConfig.setToolbarLocation("bottom");
//		editBoxConfig.setFocusOnStartup(true);
//	}
//	
//	static {
//		ensureResourcesLoaded();
//	}
	
	public TextEditTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.add(textArea);
		this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		
//		initConfig();
//		this.editBox = new CKEditor(editBoxConfig);
//		this.editBox.getElement().getStyle().setDisplay(Display.NONE);
//		this.add(editBox);
		this.replaceWithNicEdit();
	}
	
	private final native void replaceWithNicEdit() /*-{
//		window.nicEditors.allTextAreas();
	}-*/;
	
	@Override
	public void bind()
	{
//		registerHandlers();
	}

//	private void registerHandlers() {
//		this.editBox.addKeyDownHandler(new KeyDownHandler() {
//			@Override
//			public void onKeyDown(KeyDownEvent event) {
//				String data = editBox.getHTML();
//				Point2D newSize = TextEditUtils.autoSizeWidget(
//						new ElementWrapper(editBox.getEditorElement()), data, false);
//				editBox.resize(newSize.getX(), newSize.getY(), true, true);
//				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//					@Override
//					public void execute() {
//						updateSizeFromEditor();
//					}
//				});
//			}
//		});
//		this.editBox.addResizeHandler(new ResizeHandler() {
//			@Override
//			public void onResize(ResizeEvent event) {
//				updateSizeFromEditor();
//			}
//		});
//		this.editBox.addFocusHandler(new FocusHandler() {
//			@Override
//			public void onFocus(FocusEvent event) {
//				setSelfFocus(true);
//			}
//		});
//		this.editBox.addInitializeHandler(new InitializeHandler() {
//			@Override
//			public void onInitialize(InitializeEvent event) {
//				editBox.setHTML("");
//				editBox.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//					@Override
//					public void execute() {
//						updateSizeFromEditor();
//					}
//				});
//			}
//		});
//	}
//	
	@Override
	public void setActive(final boolean isActive) {
//		setSelfFocus(isActive);
//		this.editBox.setFocus(isActive);
	}
//
//	private void setSelfFocus(boolean isFocused) {
//		
//		if (isFocused) {
//			this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
//			this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
//			this.removeStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
//		}
//		else {
//			this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
//			this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
//			this.addStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
//			checkNeedsKilling();
//		}
//		this.updateSizeFromEditor();
//	}
//
//	public void checkNeedsKilling() {
//		if (this.editBox.isEditorAttached()) {
//			HTML editorHTML = new HTML(editBox.getHTML());
//			String text = editorHTML.getText().trim();
//			text = text.replace(new String(new char[]{(char)160}), "");
//			
//			// Call resetSelection LAST because it makes the getHTML return wrong results.
//			//this.editBox.resetSelection();
//			if (text.isEmpty()) {
//				killRequestEvent.dispatch("Empty");
//			}
//		}
//		else {
//			killRequestEvent.dispatch("Empty");
//		}
//	}

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
//		this.data._text = this.editBox.getHTML();
		return this.data;
	}

	@Override
	public void setValue(TextData data) {
		this.data = data;
		//this.editBox.setHTML(this.data._text);
		
//		this.updateSizeFromEditor();
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((TextData) data);
	}

//	public void updateSizeFromEditor() {
//		Element elem = editBox.getEditorElement();
//		if (null == elem){
//			return;
//		}
//		ElementWrapper wrappedEditor = new ElementWrapper(elem);
//		this.setWidth(wrappedEditor.getOffsetWidth() + "px");
//		this.setHeight(wrappedEditor.getOffsetHeight() + "px");
//	}
//
//	public void showToolbars() {
//		this.removeStyleName(CanvasResources.INSTANCE.main().textEditNoToolbars());
//	}
	
	@Override
	public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
		return null;
	}
}
