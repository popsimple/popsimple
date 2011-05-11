package com.project.canvas.server;

import com.project.canvas.shared.FieldVerifier;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.data.CanvasPage;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CanvasServiceImpl extends RemoteServiceServlet implements CanvasService {

	@Override
	public void SavePage(CanvasPage page) {
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
	}
}
