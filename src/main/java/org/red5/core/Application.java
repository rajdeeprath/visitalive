package org.red5.core;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2008 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

import org.apache.commons.lang.ArrayUtils;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import com.maxmind.geoip.*;
import org.red5.server.api.so.ISharedObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

import org.red5.core.UserType;
import org.springframework.core.io.Resource;

/**
 * Sample application that uses the client manager.
 * 
 * @author The Red5 Project (red5@osflash.org)
 */

public class Application extends ApplicationAdapter 
{
	private String geoDatabasePath;
	private LookupService cl;
	private ISharedObject visiteso;
	public IScope appscope;
	private Calendar now;
	private String sharedObjectName = "visiteso";
	private static Logger log;
	
	private Boolean HTMLDomainsAuth = true;
	private Boolean SWFDomainsAuth = true;
	private String[] allowedHTMLDomains;
	private String[] allowedSWFDomains;
	private String htmlDomains = "allowedHTMLdomains.txt";
	private String swfDomains = "allowedSWFdomains.txt";
	
	public void setHtmlDomains(String value)
	{
		htmlDomains = value;
	}
	
	public void setSwfDomains(String value)
	{
		swfDomains = value;
	}
	
	public void setGeoDatabasePath(String value)
	{
		geoDatabasePath = value;
	}
	
	
	/** {@inheritDoc} */
	 
	public boolean appStart(IScope app) 
	{
		log = Logger.getLogger(Application.class.getName());
		appscope  = app;
		
		
		/* Read in valid domain names */
		this.allowedHTMLDomains = this.readValidDomains(htmlDomains,"HTMLDomains");
		this.allowedSWFDomains = this.readValidDomains(swfDomains, "SWFDomains");
		
		
		/* Init Shared Object */
		initializeSharedObject(true);
		
		
		/* Read in database */
		try
		{
			Resource r = this.getResource(geoDatabasePath);
			String database = r.getFile().getPath();
			cl = new LookupService(database,LookupService.GEOIP_STANDARD);
		}
		catch(IOException e){	
			log.info("Error Loading DB" + e.getMessage());
			return false;
		}
		
		return true;
	}
	

 	/* Init SharedObject */
 	private void initializeSharedObject(boolean clear)
 	{
		visiteso = null;
		visiteso = getSharedObject(appscope, sharedObjectName,true);
		if(clear) visiteso.removeAttributes();
 	}
 	

	/** {@inheritDoc} */
    @Override
	public boolean connect(IConnection conn, IScope scope, Object[] params)
    {
    	initializeSharedObject(false);
    	
    	/* Security validator */
    	if(!validateSource(conn)) return false;
    	
    	
    	/* Differentiate between type of user */
    	IClient client = conn.getClient();
 		
		if(!((Boolean)params[0].equals(true)))
			client.setAttribute("type", UserType.CONSUMER);
		else
			client.setAttribute("type", UserType.PRODUCER);
		

		if(client.getAttribute("type").toString() == UserType.PRODUCER)
		{
 			String ipaddress = conn.getRemoteAddress();
 			now = Calendar.getInstance();
 			
			Location l1 = cl.getLocation(ipaddress);
			client.setAttribute("ipaddress", ipaddress);
			client.setAttribute("country", l1.countryName);
			client.setAttribute("countryCode", l1.countryCode);
			client.setAttribute("longitude", l1.longitude);
			client.setAttribute("latitude", l1.latitude);
			client.setAttribute("logintime", now.getTimeInMillis());
			client.setAttribute("type", UserType.PRODUCER);
		}
		
		/* Set update via shared object */
		visiteso.setAttribute("count", appscope.getClients().size());
		
 		return super.connect(conn,scope, params);
	}

	
    /** {@inheritDoc} */
    @Override
	public void disconnect(IConnection conn, IScope scope) 
    {    	
    	/* set update via shared object */
		visiteso.setAttribute("count", appscope.getClients().size());
		
		super.disconnect(conn, scope);
	}
    
    /** {@inheritDoc} */
	
	public void appStop(IScope app) 
	{
		super.appStop(app);
	}
	
	/********* Validate Connection **********/
	
	private Boolean validateSource(IConnection conn)
	{
		boolean flag = true;
		
		try 
		{
			String pageUrl = conn.getConnectParams().get("pageUrl").toString();
			String swfUrl = conn.getConnectParams().get("swfUrl").toString();
			String ip = conn.getRemoteAddress();
			
			
			if (( ip != "127.0.0.1") && HTMLDomainsAuth &&  !this.validate( pageUrl, this.allowedHTMLDomains ) )
			flag = false;
	
			if ((ip != "127.0.0.1") && SWFDomainsAuth &&  !this.validate(swfUrl, this.allowedSWFDomains ) )
			flag = false;
			
		}
		catch (Exception e) 
		{
			flag = false;
		}
		
		return flag;
	}
	
	/********* Validate Domain **********/
	
	private Boolean validate(String url, String[] patterns)
	{
		// Convert to lower case
		url = url.toLowerCase();
		int domainStartPos = 0; // domain start position in the URL
		int domainEndPos = 0; // domain end position in the URL
		
		switch (url.indexOf( "://" ))
		{
			case 4:
				if(url.indexOf( "http://" ) ==0)
					domainStartPos = 7;
				break;
			case 5:
				if(url.indexOf( "https://" ) ==0)
					domainStartPos = 8;
				break;
		}
		if(domainStartPos == 0)
		{
			// URL must be HTTP or HTTPS protocol based
			return false;
		}
		domainEndPos = url.indexOf("/", domainStartPos);
		if(domainEndPos>0)
		{
			int colonPos = url.indexOf(":", domainStartPos); 
			if( (colonPos>0) && (domainEndPos > colonPos))
			{
				// probably URL contains a port number
				domainEndPos = colonPos; // truncate the port number in the URL
			}
		}
		
		url = url.substring(domainStartPos, domainEndPos);
		
		// if has www in begining thne remove it
		
		if(url.indexOf("www.")== 0)
			url = url.replace("www.", "");
		
		if (ArrayUtils.contains(patterns, url)) return true; 
		
		return false;
	}
	
	
	/********* Read Domains **********/
	
	private String[] readValidDomains(String fileName , String domainsType )
	{
		String[] domainsArray = new String[100];
		
		try {
			DataInputStream in = new DataInputStream(this.getResource(fileName).getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			
			int index = 0;
			@SuppressWarnings("unused")
			int lineCount = 0;
			
			String strLine = "";
		   
		    while ((strLine = br.readLine()) != null)   {
		    	if( strLine.equals("")  || strLine.indexOf("#") == 0)
				{
					continue;
				}
		    	
		    	if(strLine.indexOf(" ") < 0)
				{
		    		index++;
		    		domainsArray[index] =  strLine.toLowerCase();
					
					if(strLine.trim().equals("*"))
					{
						if (domainsType.equals("HTMLDomains"))
						{
							HTMLDomainsAuth = false;
						} else if (domainsType.equals("SWFDomains")) {
							SWFDomainsAuth = false;	
						}
					}
				}
		    }
	    
		    in.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (domainsType.equals("HTMLDomains"))
			{	
				HTMLDomainsAuth = false;
			} else if (domainsType.equals("HTMLDomains")) {
				SWFDomainsAuth = false;	
			}
		}

		return domainsArray;
	}

}
