/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

//import sun.management.snmp.jvminstr.JvmOSImpl;

import java.io.Serializable;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	
	private static JvnRemoteCoord coord=null;
	
  /**
  * Default constructor
  * @throws JvnException
  **/
	//list containing rall jvnObjects
	private ConcurrentHashMap<String,JvnObject> jvnObjectList;
	private ConcurrentHashMap<Integer,String> jvnObjectNameIdList;
	
	private ConcurrentHashMap<Integer,JvnRemoteServer> jvnServerList;
	//The server that is currently writing in a jvnObject
	private ConcurrentHashMap<Integer,JvnRemoteServer> jvnServerWriterListOfJvnObject;
	//The List of servers that are reading from a jvnObject 
	private ConcurrentHashMap<Integer,HashSet<JvnRemoteServer>> jvnServerReaderListOfJvnObject;
	//The List of servers that have made a lookup on a jvnObject
	private ConcurrentHashMap<String,HashSet<JvnRemoteServer>> jvnServerLookupList;
	
	//the last id that was assigned to a jvnObject created
	private int jvnObjectId;
	private static JvnCoordImpl instance = null;
	
	private JvnCoordImpl() throws Exception {
		// to be completed
		jvnObjectList = new ConcurrentHashMap<String,JvnObject>();
		jvnObjectNameIdList = new ConcurrentHashMap<Integer,String>();
		jvnObjectId = -1;
		jvnServerLookupList=new ConcurrentHashMap<String,HashSet<JvnRemoteServer>>();
		//Registry registry = LocateRegistry.getRegistry("localhost");
        //JvnRemoteServer obj = (JvnRemoteServer) registry.lookup("MyServer");
		
		Registry reg;
		try {
			reg = LocateRegistry.createRegistry(1099);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			reg = LocateRegistry.getRegistry();
		}
		reg.rebind("coordinator", this);
	}
	
	synchronized public static JvnCoordImpl getInstance() throws Exception {
		if(instance==null) {
			instance = new JvnCoordImpl();
		}
		
		return instance;
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  synchronized public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    // to be completed 
    return ++jvnObjectId;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  synchronized public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    // to be completed 
	  
	  //if the object already exits we throw an exception
	  if(jvnObjectList.containsKey(jon)) 
		  throw new JvnException();
	  //else we add it to the map
	  jvnObjectList.put(jon, jo);
	  jvnObjectNameIdList.put(jo.jvnGetObjectId(),jon);
	 //we also add the server to the list of servers
	  jvnServerList.put(jo.jvnGetObjectId(),js);
	  
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  synchronized  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    // to be completed 
	 //jvnObject to be returned

	  JvnObject jo;
    //getting the list of servers that have made a lookup on the jvnObject(jon)
	HashSet<JvnRemoteServer> serversLookupSet = jvnServerLookupList.get(jon);
	//if the list does not exist we create it
    if(serversLookupSet == null){
    	serversLookupSet = new HashSet<JvnRemoteServer>();
    	jvnServerLookupList.put(jon, serversLookupSet);
    }
    
    //we adds the server to the list
    serversLookupSet.add(js);
    //if the jvnObject exists we return it
    if(jvnObjectList.containsKey(jon))
    	return jvnObjectList.get(jon);
    
    //else we return null
    return null;
    
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   synchronized public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
    // to be completed
	   Serializable object; 
	//verify that the object exists
	String jon = jvnObjectNameIdList.get(joi);
	JvnObject jo = jvnObjectList.get(jon);
	/*
	if(jo==null)
		return new JvnException();
	*/
	//get the writing servers on the jvnObject   
	JvnRemoteServer writingServer = jvnServerWriterListOfJvnObject.get(joi);
	//get list of reading servers on the jvnObject
	HashSet<JvnRemoteServer> readingServers = jvnServerReaderListOfJvnObject.get(joi); 
	//if no server is reading
	if(readingServers==null) {
		//we create  the set of servers
		readingServers = new HashSet<JvnRemoteServer>();
		//we add the set to the server readers map
		jvnServerReaderListOfJvnObject.put(joi, readingServers);
	}
	//and we add the  new server to the reading servers of the jvn object (joi)
	readingServers.add(js);
	
	
	//if there is a server writing
	if(writingServer!=null) {
		//we call invalidateWriterForReader
		object = writingServer.jvnInvalidateWriterForReader(joi);
		//we add the writing server to the reading servers after we did the invaldiation
		readingServers.add(writingServer);
		//we remove the server from the writing servers list
		jvnServerWriterListOfJvnObject.remove(joi);
	}
	else {
		//if there is no servers writing then the object is still the same so we return it as it is !!
		/**
		 * NEED TO BE CHECKED ???????????????
		 */
		object = jvnObjectList.get(joi);
	}
	
    return object;
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   synchronized public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	// to be completed
	Serializable object = null; 
	//verify that the object exists
	String jon = jvnObjectNameIdList.get(joi);
	JvnObject jo = jvnObjectList.get(jon);
	/*
	if(jo==null)
		return new JvnException();
	*/
	//get the writing servers on the jvnObject   
	JvnRemoteServer writingServer = jvnServerWriterListOfJvnObject.get(joi);
	//get list of reading servers on the jvnObject
	HashSet<JvnRemoteServer> readingServers = jvnServerReaderListOfJvnObject.get(joi); 
	//if there are servers reading ..
	if(readingServers!=null) {
		//invalidate all readers 
		while(readingServers.iterator().hasNext()) {
			readingServers.iterator().next().jvnInvalidateReader(joi);
		}
	}
	
	//if a server is writing
	if(writingServer!=null) {
		object = writingServer.jvnInvalidateWriter(joi);
	}
	
	jvnServerWriterListOfJvnObject.remove(joi);
	//we put the new server in the map
	jvnServerWriterListOfJvnObject.put(joi, js);
	
	
    return object;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    synchronized public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
	 // to be completed
    }
    
    public static void main(String[] argv) {
    JvnCoordImpl jci;
	try {
		jci = JvnCoordImpl.getInstance();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	System.out.println("coordinator ready....");
    }
}

 
