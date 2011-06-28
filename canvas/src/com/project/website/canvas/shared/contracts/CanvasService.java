package com.project.website.canvas.shared.contracts;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.project.website.canvas.shared.data.CanvasPage;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("canvas")
public interface CanvasService extends RemoteService {
    CanvasPage savePage(CanvasPage page);

    CanvasPage getPage(long id);

}
