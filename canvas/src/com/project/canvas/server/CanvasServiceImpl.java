package com.project.canvas.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.data.CanvasPage;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.PageElement;
import com.project.canvas.shared.data.Task;
import com.project.canvas.shared.data.TaskListData;
import com.project.canvas.shared.data.TextData;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CanvasServiceImpl extends RemoteServiceServlet implements CanvasService {

	private static ObjectifyFactory getObjectifyFactory() {
		ObjectifyService.register(CanvasPage.class);
		ObjectifyService.register(TextData.class);
		ObjectifyService.register(TaskListData.class);
		ObjectifyService.register(Task.class);
		ObjectifyService.register(PageElement.class);
		return ObjectifyService.factory();
	}

	protected final ObjectifyFactory oFactory = getObjectifyFactory();
	
	@Override
	public CanvasPage SavePage(CanvasPage page) {
		//String serverInfo = getServletContext().getServerInfo();
		//String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		// Get existing elements
		Objectify ofy = oFactory.begin();
		HashSet<ElementData> newElems = new HashSet<ElementData>();
		HashMap<Long, ElementData> elemsNotInPage = new HashMap<Long, ElementData>();
		HashSet<Long> elemIds = new HashSet<Long>();
		for (ElementData elem : page.elements) {
			if (null == elem.id) {
				newElems.add(elem);
				continue;
			}
			elemIds.add(elem.id);
			elemsNotInPage.put(elem.id, elem);
		}
		
		if (null != page.id) {
			// Not a new page.
			QueryResultIterable<PageElement> pageElements = ofy.query(PageElement.class)
															   .filter("page", page)
															   .fetch();
			for (PageElement pageElement : pageElements)
			{
				if (elemIds.contains(pageElement.data.getId())) {
					elemsNotInPage.remove(pageElement.data.getId());
				}
			}
		}
		Key<CanvasPage> pageKey = ofy.put(page);
		
		Map<Key<ElementData>, ElementData> newElemsMap = ofy.put(newElems);
		for (ElementData elem : newElemsMap.values()) {
			elemsNotInPage.put(elem.id, elem);
		}
		ArrayList<PageElement> newPageElements = new ArrayList<PageElement>();
		for (ElementData elem : elemsNotInPage.values()) {
			newPageElements.add(new PageElement(pageKey, 
												new Key<ElementData>(ElementData.class, elem.id)));
		}
		ofy.put(page.elements);
		ofy.put(newPageElements);
		
		return this.GetPage(pageKey.getId());
	}

	@Override
	public CanvasPage GetPage(long id) {
		Objectify ofy = oFactory.begin();
		CanvasPage page = ofy.get(CanvasPage.class, id);
		Query<PageElement> elems = ofy.query(PageElement.class).filter("page", page);
		HashSet<Key<ElementData>> elemsToFetch = new HashSet<Key<ElementData>>();
		for (PageElement elem : elems.fetch()) {
			elemsToFetch.add(elem.data);
		}
		page.elements = new ArrayList<ElementData>(ofy.get(elemsToFetch).values());
		return page;
	}

	
}
