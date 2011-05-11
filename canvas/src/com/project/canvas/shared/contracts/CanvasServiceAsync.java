package com.project.canvas.shared.contracts;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.shared.data.CanvasPage;

public interface CanvasServiceAsync {

	void SavePage(CanvasPage page, AsyncCallback<Void> callback);

}
