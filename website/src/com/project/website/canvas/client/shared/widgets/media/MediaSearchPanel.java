package com.project.website.canvas.client.shared.widgets.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.NativeUtils;
import com.project.shared.client.utils.widgets.TextBoxUtils;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaInfo;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaResult;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchProvider;
import com.project.website.canvas.client.shared.searchProviders.interfaces.MediaSearchResult;
import com.project.website.canvas.client.shared.widgets.RadioButtonPanel;

public class MediaSearchPanel extends Composite implements Focusable {

    private static MediaSearchPanelUiBinder uiBinder = GWT.create(MediaSearchPanelUiBinder.class);

    interface MediaSearchPanelUiBinder extends UiBinder<Widget, MediaSearchPanel> {
    }

    @UiField
    Button searchButton;

    @UiField
    TextBox searchText;

    @UiField
    FlowPanel resultsPanel;

    @UiField
    ScrollPanel resultsPanelContainer;

    @UiField
    FlowPanel formPanel;

    @UiField
    FlowPanel photoSizesPanel;

    @UiField
    FlowPanel providersPanel;

    protected final SimpleEvent<MediaInfo> mediaPicked = new SimpleEvent<MediaInfo>();

    protected final RegistrationsManager registrationsManager = new RegistrationsManager();
    private MediaSearchProvider _selectedSearchProvider = null;

    private Image selectedThumbnail;
    private HashMap<MediaSearchProvider, RadioButtonPanel> _searchProviderMap = new HashMap<MediaSearchProvider, RadioButtonPanel>();
    private HashMap<RadioButton, MediaInfo> _sizeSelectionMap = new HashMap<RadioButton, MediaInfo>();

    public MediaSearchPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));

        TextBoxUtils.setPlaceHolder(this.searchText, "Search...");

        this.searchText.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (NativeUtils.keyIsEnter(event)) {
                    searchSubmitted();
                }
            }
        });
    }

    public void setSearchProviders(List<? extends MediaSearchProvider> searchProviders)
    {
        if (0 == searchProviders.size())
        {
            return;
        }
        boolean allowSelect = (1 < searchProviders.size());
        for (final MediaSearchProvider searchProvider : searchProviders)
        {
            addProvider(searchProvider, allowSelect);
        }

        this._selectedSearchProvider = searchProviders.get(0);

        if (allowSelect) {
            this._searchProviderMap.get(this._selectedSearchProvider).setValue(true);
        }
    }

    public void addProvider(final MediaSearchProvider searchProvider, boolean allowSelect)
    {
        HasWidgets providerPanel;
        Widget providerPanelWidget;
        if (allowSelect) {
            final RadioButtonPanel radioButtonPanel = new RadioButtonPanel();
            radioButtonPanel.setName("searchProviders");
            radioButtonPanel.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    if (false == radioButtonPanel.getValue())
                    {
                        return;
                    }
                    _selectedSearchProvider = searchProvider;
                }
            });
            this._searchProviderMap.put(searchProvider, radioButtonPanel);

            providerPanel = radioButtonPanel;
            providerPanelWidget = radioButtonPanel;
        }
        else {
            FlowPanel flowPanel = new FlowPanel();
            providerPanel = flowPanel;
            providerPanelWidget = flowPanel;
        }
        providerPanelWidget.addStyleName(
                CanvasResources.INSTANCE.main().imageToolSearchProviderPanelStyle());
        FlowPanel imagePanel = new FlowPanel();
        imagePanel.addStyleName(
                CanvasResources.INSTANCE.main().imageToolSearchProviderIconStyle());
        imagePanel.getElement().getStyle()
            .setBackgroundImage("url(" + searchProvider.getIconUrl() + ")");
        providerPanel.add(imagePanel);
        providerPanel.add(new InlineLabel(searchProvider.getTitle()));

        this.providersPanel.add(providerPanelWidget);
    }

    @UiHandler("searchButton")
    void handleClick(ClickEvent e) {
        searchSubmitted();
    }

    private void searchSubmitted()
    {
        String text = this.searchText.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        if (null == this._selectedSearchProvider)
        {
            return;
        }
        searchButton.setEnabled(false);
        this._selectedSearchProvider.search(text, new AsyncCallback<MediaSearchResult>() {
            @Override
            public void onSuccess(MediaSearchResult result) {
                setSearchResult(result);
                searchButton.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Search failed: " + caught);
                searchButton.setEnabled(true);
            }
        });
    }

    protected void setSearchResult(MediaSearchResult result)
    {
        registrationsManager.clear();
        resultsPanel.clear();
        resultsPanelContainer.scrollToTop();

        for (MediaResult imageResult : result.getMediaResults())
        {
            Widget imageWidget = createPhoto(imageResult);
            if (null != imageWidget) {
                resultsPanel.add(imageWidget);
            }
        }
    }

    public Widget createPhoto(final MediaResult imageResult) {
        if (null == imageResult) {
            return null;
        }
        final Image image = new Image();
        image.setTitle(imageResult.getTitle());
        image.addStyleName(CanvasResources.INSTANCE.main().mediaSearchPanelResultImage());
        image.setUrl(imageResult.getThumbnailUrl());
        this.registrationsManager.add(image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                imageSelected(imageResult, image);
            }
        }));
        return image;
    }

    public HandlerRegistration addMediaPickedHandler(SimpleEvent.Handler<MediaInfo> handler) {
        return this.mediaPicked.addHandler(handler);
    }

    public void imageSelected(final MediaResult imageResult, final Image image) {
        if (null != selectedThumbnail) {
            selectedThumbnail.removeStyleName(CanvasResources.INSTANCE.main().selected());
        }
        this.selectedThumbnail = image;
        image.addStyleName(CanvasResources.INSTANCE.main().selected());

        photoSizesPanel.clear();
        imageResult.getMediaSizes(new AsyncCallback<ArrayList<MediaInfo>>() {
            @Override
            public void onSuccess(ArrayList<MediaInfo> result) {
                setPhotoSizes(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
            }
        });
    }

    protected void setPhotoSizes(ArrayList<MediaInfo> result)
    {
        this._sizeSelectionMap.clear();
        photoSizesPanel.clear();
        int defaultSelectionIndex = result.size() / 2;
        int i = 0;
        for (final MediaInfo mediaInfo : result) {
            RadioButton radioButton = new RadioButton("sizes", mediaInfo.getSizeDescription());
            this._sizeSelectionMap.put(radioButton, mediaInfo);
            photoSizesPanel.add(radioButton);
            radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        imageSizeSelected(mediaInfo);
                    }
                }
            });
        	if (i == defaultSelectionIndex) {
        		radioButton.setValue(true);
                imageSizeSelected(mediaInfo);
        	}
        	i++;
        }
        photoSizesPanel.setVisible(true);
    }

    public void imageSizeSelected(final MediaInfo selectedSize) {
        mediaPicked.dispatch(selectedSize);
    }

    public MediaInfo getSelectedMedia()
    {
        for (Entry<RadioButton, MediaInfo> entry : this._sizeSelectionMap.entrySet())
        {
            if (entry.getKey().getValue())
            {
                return entry.getValue();
            }
        }
        return null;
    }

    public void clear()
    {
        //TODO: Set the "Search" text as watermark.
        this.searchText.setText("");
        resultsPanel.clear();
        resultsPanelContainer.scrollToTop();
        photoSizesPanel.clear();
    }

    @Override
    public int getTabIndex()
    {
        return this.searchText.getTabIndex();
    }

    @Override
    public void setAccessKey(char key)
    {
        this.searchText.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused)
    {
        this.searchText.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index)
    {
        this.searchText.setTabIndex(index);
    }
}
