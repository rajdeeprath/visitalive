package org.red5.core.security;

import org.red5.server.api.stream.IStreamPlaybackSecurity;
import org.red5.server.api.stream.IStreamPublishSecurity;
import org.red5.server.api.so.ISharedObjectSecurity;


public class SecurityLoader extends SecurityBase {

	private IStreamPlaybackSecurity playbackSecurity;
	private IStreamPublishSecurity publishSecurity;
	private ISharedObjectSecurity sharedObjectSecurity;
	
	public void setPlaybackSecurity(IStreamPlaybackSecurity playback)
	{
		playbackSecurity = playback;
	}
	
	public void setPublishSecurity(IStreamPublishSecurity publish)
	{
		publishSecurity = publish;
	}
	
	public void setSharedObjectSecurity(ISharedObjectSecurity sharedobject)
	{
		sharedObjectSecurity = sharedobject;
	}
	
	
	public void init()
	{
		application.registerStreamPlaybackSecurity(playbackSecurity);
		application.registerStreamPublishSecurity(publishSecurity);
		application.registerSharedObjectSecurity(sharedObjectSecurity);
	}

}
