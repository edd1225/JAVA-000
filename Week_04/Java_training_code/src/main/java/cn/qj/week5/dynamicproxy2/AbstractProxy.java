package cn.qj.week5.dynamicproxy2;
 
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/***
 * 定义了抽象的代理类（AbstractProxy），定义了抽象的方法调用前、后的操作，具体怎么操作由子类完成
 */
public abstract class AbstractProxy implements InvocationHandler {
 
	private Object subject;
	
	public Object getSubject(){
		return subject;
	}
	
	public void setSubject(Object value){
		subject = value;
	}
	
	public abstract boolean beforeInvoke(Method method, Object[] args);
	
	public abstract Object afterInvoke(Method method, Object[] args, Object result);
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if(this.beforeInvoke(method, args) == false)
			return null;
		
		Object result = method.invoke(this.subject, args);
		
		return this.afterInvoke(method, args, result);
	}
}
