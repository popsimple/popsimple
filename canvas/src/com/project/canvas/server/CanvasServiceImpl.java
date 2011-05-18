package com.project.canvas.server;

import java.util.HashMap;
import java.util.HashSet;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.data.CanvasPage;
import com.project.canvas.shared.data.ElementData;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CanvasServiceImpl extends RemoteServiceServlet implements CanvasService {

    @Override
    public CanvasPage SavePage(CanvasPage page) {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        // String serverInfo = getServletContext().getServerInfo();
        // String userAgent = getThreadLocalRequest().getHeader("User-Agent");
        // Get existing elements
        HashMap<Long, ElementData> removedElems = new HashMap<Long, ElementData>();
        HashSet<Long> elemIds = new HashSet<Long>();
        for (ElementData elem : page.elements) {
            elemIds.add(elem.id);
        }

        datastore.setActivationDepth(2);
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

        return this.GetPage(page.id);
    }

    @Override
    public CanvasPage GetPage(long id) {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        return datastore.load(CanvasPage.class, id);
    }
}
