package com.project.canvas.client.shared.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.NativeUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageInfo;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageResult;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchProvider;
import com.project.canvas.client.shared.searchProviders.interfaces.ImageSearchResult;
import com.project.canvas.client.shared.widgets.RadioButtonPanel;

public class ImagePicker extends Composite {

    private static ImagePickerUiBinder uiBinder = GWT.create(ImagePickerUiBinder.class);

    interface ImagePickerUiBinder extends UiBinder<Widget, ImagePicker> {
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

    protected final SimpleEvent<ImageInfo> imagePicked = new SimpleEvent<ImageInfo>();

    protected final RegistrationsManager registrationsManager = new RegistrationsManager();
    private ImageSearchProvider _selectedSearchProvider = null; 

    private InlineLabel selectedImage;
    private HashMap<ImageSearchProvider, RadioButtonPanel> _searchProviderMap = new HashMap<ImageSearchProvider, RadioButtonPanel>();

    public ImagePicker() 
    {
        initWidget(uiBinder.createAndBindUi(this));
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
    
    public void setSearchProviders(List<ImageSearchProvider> searchProviders)
    {
        if (0 == searchProviders.size())
        {
            return;
        }
        for (final ImageSearchProvider searchProvider : searchProviders)
        {
            addProvider(searchProvider);
        }
        this._selectedSearchProvider = searchProviders.get(0);
        this._searchProviderMap.get(this._selectedSearchProvider).getRadioButton().setValue(true);
    }

    public void addProvider(final ImageSearchProvider searchProvider)
    {
        RadioButtonPanel radioButtonPanel = new RadioButtonPanel();
        radioButtonPanel.addStyleName(
                CanvasResources.INSTANCE.main().imageToolSearchProviderPanelStyle());
        radioButtonPanel.setName("searchProviders");
        FlowPanel imagePanel = new FlowPanel();
        imagePanel.addStyleName(
                CanvasResources.INSTANCE.main().imageToolSearchProviderIconStyle());
        imagePanel.getElement().getStyle()
            .setBackgroundImage("url(" + searchProvider.getIconUrl() + ")");
        radioButtonPanel.add(imagePanel);
        radioButtonPanel.add(new InlineLabel(searchProvider.getTitle()));
        
        radioButtonPanel.getRadioButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (false == event.getValue())
                {
                    return;
                }
                _selectedSearchProvider = searchProvider; 
            }
        });
        this.providersPanel.add(radioButtonPanel);
        this._searchProviderMap.put(searchProvider, radioButtonPanel);
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
        this._selectedSearchProvider.search(text, new AsyncCallback<ImageSearchResult>() {
            @Override
            public void onSuccess(ImageSearchResult result) {
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

    protected void setSearchResult(ImageSearchResult result) 
    {
        registrationsManager.clear();
        resultsPanel.clear();
        resultsPanelContainer.scrollToTop();

        for (ImageResult imageResult : result.getImageResults()) 
        {
            Widget imageWidget = createPhoto(imageResult);
            if (null != imageWidget) {
                resultsPanel.add(imageWidget);
            }
        }
    }

    public Widget createPhoto(final ImageResult imageResult) {
        if (null == imageResult) {
            return null;
        }
        final InlineLabel image = new InlineLabel();
        image.setTitle(imageResult.getTitle());
        image.addStyleName(CanvasResources.INSTANCE.main().imagePickerResultImage());
        image.getElement().getStyle()
                .setBackgroundImage("url(" + imageResult.getThumbnailUrl() + ")");
        this.registrationsManager.add(image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                imageSelected(imageResult, image);
            }
        }));
        return image;
    }

    public HandlerRegistration addImagePickedHandler(SimpleEvent.Handler<ImageInfo> handler) {
        return this.imagePicked.addHandler(handler);
    }

    public void imageSelected(final ImageResult imageResult, final InlineLabel image) {
        if (null != selectedImage) {
            selectedImage.removeStyleName(CanvasResources.INSTANCE.main().selected());
        }
        this.selectedImage = image;
        image.addStyleName(CanvasResources.INSTANCE.main().selected());
        
        photoSizesPanel.clear();
        imageResult.getImageSizes(new AsyncCallback<ArrayList<ImageInfo>>() {
            @Override
            public void onSuccess(ArrayList<ImageInfo> result) {
                setPhotoSizes(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
            }
        });
    }

    protected void setPhotoSizes(ArrayList<ImageInfo> result) 
    {
        final HashMap<RadioButton, ImageInfo> selectionMap = new HashMap<RadioButton, ImageInfo>();
        photoSizesPanel.clear();
        int defaultSelectionIndex = result.size() / 2;
        int i = 0;
        for (final ImageInfo imageInfo : result) {
            String sizeStr = imageInfo.getWidth() + " x " + imageInfo.getHeight();
            RadioButton radioButton = new RadioButton("sizes", sizeStr);
            selectionMap.put(radioButton, imageInfo);
            photoSizesPanel.add(radioButton);
            radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        imageSizeSelected(imageInfo);
                    }
                }
            });
        	if (i == defaultSelectionIndex) {
        		radioButton.setValue(true);
                imageSizeSelected(imageInfo);
        	}
        	i++;
        }
        photoSizesPanel.setVisible(true);
    }

    public void imageSizeSelected(final ImageInfo selectedSize) {
        imagePicked.dispatch(selectedSize);
    }
}
