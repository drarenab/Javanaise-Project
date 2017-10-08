package jvn;

import java.io.Serializable;

import outils.ObjectStatEnum;


public class jvnObjectImpl  implements JvnObject{

	private Serializable SharedObject;
	private ObjectStatEnum objectStat;//0 for no lock, 1 for read lock, 2 for write lock
	private int objectId=-1;
	
	/*
	 * when initialize new serializable object state must be write lock 
	 * for exemple when creating new sentence it does it mean that we want to 
	 * write new sentence i think
	 */
	public jvnObjectImpl(Serializable o) {
		SharedObject=o;
		objectStat=ObjectStatEnum.WRITE_LOCK_TAKEN;
		
	}
	
	public void jvnLockRead() throws JvnException {
		// TODO Auto-generated method stub
		synchronized(SharedObject) {// sur l'objet ou sur son etat
			if(objectStat==objectStat.READ_LOCK_CACHED) {
				objectStat=objectStat.READ_LOCK_TAKEN;
			}else if(objectStat==objectStat.No_LOCK){
				SharedObject=JvnServerImpl.jvnGetServer().jvnLockRead(this.jvnGetObjectId());
				objectStat=objectStat.READ_LOCK_TAKEN;
			}else if(objectStat==objectStat.WRITE_LOCK_CACHED){
				objectStat=ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED;
			}else {
			throw new JvnException("can't do lock read for the stat : "+this.objectStat );
			//si objectStat==write lock taken on le laisse comme il est
			}
		}
		
	}
	
	public void jvnLockWrite() throws JvnException {
		// TODO Auto-generated method stub
		synchronized(SharedObject) {// sur l'objet ou sur son etat
			if(objectStat==objectStat.WRITE_LOCK_CACHED || objectStat==objectStat.READ_LOCK_TAKEN_WRITE_LOCK_CACHED)
			{
				objectStat=objectStat.WRITE_LOCK_TAKEN;
			}else if(objectStat==ObjectStatEnum.No_LOCK
					||objectStat==ObjectStatEnum.READ_LOCK_TAKEN
					||objectStat==ObjectStatEnum.READ_LOCK_CACHED){
				//retourne null ou quoi en cas d'erreur??? a verifier !!
				SharedObject=JvnServerImpl.jvnGetServer().jvnLockWrite(this.jvnGetObjectId());
				objectStat=ObjectStatEnum.WRITE_LOCK_TAKEN;
			}else {
				throw new JvnException("can't do lock write for object state : "+this.objectStat);
			}
		}
	}

	public void jvnUnLock() throws JvnException {
		// TODO Auto-generated method stub
		synchronized(SharedObject) {// sur l'objet ou sur son etat
		
			if(objectStat==objectStat.READ_LOCK_TAKEN) {
				objectStat=objectStat.READ_LOCK_CACHED;
			}else if(objectStat==objectStat.WRITE_LOCK_TAKEN) {
				objectStat=objectStat.WRITE_LOCK_CACHED;
			}else if(objectStat== objectStat.READ_LOCK_TAKEN_WRITE_LOCK_CACHED){
				//on livére seulement le read on ne touche pas au write
				objectStat=objectStat.WRITE_LOCK_CACHED;
			}else {
				throw new JvnException("can't do unlock lock for the stat : "+this.objectStat );
			}
		
		}
	}

	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
	
		if(objectId<0)
			throw new JvnException("Id nagative , not valid !");
		return objectId;
	}
	//retourne l'objet si il a lock teken ou bien retourne seulement l'etat de l'objet
	public Serializable jvnGetObjectState() throws JvnException {
		// TODO Auto-generated method stub
		return SharedObject ;
	}

	public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub
		synchronized(SharedObject) {// sur l'objet ou sur son etat
			if(objectStat==ObjectStatEnum.READ_LOCK_TAKEN) {
				while(objectStat==ObjectStatEnum.READ_LOCK_TAKEN) {//en attente d'une ecriture d'un autre utilisateur
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				objectStat=ObjectStatEnum.No_LOCK;

			}//pour eviter le while et la suite en sequentiel
			else if(objectStat==ObjectStatEnum.WRITE_LOCK_CACHED
			   ||objectStat==ObjectStatEnum.WRITE_LOCK_TAKEN) {
				throw new JvnException("case not permitted ");
			}else if (objectStat==ObjectStatEnum.READ_LOCK_CACHED
					||objectStat==ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED) {
				objectStat=ObjectStatEnum.No_LOCK;

			}
			
		}
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
