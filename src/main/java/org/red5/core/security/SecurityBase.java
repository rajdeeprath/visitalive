package org.red5.core.security;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;

public class SecurityBase {

	protected MultiThreadedApplicationAdapter application;
	
	public void setApplication(MultiThreadedApplicationAdapter app)
	{
		application = app;
	}
}


