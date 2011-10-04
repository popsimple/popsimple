package com.project.shared.client.utils;

import java.util.HashSet;

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
						successHandler.apply(null);
					}
				});
			}
		};
	}

	public static class OneTimeScheduler
	{
	    private static OneTimeScheduler INSTANCE;
        private HashSet<ScheduledCommand> _pendingCommands = new HashSet<ScheduledCommand>();

	    public static OneTimeScheduler get() {
	        if (null == INSTANCE) {
	            INSTANCE = new OneTimeScheduler();
	        }
	        return INSTANCE;
	    }

	    public void scheduleDeferredOnce(final ScheduledCommand command)
	    {
            if (_pendingCommands.contains(command)) {
                return;
            }
            _pendingCommands.add(command);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute()
                {
                    _pendingCommands.remove(command);
                    command.execute();
                }
            });
	    }
	}
}
