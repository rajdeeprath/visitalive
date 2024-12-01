package org.red5.core.security;

import java.util.List;
import org.red5.server.api.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectSecurity;

import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.ArrayUtils;

public class SharedObjectSecurity extends SecurityBase implements ISharedObjectSecurity {
  
  private Boolean creationAllowed;
  private Boolean connectionAllowed;
  private Boolean deleteAllowed;
  private Boolean sendAllowed;
  private Boolean writeAllowed;
  private Boolean enableSharedObjects;
  
  private String sharedObjectNames;
  private Boolean NamesAuth = false;
  private String[] allowedPublishNames;
  
  
  public void setConnectionAllowed(Boolean value)
  {
	  connectionAllowed = value;
  }
  
  public void setCreationAllowed(Boolean value)
  {
	  creationAllowed = value;
  }
  
  public void setDeleteAllowed(Boolean value)
  {
	  deleteAllowed = value;
  }
  
  public void setSendAllowed(Boolean value)
  {
	  sendAllowed = value;
  }
  
  public void setWriteAllowed(Boolean value)
  {
	  writeAllowed = value;
  }
  
  public void setEnableSharedObjects(Boolean value)
  {
	  enableSharedObjects = value;
  }
  
  public void setSharedObjectNames(String names)
  {
	  sharedObjectNames = names;
  }
  
  public void init()
  {
	  allowedPublishNames = this.readValidNames(sharedObjectNames);
  }
  
  private Boolean validate(String name, String[] patterns)
	{
		if (ArrayUtils.indexOf(patterns, name) > 0) return true; 
		return false;
	}
	
	private String[] readValidNames(String fileName)
	{
		String[] namesArray = {};
		
		try {
			NamesAuth = true;

			DataInputStream in = new DataInputStream(application.getResource(fileName).getInputStream());
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
		
		    		namesArray[index] =  strLine.toLowerCase();
					index++;
					
					if(strLine == "*")
					{
						//log.debug("Found wildcard (*) entry: disabling authentication of HTML file domains ")	;
						NamesAuth = false;
						
					}
				}
		    }
	    
		    in.close();
		} catch (Exception e) {
			//log.error(e.getMessage());
			NamesAuth = false;
		}

		return namesArray;
	}
  
  public boolean isConnectionAllowed(ISharedObject so) {
      // Note: we don't check for the name here as only one SO can be
      //       created with this handler.
      return (enableSharedObjects && connectionAllowed);
  }
  
  public boolean isCreationAllowed(IScope scope, String name,
    boolean persistent) {
	
	   if (enableSharedObjects && creationAllowed)
	   {
		  if (NamesAuth &&  !this.validate(name, this.allowedPublishNames ) )
		  {
				//log.debug("Authentication failed for shared object name: " + name);
				return false;
		  }
		  return true;
	   }
      return false;
  }
  
  public boolean isDeleteAllowed(ISharedObject so, String key) {
      return deleteAllowed;
  }
  
  public boolean isSendAllowed(ISharedObject so, String message,
    List<?> arguments) {
      return sendAllowed;
  }
  
  public boolean isWriteAllowed(ISharedObject so, String key,
    Object value) {
      return writeAllowed;
  }
  
}