package com.project.website.canvas.client.canvastools.map;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.gwtmapstraction.client.mxn.LatLonPoint;
import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.gwtmapstraction.client.mxn.Mapstraction;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.events.SingleEvent;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.shared.data.Location;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.StringUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.MapData;

public class MapTool extends Composite implements CanvasTool<MapData> {
	private static final double DEFAULT_MAP_LONGITUDE = 0;
	private static final double DEFAULT_MAP_LATITUDE = 0;
	private static final int DEFAULT_MAP_ZOOM = 1;
	private static final MapProvider DEFAULT_MAP_PROVIDER = MapProvider.GOOGLE_V3;

	interface MapToolUiBinder extends UiBinder<Widget, MapTool> {
	}

	private static MapToolUiBinder uiBinder = GWT.create(MapToolUiBinder.class);

	@UiField
	FlowPanel mainPanel;
	@UiField
	FlowPanel mapPanel;
	@UiField
	Button optionsLabel;
	@UiField
	FlowPanel optionsBar;

	private final SimpleEvent<MouseEvent<?>> moveStartEvent = new SimpleEvent<MouseEvent<?>>();
	private final RegistrationsManager registrationsManager = new RegistrationsManager();
	private DialogBox optionsDialog;
	private MapToolOptions mapToolOptionsWidget;
	private HashMap<MapProvider, Widget> mapWidgets = new HashMap<MapProvider, Widget>();

	private Mapstraction mapstraction = null;
	private MapData mapData = null;
	private final SingleEvent<Void> asyncInitCompleted = new SingleEvent<Void>();

	public MapTool() {
		initWidget(uiBinder.createAndBindUi(this));
		CanvasToolCommon.initCanvasToolWidget(this);

		this.addStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());

		this.optionsLabel.addStyleName(CanvasResources.INSTANCE.main().disabledLink());
		this.getApiLoadedAndAttachedAsyncFunc()
			.then(getEnsureSetupProviderAsyncFunc(DEFAULT_MAP_PROVIDER))
			.then(new Func.VoidAction() {
				@Override
				public void exec() {
					asyncInitCompleted.dispatch(null);
				}})
			.run(null);
	}

	private AsyncFunc<Void, Void> getApiLoadedAndAttachedAsyncFunc() {
		return MapToolStaticUtils.getLoadMapScriptsAsyncFunc()
			.then(WidgetUtils.getOnAttachAsyncFunc(this));
	}

	@Override
	public HandlerRegistration addKillRequestEventHandler(Handler<String> handler) {
		return null;
	}

	@Override
	public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
		return this.moveStartEvent.addHandler(handler);
	}

	@Override
	public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
		return null;
	}

	@Override
	public void bind() {
		this.registrationsManager.add(this.optionsLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOptions();
			}
		}));
        registrationsManager.add(this.optionsBar.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.isControlKeyDown()) {
                    moveStartEvent.dispatch(event);
                }
            }
        }, MouseDownEvent.getType()));
	}

	@Override
	public boolean canRotate() {
		return true;
	}

	@Override
	public ResizeMode getResizeMode() {
		return ResizeMode.BOTH;
	}

	@Override
	public MapData getValue() {
		if (this.isReady()) {
			LatLonPoint center = this.mapstraction.getCenter();
			this.mapData.center = new Location();
			this.mapData.center.latitude = center.getLat();
			this.mapData.center.longitude = center.getLon();
			this.mapData.zoom = this.mapstraction.getZoom();
			this.mapData.mapType = MapToolStaticUtils.fromMapstractionMapType(this.mapstraction.getMapType());
		}
		return this.mapData;
	}

	@Override
	public void setActive(boolean isActive) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((MapData) data);
	}

	@Override
	public void setValue(MapData value) {
		this.mapData = value;
		this.applyMapDataToWidget();
	}

	@Override
	public void setViewMode(boolean isViewMode) {
		this.optionsLabel.setVisible(false == isViewMode);
	}

	@Override
	public void onResize() {
		// widget may be null if api not loaded yet
		if (this.isReady()) {
			this.updateMapSize();
		}
	}

	private void applyMapDataToWidget() {
		final MapProvider provider = this.getCurrentSelectedProvider();
		// apply the data to the widget only after the async init has finished.
		this.asyncInitCompleted.addHandler(HandlerUtils.fromAsyncFunc(
				this.getEnsureSetupProviderAsyncFunc(provider)
					.then(new Func.VoidAction() {
						@Override
						public void exec() {
							actualApplyMapDataToWidget(provider, getWidgetForProvider(provider));
						}})
		));

	}

	private void actualApplyMapDataToWidget(MapProvider provider, Widget mapWidget) {
		if (null == this.mapData) {
			return;
		}
		Logger.log("Applying map data to widget: " + provider.getApiString() + " -> " + mapWidget.getElement().getId());
		if (null == this.mapData.center) {
			this.mapData.center = new Location();
			this.mapData.center.latitude = DEFAULT_MAP_LATITUDE;
			this.mapData.center.longitude = DEFAULT_MAP_LONGITUDE;
			this.mapData.zoom = DEFAULT_MAP_ZOOM;
		}
		this.removeStyleName(CanvasResources.INSTANCE.main().mapToolEmpty());
		// we MUST first swap the api, because some functions are not
		// implemented by all APIS
		this.mapstraction.swap(provider, mapWidget.getElement());
		this.mapstraction.setCenter(LatLonPoint.create(this.mapData.center.latitude, this.mapData.center.longitude));
		this.mapstraction.setZoom(this.mapData.zoom);
		this.mapstraction.setMapType(MapToolStaticUtils.fromMapType(this.mapData.mapType));
		mapWidget.setVisible(true);
		this.updateMapSize();
	}

	private AsyncFunc<Void,Void> getEnsureSetupProviderAsyncFunc(final MapProvider provider) {
		Widget mapWidget = getWidgetForProvider(provider);
		if (null == mapWidget) {
			mapWidget = this.createWidgetForProvider(provider);
		}
		return WidgetUtils.getOnAttachAsyncFunc(mapWidget)
			.then(new Func.VoidAction() {
				@Override
				public void exec() {
					ensureCreatedMapstractionInstance(getWidgetForProvider(provider), provider);
				}});
	}

	private void ensureCreatedMapstractionInstance(Widget mapWidget, MapProvider provider) {
		if (null != this.mapstraction) {
			return;
		}
		this.mapstraction = Mapstraction.createInstance(mapWidget.getElement(), provider, true);
		this.mapstraction.setDebug(true);
		this.mapstraction.addSmallControls();
		this.mapstraction.enableScrollWheelZoom();
		this.mapstraction.setCenter(LatLonPoint.create(DEFAULT_MAP_LATITUDE, DEFAULT_MAP_LONGITUDE));
		this.mapstraction.setZoom(DEFAULT_MAP_ZOOM);
		this.optionsLabel.removeStyleName(CanvasResources.INSTANCE.main().disabledLink());
	}

	private Widget getWidgetForProvider(MapProvider provider) {
		return this.mapWidgets.get(provider);
	}

	private boolean isReady() {
		return (null != this.mapData) && this.isAttached() && MapToolStaticUtils.isApiLoaded() && (null != this.mapstraction);
	}

	private void updateMapSize() {
		Point2D widgetSize = ElementUtils.getElementClientSize(this.getWidgetForProvider(getCurrentSelectedProvider()).getElement());
		this.mapstraction.resizeTo(widgetSize.getX(), widgetSize.getY());
	}

	private MapProvider getCurrentSelectedProvider() {
		if (StringUtils.isWhitespaceOrNull(this.mapData.provider)) {
			return DEFAULT_MAP_PROVIDER;
		}
		try {
			return MapProvider.valueOf(this.mapData.provider);
		} catch (IllegalArgumentException e) {
			return DEFAULT_MAP_PROVIDER;
		}
	}

	private Widget createWidgetForProvider(final MapProvider provider) {
		Widget mapWidget = getWidgetForProvider(provider);
		if (null != mapWidget) {
			return mapWidget;
		}
		mapWidget = new FlowPanel();
		// For mapstraction to work properly, must set the element id
		mapWidget.getElement().setId("mw_" + provider.ordinal() + "_" + String.valueOf(Random.nextInt()));
		mapWidget.addStyleName(CanvasResources.INSTANCE.main().mapToolMapWidget());
		this.mapWidgets.put(provider, mapWidget);
		this.mapPanel.add(mapWidget);
		return mapWidget;
	}

	protected void showOptions()
	{
		if (false == this.isReady()) {
			return;
		}
		if (null == this.optionsDialog) {
			this.optionsDialog = new DialogWithZIndex(false, true);
			this.optionsDialog.setText("Map options");
		}
		if (null == this.mapToolOptionsWidget) {
			this.mapToolOptionsWidget = new MapToolOptions(MapToolStaticUtils.AVAILABLE_PROVIDERS);
			this.mapToolOptionsWidget.addDoneHandler(new Handler<Void>() {
				@Override
				public void onFire(Void arg) {
					optionsDialog.hide();
					setValue(mapToolOptionsWidget.getValue());
				}
			});
			this.optionsDialog.add(this.mapToolOptionsWidget);
			this.mapToolOptionsWidget.addValueChangeHandler(new ValueChangeHandler<MapData>() {
				@Override
				public void onValueChange(ValueChangeEvent<MapData> event) {
					setValue(event.getValue());
				}
			});
		}
		this.mapToolOptionsWidget.setValue(this.getValue());
		this.optionsDialog.center();
	}
}
