/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;


public class JvnServerImpl
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	

	private static final long serialVersionUID = 1L;

// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	
	private JvnRemoteCoord jvnCoord;
	
	private HashMap<Integer,JvnObject> objectsCache;
  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnServerImpl() throws Exception {
		// to be completed
		objectsCache=new HashMap<>();
        try{
	    	jvnCoord=(JvnRemoteCoord) Naming.lookup("//localhost:1050/coord");
        }
            catch(NotBoundException e){
            System.out.println("fail");
            e.printStackTrace();
        }
            catch(RemoteException e){
            System.out.println("fail");

        }
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
        JvnObject object=null;
		try {
			int id = jvnCoord.jvnGetObjectId();
			object=new jvnObjectImpl(o, id);
            objectsCache.put(object.jvnGetObjectId(),object);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JvnException("Error ! jvnCreateObject ");
		}

        return object;
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
				objectsCache.put(object.jvnGetObjectId(),object);

			
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JvnException("EXception in jvnLookUpObject in jvnServer");
		}
        return object;
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

       System.out.println("jvn server lock read called1");
	   Serializable s=null;
	try {

		s = jvnCoord.jvnLockRead(joi, js);

        System.out.println("jvn server lock read called");

	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new JvnException("Exception in jvnLockRead in jvnServer ");
	}

       return s;
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
		Serializable s=null;

       System.out.println("jvn server lock read called1");
		try {
			s=jvnCoord.jvnLockWrite(joi, js);
            System.out.println("jvn server lock write called");

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new JvnException("Exception in jvnLockWrite in jvnServer");
		}
       return s;
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
         this.objectsCache.get(joi).jvnInvalidateReader();
	}
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
      return this.objectsCache.get(joi).jvnInvalidateWriter();
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException {
    return this.objectsCache.get(joi).jvnInvalidateWriterForReader();
   }
}

 
