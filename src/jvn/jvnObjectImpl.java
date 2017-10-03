package jvn;

import java.io.Serializable;


public class jvnObjectImpl  implements JvnObject{

	private Serializable SharedObject;
	private int objectStat;//0 for no lock, 1 for read lock, 2 for write lock
	private int objectId=-1;
	
	public jvnObjectImpl(Serializable o) {
		SharedObject=o;
		objectStat=0;
	}
	
	public void jvnLockRead() throws JvnException {
		// TODO Auto-generated method stub
		
	}

	public void jvnLockWrite() throws JvnException {
		// TODO Auto-generated method stub
		
	}

	public void jvnUnLock() throws JvnException {
		// TODO Auto-generated method stub
		
	}

	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
	
		if(objectId<0)
			throw new JvnException("Id nagativ , not valid !");
		return objectId;
	}

	public Serializable jvnGetObjectState() throws JvnException {
		// TODO Auto-generated method stub
		return objectStat;
	}

	public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub
		
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

}
