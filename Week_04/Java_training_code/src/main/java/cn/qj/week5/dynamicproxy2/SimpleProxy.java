package cn.qj.week5.dynamicproxy2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
 
public class SimpleProxy implements InvocationHandler {
 
	private Object subject;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("Begin invoke method: " + method.getName());
		Object result = method.invoke(this.subject, args);
		System.out.println("Finished invoke method: " + method.getName());
		
		return result;
	}
 
	public SimpleProxy(Object subject){
		this.subject = subject;
	}
}
