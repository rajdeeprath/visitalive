package org.red5.core;

import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;

public class UtiliService 
{
	
	public boolean amAlive()
	{
		IConnection conn = Red5.getConnectionLocal();
		return conn.isConnected();
	}
	
	public String getRemoteAddress()
	{
		IConnection conn = Red5.getConnectionLocal();
		return conn.getRemoteAddress();
	}
	
	public String getHost()
	{
		IConnection conn = Red5.getConnectionLocal();
		return conn.getHost();
	}
	
	public String getConnectionType()
	{
		IConnection conn = Red5.getConnectionLocal();
		return conn.getType();
	}
	
	public int getRemotePort()
	{
		IConnection conn = Red5.getConnectionLocal();
		return conn.getRemotePort();
	}

}
