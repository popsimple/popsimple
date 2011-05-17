package com.project.canvas.client.shared.dialogs;

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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;

public class ImagePicker extends Composite {

	private static final String API_KEY = "023322961d08d84124ba870f1adce55b";
	
	private static ImagePickerUiBinder uiBinder = GWT
			.create(ImagePickerUiBinder.class);

	interface ImagePickerUiBinder extends UiBinder<Widget, ImagePicker> {
	}

	@UiField
	Button searchButton;
	
	@UiField
	TextBox searchText;

	@UiField
	HTMLPanel resultsPanel;
	
	protected final SimpleEvent<List<PhotoSizeResponse>> imagePicked = new SimpleEvent<List<PhotoSizeResponse>>();
	
	protected final Credentials credentials = new Credentials(API_KEY);
	protected final Search searcher = new Search(credentials);
	protected final GetSizes photoSizesGetter = new GetSizes(credentials);  

	protected PhotoSize searchResultPhotoSize = PhotoSize.THUMBNAIL;
	protected PhotoSize pickedImagePhotoSize = PhotoSize.ORIGINAL;

	private InlineLabel selectedImage; 
	
	public ImagePicker() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}

	@UiHandler("searchButton")
	void handleClick(ClickEvent e)
	{
		String text = this.searchText.getText().trim();
		if (text.isEmpty()) {
			return;
		}
		searcher.setText(text);
		searchButton.setEnabled(false);
		searcher.send(new AsyncCallback<PhotosResponse>() {
			@Override
			public void onSuccess(PhotosResponse result) {
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

	protected void setSearchResult(PhotosResponse result) {
		resultsPanel.clear();
		PhotosPage photosPage = result.getPhotosPage();
		for (int i = 0; i < photosPage.getPerPage(); i++) {
			Photo photo = photosPage.getPhotos().get(i);
			resultsPanel.add(createPhoto(photo));
		}
	}

	public Widget createPhoto(final Photo photo) {
		final InlineLabel image = new InlineLabel();
		image.addStyleName(CanvasResources.INSTANCE.main().imagePickerResultImage());
		image.getElement().getStyle().setBackgroundImage("url(" + photo.getSourceUrl(searchResultPhotoSize) + ")");
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				imageSelected(photo, image);
			}
		});
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

	public SimpleEvent<List<PhotoSizeResponse>> getImagePicked() {
		return imagePicked;
	}

	public void imageSelected(final Photo photo, final InlineLabel image) {
		if (null != selectedImage) {
			selectedImage.removeStyleName(CanvasResources.INSTANCE.main().selected());
		}
		this.selectedImage = image;
		image.addStyleName(CanvasResources.INSTANCE.main().selected());
		photoSizesGetter.setPhoto(photo);
		photoSizesGetter.send(new AsyncCallback<PhotoSizesResponse>() {
			@Override
			public void onSuccess(PhotoSizesResponse result) {
				imagePicked.dispatch(result.getSizes());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
}
