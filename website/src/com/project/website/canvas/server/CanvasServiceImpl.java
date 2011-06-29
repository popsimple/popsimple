package com.project.website.canvas.server;

import java.util.HashMap;
import java.util.HashSet;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.website.canvas.shared.contracts.CanvasService;
import com.project.website.canvas.shared.data.CanvasPage;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.shared.server.authentication.AuthenticationServiceImpl;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CanvasServiceImpl extends RemoteServiceServlet implements CanvasService {

    @Override
    public CanvasPage savePage(CanvasPage page) {
        new AuthenticationServiceImpl().isLoggedIn(this.getThreadLocalRequest());

        ObjectDatastore datastore = new AnnotationObjectDatastore();
        // String serverInfo = getServletContext().getServerInfo();
        // String userAgent = getThreadLocalRequest().getHeader("User-Agent");
        // Get existing elements
        HashMap<Long, ElementData> removedElems = new HashMap<Long, ElementData>();
        HashSet<Long> elemIds = new HashSet<Long>();
        for (ElementData elem : page.elements) {
            elemIds.add(elem.id);
        }

        if (null != page.id) {
            CanvasPage existingPage = datastore.load(CanvasPage.class, page.id);
            for (ElementData elem : existingPage.elements) {
                if (elemIds.contains(elem.id)) {
                    continue;
                }
                removedElems.put(elem.id, elem);
            }
            // TODO: deal with the removed elements.
            // we have to check if any other page is still using them.
        }

        datastore.store(page);

        return this.getPage(page.id);
    }

    @Override
    public CanvasPage getPage(long id) {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        CanvasPage page = datastore.load(CanvasPage.class, id);
        return page;
    }
}
