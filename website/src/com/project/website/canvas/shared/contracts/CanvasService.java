package com.project.website.canvas.shared.contracts;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.project.website.canvas.shared.data.CanvasPage;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("canvas")
public interface CanvasService extends RemoteService {
    
    public class AccessDeniedException extends Exception {
        public AccessDeniedException() { super(); }
        public AccessDeniedException(String string) {
            super(string);
        }

        private static final long serialVersionUID = 1L;
    }

    public static int CANVAS_PAGE_SAVE_KEY_LENGTH = 16;

    
    CanvasPage savePage(CanvasPage page) throws AccessDeniedException;

    CanvasPage getPage(long id);

}
