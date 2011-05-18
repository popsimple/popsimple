package com.project.canvas.shared.contracts;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.project.canvas.shared.data.CanvasPage;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("canvas")
public interface CanvasService extends RemoteService {
    CanvasPage SavePage(CanvasPage page);

    CanvasPage GetPage(long id);

}
