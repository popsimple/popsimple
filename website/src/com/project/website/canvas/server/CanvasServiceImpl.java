package com.project.website.canvas.server;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.shared.utils.RandomUtils;
import com.project.website.canvas.shared.contracts.CanvasService;
import com.project.website.canvas.shared.data.CanvasPage;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CanvasServiceImpl extends RemoteServiceServlet implements CanvasService {

    @Override
    public CanvasPage savePage(CanvasPage page) throws AccessDeniedException {
        //HttpAuthentication.isLoggedIn(this.getThreadLocalRequest(), this.getThreadLocalResponse());

        ObjectDatastore datastore = new AnnotationObjectDatastore();
        // String serverInfo = getServletContext().getServerInfo();
        // String userAgent = getThreadLocalRequest().getHeader("User-Agent");
        // Get existing elements
        //HashMap<Long, ElementData> removedElems = new HashMap<Long, ElementData>();
//        HashSet<Long> elemIds = new HashSet<Long>();
//        for (ElementData elem : page.elements) {
//            elemIds.add(elem.id);
//        }

        if (null == page.id) {
            page.key = RandomUtils.randomString(CanvasService.CANVAS_PAGE_SAVE_KEY_LENGTH);
        }
        else {
            CanvasPage existingPage = datastore.load(CanvasPage.class, page.id);
            
            if (false == existingPage.key.equals(page.key)) {
                // Can't save, key is wrong.
                throw new AccessDeniedException("Can't save page - invalid key"); 
            }
            
//            for (ElementData elem : existingPage.elements) {
//                if (elemIds.contains(elem.id)) {
//                    continue;
//                }
//                removedElems.put(elem.id, elem);
//            }
            // TODO: deal with the removed elements.
            // we have to check if any other page is still using them.
        }

        datastore.store(page);

        return this.loadPageFromDataStore(page.id);
    }

    @Override
    public CanvasPage getPage(long id) {
        CanvasPage page = loadPageFromDataStore(id);
        page.key = null;
        return page;
    }

    private CanvasPage loadPageFromDataStore(long id) {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        CanvasPage page = datastore.load(CanvasPage.class, id);
        return page;
    }
}
