package com.project.shared.client.utils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;

public class SchedulerUtils {
	public static AsyncFunc<Void,Void> getDeferredAsyncFunc() {
		return new AsyncFunc<Void, Void>() {
			@Override
			protected <S, E> void run(Void arg, final Func<Void, S> successHandler, Func<Throwable, E> errorHandler) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						successHandler.call(null);
					}
				});
			}
		};
	}
}
