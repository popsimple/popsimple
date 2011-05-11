package com.project.canvas.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class PersistenceManagerFactoryWrapper {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private PersistenceManagerFactoryWrapper() {
	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}
}
