package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import annotation.Read;
import annotation.Write;

/**
 * 
 * @author karim
 *
 */
public class JvnProxy implements InvocationHandler{
	JvnObject object;

	private JvnProxy(Object object,String name) throws JvnException {
//		this.object = object;
		this.object=JvnServerImpl.jvnGetServer().jvnLookupObject(name);
		if(this.object==null) {
			this.object=JvnServerImpl.jvnGetServer().jvnCreateObject((Serializable)object);
			if(this.object==null) {
				throw new JvnException("object can't be created");
			}
			this.object.jvnUnLock();
			JvnServerImpl.jvnGetServer().jvnRegisterObject(name, this.object);
		}
	}
	
	public static Object nwInstance(Class obj,String name) throws JvnException{
//		Object o=Proxy.newProxyInstance(obj.getClassLoader(), obj.getInterfaces(), new JvnProxy(obj));
		return Proxy.newProxyInstance(obj.getClassLoader(),
									  obj.getInterfaces(), 
									  new JvnProxy(obj,name)
									  );
	}
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("Proxy invoke methode"+method.getName());
		boolean canInvoke=true;
		if(method.isAnnotationPresent(Read.class)) {
			this.object.jvnLockRead();
		}else if(method.isAnnotationPresent(Write.class)) {
			this.object.jvnLockWrite();
		}else {
			canInvoke=false;
		}
		if(canInvoke) {
			Object o=method.invoke(this.object.jvnGetObjectState(), args);
			this.object.jvnUnLock();
			return o;
		}
		return null;
}




}
