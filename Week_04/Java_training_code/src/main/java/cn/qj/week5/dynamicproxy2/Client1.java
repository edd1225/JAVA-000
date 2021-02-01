package cn.qj.week5.dynamicproxy2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
 
public class Client1 {
 
	
	public static void main(String[] args){
		Cat cat = new Cat();
		
		InvocationHandler handler = new SimpleProxy(cat);
		
		Animate animate = 
				(Animate)Proxy.newProxyInstance(
						Cat.class.getClassLoader(), 
						Cat.class.getInterfaces(), 
						handler);
		
		System.out.println("Animate: " + animate.getClass().getName());
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>");
		animate.printName();
		System.out.println();
		System.out.println("animate.getName() = " + animate.getName());
		System.out.println(">>>>>>>>>>>>>>>>>>>>");
	}
}
//最后运行 Client 类的结果是：

//Animate: com.sun.proxy.$Proxy0
//>>>>>>>>>>>>>>>>>>>>
//Begin invoke method: printName
//This is cat!
//Finished invoke method: printName
//
//Begin invoke method: getName
//Finished invoke method: getName
//animate.getName() = cat


//把上面的代码分为几大块：
//
//		1、接口定义（Animate）
//
//		2、接口实现（Cat、Dog 类）
//
//		3、得到 Animate 实例（Client.main 方法中，Proxy.newProxInstance  语句之前的代码全部）
//
//		4、调用 Animate 方法，实现业务逻辑