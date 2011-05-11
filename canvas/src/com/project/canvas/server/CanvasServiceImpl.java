package com.project.canvas.server;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.data.CanvasPage;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CanvasServiceImpl extends RemoteServiceServlet implements CanvasService {

	PersistenceManagerFactory pmf = PersistenceManagerFactoryWrapper.get();
	
	@Override
	public void SavePage(CanvasPage page) {
		//String serverInfo = getServletContext().getServerInfo();
		//String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			pm.makePersistent(page);
		}
		finally {
			pm.close();
		}
	}
	
}
