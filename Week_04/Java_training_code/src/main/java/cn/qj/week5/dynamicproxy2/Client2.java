package cn.qj.week5.dynamicproxy2;

import java.lang.reflect.Proxy;

/**
 * 测试类
 * 在以上测试类中的 getAnimate 方法，在实际项目中一般建议使用一个工厂类使用工厂模式创建，这里简化起见直接写到 Client2 类中了。
 *
 * 从以上的代码结构中我们可以看到：
 *
 * 1、接口的定义和传统没什么两样
 *
 * 2、接口的实现和传统没什么两样
 *
 * 3、接口的调用和传统没什么两样，不一样的是修改 getAnimate 方法的实现，但这不会影响到主业务逻辑
 *
 * 4、但通过继承 AbstractProxy 类，可以在 Animate.printName() 和 Animate.getName() 方法的调用前后、异常发生时做一些逻辑处理工作。并且这些操作是可以通过配置进行动态修改的。
 */
public class Client2 {
 
	private static Animate getAnimate(){
		
		try{
			Class<?> cls = null;
			
			// 使用框架时，如何实例化animate会从配置文件中读取
			cls = Class.forName("com.csjl.tangram.test.Cat");
			Animate animate = (Animate)cls.newInstance();
			
			// 使用框架时，如何实现化 handler 会从配置文件中读取
			cls = Class.forName("com.csjl.tangram.test.RealProxy");
			RealProxy handler = (RealProxy)cls.newInstance();
			handler.setSubject(animate);
			
			Animate result = 
					(Animate)Proxy.newProxyInstance(
							animate.getClass().getClassLoader(), 
							animate.getClass().getInterfaces(),
							handler);
			
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	
	public static void f2(){
		Animate animate = getAnimate();
		System.out.println("我要调用接口了");
		animate.printName();
	}
	
	public static void main(String[] args){
		f2();
	}
}
//
//
//		根据前面说的面向接口编程，上面的几块代码中：
//		1是接口定义；
//		2是接口实现；
//		3和4是接口使用。而对方法的调用，是由自定义的代理类来实现的，那么代理在具体调用方法前、后，
//		以及调用发生异常时都可以进行一些动作。这就是Java能够实现AOP的前提条件。在上面的程序中，只是在调用前后打印了一些语句，实际项目中会复杂得多。
//
//		如果使用了AOP框架，上面的第３块代码中得到 Animate 实例的具体实现一般是由框架根据配置文件动态生成，使用哪个代理也可以由配置文件动态指定。当然在 spring AOP 中，机制会复杂得多、功能会强大得多。本文只描述对AOP基本思想的理解。
//
//		看看下面修改后的程序，应该可以对AOP思想有了比较清晰的理解
//
//		接口定义（Animate，同上，略过）
//
//		接口实现（Dog、Cat，同上，略过）