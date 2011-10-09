package com.project.website.canvas.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

public interface CanvasResources extends ClientBundle {
    public static final CanvasResources INSTANCE = GWT.create(CanvasResources.class);

    @Source("main.css")
    MainStyles main();

    @Source("com/project/website/shared/client/resources/images/bitmaps/text_48.png")
    ImageResource toolboxTextIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/cursor_48.png")
    ImageResource toolboxCursorIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/move-icon_48.png")
    ImageResource toolboxMoveIcon();

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

    @Source("com/project/website/shared/client/resources/images/bitmaps/system-help.png")
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
    DataResource loadingIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/bingLogo_16.png")
    ImageResource bingLogo16();

    @Source("com/project/website/shared/client/resources/images/bitmaps/flickrLogo_16.png")
    ImageResource flickrLogo16();

    @Source("com/project/website/shared/client/resources/images/bitmaps/person_24.png")
    ImageResource person24();

    @Source("com/project/website/shared/client/resources/images/bitmaps/siteCropIcon_48.png")
    ImageResource cropSiteIcon48();

    @Source("com/project/website/shared/client/resources/images/bitmaps/siteCropIcon_48.png")
    ImageResource cropSiteToolEmptyBackground();

    @Source("com/project/website/shared/client/resources/images/bitmaps/sketchIcon_48.png")
    ImageResource sketchIcon48();

    @Source("com/project/website/shared/client/resources/images/bitmaps/cropSelectIcon_16.png")
    ImageResource cropSelectIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/browseIcon_16.png")
    ImageResource cropBrowseIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/cropMoveIcon_16.png")
    ImageResource cropMoveIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/cropCutIcon_16.png")
    ImageResource cropCutIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/cropAcceptIcon_16.png")
    ImageResource cropAcceptIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/closeIcon.png")
    ImageResource cropCancelIcon();

    @Source("com/project/website/shared/client/resources/images/bitmaps/sliderHorizontalLeft_10.png")
    @ImageOptions(flipRtl = true)
    ImageResource sliderIconHorizontalLess();

    @Source("com/project/website/shared/client/resources/images/bitmaps/sliderHorizontalRight_10.png")
    @ImageOptions(flipRtl = true)
    ImageResource sliderIconHorizontalMore();

    @Source("com/project/website/shared/client/resources/images/bitmaps/sliderDrag_10.png")
    ImageResource sliderIconDrag();
}
