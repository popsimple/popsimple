package com.project.canvas.client.canvastools.Image.BingImage;

import java.util.HashMap;
import java.util.List;

import com.ghusse.dolomite.flickr.Credentials;
import com.ghusse.dolomite.flickr.Photo;
import com.ghusse.dolomite.flickr.PhotoSize;
import com.ghusse.dolomite.flickr.PhotoSizesResponse;
import com.ghusse.dolomite.flickr.PhotoSizesResponse.PhotoSizeResponse;
import com.ghusse.dolomite.flickr.PhotosPage;
import com.ghusse.dolomite.flickr.PhotosResponse;
import com.ghusse.dolomite.flickr.photos.GetSizes;
import com.ghusse.dolomite.flickr.photos.Search;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.Image.BingImage.BingSearch.BingSearchProvider;
import com.project.canvas.client.canvastools.Image.BingImage.BingSearch.ImageResponse;
import com.project.canvas.client.canvastools.Image.BingImage.BingSearch.ImageResult;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.Point2D;

public class BingImagePicker extends Composite {

    private static final String API_KEY = "68910216D550D46A50E65B86A92F0FC245EFE6B7";

    private static ImagePickerUiBinder uiBinder = GWT.create(ImagePickerUiBinder.class);

    interface ImagePickerUiBinder extends UiBinder<Widget, BingImagePicker> {
    }

    @UiField
    Button searchButton;

    @UiField
    TextBox searchText;

    @UiField
    HTMLPanel resultsPanel;

    @UiField
    FormPanel formPanel;

    @UiField
    FlowPanel photoSizesPanel;

    public class ImageInfo {
        public ImageInfo(String url, Point2D size) {
            this.url = url;
            this.size = size;
        }

        public final String url;
        public final Point2D size;
    }

    protected final SimpleEvent<ImageInfo> imagePicked = new SimpleEvent<ImageInfo>();

    protected final BingSearchProvider searcher = new BingSearchProvider(API_KEY);
    protected final RegistrationsManager registrationsManager = new RegistrationsManager();

    protected PhotoSize searchResultPhotoSize = PhotoSize.THUMBNAIL;
    protected PhotoSize pickedImagePhotoSize = PhotoSize.ORIGINAL;

    private InlineLabel selectedImage;

    public BingImagePicker() {
        initWidget(uiBinder.createAndBindUi(this));

    }

    @UiHandler("searchButton")
    void handleClick(ClickEvent e) 
    {
        String text = this.searchText.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        searchButton.setEnabled(false);
        searcher.searchImages(text, new AsyncCallback<ImageResponse>() {
            @Override
            public void onSuccess(ImageResponse result) {
                setSearchResult(result);
                searchButton.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Search failed: " + caught);
                searchButton.setEnabled(true);
            }
        });
        formPanel.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                event.cancel();
            }
        });
    }

    protected void setSearchResult(ImageResponse result) {
        registrationsManager.clear();
        resultsPanel.clear();

        JsArray<ImageResult> imageResults = result.getResults();
        for (int i = 0; i < imageResults.length(); i++) 
        {
            Widget imageWidget = createPhoto(imageResults.get(i));
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
        image.addStyleName(CanvasResources.INSTANCE.main().imagePickerResultImage());
        image.getElement().getStyle()
                .setBackgroundImage("url(" + imageResult.getThumbnail().getUrl() + ")");
//        this.registrationsManager.add(image.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                imageSelected(photo, image);
//            }
//        }));
        return image;
    }

    public PhotoSize getSearchResultPhotoSize() {
        return searchResultPhotoSize;
    }

    public void setSearchResultPhotoSize(PhotoSize searchResultPhotoSize) {
        this.searchResultPhotoSize = searchResultPhotoSize;
    }

    public PhotoSize getPickedImagePhotoSize() {
        return pickedImagePhotoSize;
    }

    public void setPickedImagePhotoSize(PhotoSize pickedImagePhotoSize) {
        this.pickedImagePhotoSize = pickedImagePhotoSize;
    }

    public HandlerRegistration addImagePickedHandler(SimpleEvent.Handler<ImageInfo> handler) {
        return this.imagePicked.addHandler(handler);
    }

    public void imageSelected(final Photo photo, final InlineLabel image) {
        if (null != selectedImage) {
            selectedImage.removeStyleName(CanvasResources.INSTANCE.main().selected());
        }
        this.selectedImage = image;
        image.addStyleName(CanvasResources.INSTANCE.main().selected());
        photoSizesPanel.clear();
//        photoSizesGetter.setPhoto(photo);
//        photoSizesGetter.send(new AsyncCallback<PhotoSizesResponse>() {
//            @Override
//            public void onSuccess(PhotoSizesResponse result) {
//                setPhotoSizes(result);
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                // TODO Auto-generated method stub
//            }
//        });
    }

    protected void setPhotoSizes(PhotoSizesResponse result) {
        final HashMap<RadioButton, PhotoSizeResponse> selectionMap = new HashMap<RadioButton, PhotoSizeResponse>();
        photoSizesPanel.clear();
        List<PhotoSizeResponse> sizes = result.getSizes();
        int defaultSelectionIndex = sizes.size() / 2;
        int i = 0;
        for (final PhotoSizeResponse size : sizes) {
            String sizeStr = size.getWidth() + " x " + size.getHeight();
            RadioButton radioButton = new RadioButton("sizes", sizeStr);
            selectionMap.put(radioButton, size);
            photoSizesPanel.add(radioButton);
            radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        imageSizeSelected(size);
                    }
                }
            });
        	if (i == defaultSelectionIndex) {
        		radioButton.setValue(true);
                imageSizeSelected(size);
        	}
        	i++;
        }
        photoSizesPanel.setVisible(true);
    }

    public void imageSizeSelected(final PhotoSizeResponse selectedSize) {
        imagePicked.dispatch(new ImageInfo(selectedSize.getSource(), new Point2D(Integer.valueOf(selectedSize
                .getWidth()), Integer.valueOf(selectedSize.getHeight()))));
    }
}
