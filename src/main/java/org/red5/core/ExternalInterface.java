package org.red5.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.red5.server.api.IClient;
import org.red5.server.api.IScope;

public class ExternalInterface 
{
	public Application application;

	/**
     * Get all the connections (clients)
     * 
     * @param scopeName
     * @return HashMap with all clients in the given scope
     */
 	
    public ArrayList<User> getConnections() 
    {
            ArrayList<User> connections = new ArrayList<User>();
            IScope root = application.appscope;
            if (root != null) 
            {
                    Set<IClient> clients = root.getClients();
                    Iterator<IClient> client = clients.iterator();
                    while (client.hasNext()) 
                    {
                            IClient c = client.next();
                            String clientType = (String)c.getAttribute("type");
                            
                            if(clientType.equals(UserType.PRODUCER))
                            {
	                            User user = new User();
	                            user.clientid = c.getId();
	                            user.ip = (String) c.getAttribute("ipaddress");
	                            user.country = (String) c.getAttribute("country");
	                            user.countryCode = (String) c.getAttribute("countryCode");
	                            user.longitude = Float.parseFloat(c.getAttribute("longitude").toString());
	                            user.latitude = Float.parseFloat(c.getAttribute("latitude").toString());
	                            user.entrytime = Long.parseLong(c.getAttribute("logintime").toString());
	                            user.type = (String) c.getAttribute("type");
	                            connections.add(user);
                            }
                    }
            }
            
            return connections;
    }
    
    public void setApplication(Application ref)
    {
    	application = ref;
    }
}
