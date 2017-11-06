package jvn;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import outils.ObjectStatEnum;


public class jvnObjectImpl implements JvnObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Serializable SharedObject;
	private ObjectStatEnum objectStat;//0 for no lock, 1 for read lock, 2 for write lock
	private int objectId=-1;
	
	/*
	 * when initialize new serializable object state must be write lock 
	 * for exemple when creating new sentence it does it mean that we want to 
	 * write new sentence i think
	 */

	public jvnObjectImpl(Serializable o,int id) throws RemoteException {
	    super();
		SharedObject=o;
		objectStat=ObjectStatEnum.WRITE_LOCK_TAKEN;
		objectId=id;
	}


	public void setLock(ObjectStatEnum lock) {
		this.objectStat = lock;
	}


	public ObjectStatEnum getLock() {
		return this.objectStat;
	}


	public  void jvnLockRead() throws JvnException {

        System.out.println("JvnObject :lock read called");
		// TODO Auto-generated method stub

		//synchronized(this) {// sur l'objet ou sur son etat
			if(objectStat==ObjectStatEnum.READ_LOCK_CACHED
            //        ||objectStat==ObjectStatEnum.READ_LOCK_TAKEN
            )
			{

				System.out.println("JvnObject :lock read (READ_LOCK_CACHED)");
				objectStat=ObjectStatEnum.READ_LOCK_TAKEN;

			}else if(objectStat==ObjectStatEnum.No_LOCK){
				System.out.println("JvnObject :lock read (No_LOCK)");
				SharedObject=JvnServerImpl.jvnGetServer().jvnLockRead(this.jvnGetObjectId());
				objectStat=ObjectStatEnum.READ_LOCK_TAKEN;

			}else if(objectStat==ObjectStatEnum.WRITE_LOCK_CACHED){
				System.out.println("JvnObject :lock read (WRITE_LOCK_CACHED)");
				objectStat=ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED;

			}else {
			throw new JvnException("can't do lock read for the stat : "+this.objectStat );
			//si objectStat==write lock taken on le laisse comme il est
			}

		
	}
	
	public  void jvnLockWrite() throws JvnException {
		System.out.println("JvnObject :lock write called");
		// TODO Auto-generated method stub
		//synchronized(this) {// sur l'objet ou sur son etat
			if(objectStat==ObjectStatEnum.WRITE_LOCK_CACHED
                    || objectStat==ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED)
			{
				System.out.println("JvnObject :lock write (WRITE_LOCK_CACHED et READ_LOCK_TAKEN_WRITE_LOCK_CACHED)");

				objectStat=ObjectStatEnum.WRITE_LOCK_TAKEN;
			}else if(objectStat==ObjectStatEnum.No_LOCK
					||objectStat==ObjectStatEnum.READ_LOCK_TAKEN
					||objectStat==ObjectStatEnum.READ_LOCK_CACHED){
				System.out.println("JvnObject :lock write (WRITE_LOCK_TAKEN et No_LOCK ..)");

				//retourne null ou quoi en cas d'erreur??? a verifier !!
				SharedObject=JvnServerImpl.jvnGetServer().jvnLockWrite(this.jvnGetObjectId());
				objectStat=ObjectStatEnum.WRITE_LOCK_TAKEN;
			}else {
				System.out.println("JvnObject :lock write (exception ..)");
				throw new JvnException("can't do lock write for object state : "+this.objectStat);
			}
		//}
	}

    synchronized public     void jvnUnLock() throws JvnException {
		System.out.println("JvnObject :unlock called");
		// TODO Auto-generated method stub
	//	synchronized(this) {// sur l'objet ou sur son etat
        if(objectStat==ObjectStatEnum.READ_LOCK_TAKEN) {
				System.out.println("JvnObject :READ_LOCK_TAKEN");
				objectStat=ObjectStatEnum.READ_LOCK_CACHED;
				notify();
			}else if(objectStat==ObjectStatEnum.WRITE_LOCK_TAKEN) {
				System.out.println("JvnObject :WRITE_LOCK_TAKEN");
				objectStat=ObjectStatEnum.WRITE_LOCK_CACHED;
				notify();
			}else if(objectStat== ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED){
				System.out.println("JvnObject :READ_LOCK_TAKEN_WRITE_LOCK_CACHED");
				//on livére seulement le read on ne touche pas au write
				notify();
				objectStat=ObjectStatEnum.WRITE_LOCK_CACHED;
			}else {
				System.out.println("JvnObject :exception");

				throw new JvnException("can't do unlock lock for the stat : "+this.objectStat );
			}
		
//			notify();
	//	}
	}

	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
	
		if(objectId<0)
			throw new JvnException("Id nagative , not valid !");
		return objectId;
	}

	/**
	 * A VOIR
	 *
	 * @return
	 * @throws JvnException
	 */
	//retourne l'objet si il a lock teken ou bien retourne seulement l'etat de l'objet
	public Serializable jvnGetObjectState() throws JvnException {
		// TODO Auto-generated method stub
		synchronized(this){
			return SharedObject ;
		}

	}

    synchronized public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub
		System.out.println("JvnObject :Invalidate reader called");
		
		if(objectStat==ObjectStatEnum.READ_LOCK_TAKEN) {
				System.out.println("JvnObject :Invalidate reader READ_LOCK_TAKEN");
				//while(objectStat==ObjectStatEnum.READ_LOCK_TAKEN) {//en attente d'une ecriture d'un autre utilisateur
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			//	}
				objectStat=ObjectStatEnum.No_LOCK;

			}//pour eviter le while et la suite en sequentiel
			else if(objectStat==ObjectStatEnum.WRITE_LOCK_CACHED
			   ||objectStat==ObjectStatEnum.WRITE_LOCK_TAKEN) {
				throw new JvnException("case not permitted ");

			}else if (objectStat==ObjectStatEnum.READ_LOCK_CACHED) {
			    System.out.println("JvnObject :Invalidate reader READ_LOCK_CACHED et READ_LOCK_TAKEN_WRITE_LOCK_CACHED");
				objectStat=ObjectStatEnum.No_LOCK;
			}

			else if(objectStat==ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                objectStat=ObjectStatEnum.No_LOCK;

            }

	}

    synchronized public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		System.out.println("JvnObject :Invalidate writer called");

			if(objectStat==ObjectStatEnum.WRITE_LOCK_TAKEN) {
				System.out.println("JvnObject :Invalidate writer WRITE_LOCK_TAKEN ");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                objectStat=ObjectStatEnum.No_LOCK;
			}else if(objectStat==ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED){
				System.out.println("JvnObject :Invalidate writer  WRITE_LOCK_CACHED");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
				objectStat=ObjectStatEnum.No_LOCK;
				
			}
			else if(objectStat==ObjectStatEnum.WRITE_LOCK_CACHED){
                objectStat=ObjectStatEnum.No_LOCK;
            }
			else if(objectStat==ObjectStatEnum.READ_LOCK_CACHED
					||objectStat==ObjectStatEnum.READ_LOCK_TAKEN) {
				throw new JvnException("case not permitted ");
			}

		return SharedObject;
	}

    synchronized public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		System.out.println("JvnObject :Invalidate writer for reader");
			if(objectStat==ObjectStatEnum.WRITE_LOCK_TAKEN) {
				System.out.println("JvnObject :Invalidate writer for reader WRITE_LOCK_TAKEN");
                try{
                    this.wait();
                }catch(InterruptedException e){

                }
				objectStat=ObjectStatEnum.READ_LOCK_CACHED;

			}else if(objectStat==ObjectStatEnum.WRITE_LOCK_CACHED) {//not sure
				System.out.println("JvnObject :Invalidate writer for reader WRITE_LOCK_CACHED");
				objectStat=ObjectStatEnum.READ_LOCK_CACHED;

			}
			else if(objectStat==ObjectStatEnum.READ_LOCK_TAKEN_WRITE_LOCK_CACHED) {
                try{
                    this.wait();
                }catch(InterruptedException e){

                }
                objectStat=ObjectStatEnum.READ_LOCK_TAKEN;
            }
		return SharedObject;
	}

    public void jvnSetToNoLock() throws JvnException {
        objectStat = ObjectStatEnum.No_LOCK;
    }

}
