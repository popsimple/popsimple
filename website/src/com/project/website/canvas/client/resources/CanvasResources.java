package com.project.website.canvas.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface CanvasResources extends ClientBundle {
    public static final CanvasResources INSTANCE = GWT.create(CanvasResources.class);

    @Source("main.css")
    MainStyles main();

    @Source("com/project/website/shared/client/resources/images/bitmaps/text_48.png")
    ImageResource toolboxTextIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/cursor_48.png")
    ImageResource toolboxCursorIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/tasklist_2_48.png")
    ImageResource toolboxTaskListIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/image_48.png")
    ImageResource toolboxImageIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/delete.png")
    ImageResource taskRemoveIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/resize.png")
    ImageResource resizeIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/rotate.png")
    ImageResource rotateIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/closeIcon.png")
    ImageResource closeIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/moveBackIcon.png")
    ImageResource moveBackIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/moveFrontIcon.png")
    ImageResource moveFrontIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/helpIcon_blue_32.png")
    ImageResource taskDefaultIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/bank_32.png")
    ImageResource bankIcon32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/bicycle_32.png")
    ImageResource bicycleIcon32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/fix_32.png")
    ImageResource fixIcon32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/rainbow_32.png")
    ImageResource rainbowIcon32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/phone_32.png")
    ImageResource phoneIcon32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/robot_32.png")
    ImageResource robotIcon32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/bingLogo_32.png")
    ImageResource bingLogo32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/flickrLogo_32.png")
    ImageResource flickrLogo32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/youtubeLogo_32.png")
    ImageResource youtubeLogo32();

    @Source("com/project/website/shared/client/resources/images/bitmaps/videoIcon_48.png")
    ImageResource toolboxVideoIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/videoIcon_48.png")
    ImageResource videoToolEmptyBackground();

    @Source("com/project/website/shared/client/resources/images/bitmaps/map_48.png")
    ImageResource mapIcon48();

    @Source("com/project/website/shared/client/resources/images/bitmaps/image_unavailable.png")
    ImageResource imageUnavailable();

    @Source("com/project/website/shared/client/resources/images/bitmaps/loadCircle.gif")
    DataResource  imageLoadIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/bingLogo_16.png")
    ImageResource bingLogo16();

    @Source("com/project/website/shared/client/resources/images/bitmaps/flickrLogo_16.png")
    ImageResource flickrLogo16();

    @Source("com/project/website/shared/client/resources/images/bitmaps/person_24.png")
    ImageResource person24();

}
