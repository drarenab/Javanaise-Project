/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.io.*;



public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	

	private static final long serialVersionUID = 1L;

// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	
	private static JvnRemoteCoord jvnCoord;
	
	private ArrayList<JvnObject> objectsCache;
  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnServerImpl() throws Exception {
		super();
		// to be completed
		objectsCache=new ArrayList<JvnObject>();
		jvnCoord=(JvnRemoteCoord) Naming.lookup("coordinator");
	}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
    // to be completed 
		
		try {
			jvnCoord.jvnTerminate(js);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException { 
		// to be completed 
		
		try {
			int id = jvnCoord.jvnGetObjectId();
			JvnObject object=new jvnObjectImpl(o, id);
			return object;
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JvnException("Error ! jvnCreateObject ");
		}
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
		// to be completed 
		try {
			jvnCoord.jvnRegisterObject(jon, jo, js);//not sure 
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	* Provide the reference of a JVN object beeing given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
    // to be completed 
		JvnObject object=null;
		
		try {
			
			object=jvnCoord.jvnLookupObject(jon, js);
			if(object!=null) {
				objectsCache.add(object);
			return object;
			
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JvnException("EXception in jvnLookUpObject in jvnServer");
		}
		
		return null;
		
		
	}	
	
	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockRead(int joi)
	 throws JvnException {
		// to be completed 
	   
	   Serializable s;
	try {
		s = jvnCoord.jvnLockRead(joi, js);
		return s;
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new JvnException("Exception in jvnLockRead in jvnServer ");
	}
	   

	}	
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
		// to be completed 
		Serializable s;
		
		try {
			s=jvnCoord.jvnLockWrite(joi, js);
			return s;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new JvnException("Exception in jvnLockWrite in jvnServer");
		}
	}	

	
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
		// to be completed 
	  //recuperer l'objet du cache and invalidate it
	  boolean found=false;
	  for (JvnObject jvnObject : objectsCache) {
		if(jvnObject.jvnGetObjectId()==joi) {
			found=true;
			jvnObject.jvnInvalidateReader();
			break;
		}
			
	  }
	  if(!found) {
			throw new JvnException("no object in server cache to invalidate for Reader");
		}

	};
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed 
	
	  boolean found=false;
	  for (JvnObject jvnObject : objectsCache) {
		if(jvnObject.jvnGetObjectId()==joi) {
			found=true;
			return jvnObject.jvnInvalidateWriter();
			
		}
			
	  }
	  if(!found) {
			return new JvnException("no object in server cache to invalidate for writing");
		}
	  //ligne rajouter pour rien mais obligatoire apparement 
	  return null;
	};
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed 
	   boolean found=false;
		  for (JvnObject jvnObject : objectsCache) {
			if(jvnObject.jvnGetObjectId()==joi) {
				found=true;
				return jvnObject.jvnInvalidateWriterForReader();
				
			}
				
		  }
		  if(!found) {
				return new JvnException("no object in server cache to invalidate writer for reader");
			}
		  //ligne rajouter pour rien mais obligatoire apparement 
		  return null;
		};
}

 
