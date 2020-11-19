package cn.qj.week5.dynamicproxy2;

import cn.qj.week5.dynamicproxy2.AbstractProxy;

import java.lang.reflect.Method;
//实现切片操作的代理类（RealProxy），实现代理类可以有多个，具体由哪个进行代理，可以由配置文件指定
public class RealProxy extends AbstractProxy {
 
	@Override
	public boolean beforeInvoke(Method method, Object[] args) {
		System.out.println("调用方法 " + method.getName() + " 之前的操作，看我能先准备点什么");
		return true;
	}
 
	@Override
	public Object afterInvoke(Method method, Object[] args, Object result) {
		System.out.println("完成对方法 " + method.getName() + " 的调用，看我需要做什么收尾工作");
		return result;
	}
 
}