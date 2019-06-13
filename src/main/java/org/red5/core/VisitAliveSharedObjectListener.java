package org.red5.core;

import java.util.List;
import java.util.Map;

import org.red5.server.api.IAttributeStore;
import org.red5.server.api.so.ISharedObjectBase;
import org.red5.server.api.so.ISharedObjectListener;

public class VisitAliveSharedObjectListener implements ISharedObjectListener {

	
	public void onSharedObjectClear(ISharedObjectBase arg0) {
		// TODO Auto-generated method stub
	}

	public void onSharedObjectConnect(ISharedObjectBase arg0) {
		// TODO Auto-generated method stub
	}

	public void onSharedObjectDelete(ISharedObjectBase arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	public void onSharedObjectDisconnect(ISharedObjectBase arg0) {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unchecked")
	public void onSharedObjectSend(ISharedObjectBase arg0, String arg1,List arg2) {
		// TODO Auto-generated method stub
	}

	public void onSharedObjectUpdate(ISharedObjectBase arg0,IAttributeStore arg1) {
		// TODO Auto-generated method stub
	}

	public void onSharedObjectUpdate(ISharedObjectBase arg0,Map<String, Object> arg1) {
		// TODO Auto-generated method stub
	}

	public void onSharedObjectUpdate(ISharedObjectBase arg0, String arg1,Object arg2) {
		// TODO Auto-generated method stub

	}

}
