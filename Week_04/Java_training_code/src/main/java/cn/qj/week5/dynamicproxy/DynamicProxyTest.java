package cn.qj.week5.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/****
 * 测试动态代理
 */
public class DynamicProxyTest {

    public static void main(String[] args)  {

        ExampleServiceImpl exampleService = new ExampleServiceImpl();
        ClassLoader classLoader = exampleService.getClass().getClassLoader();
        Class[] interfaces = exampleService.getClass().getInterfaces();
        InvocationHandler handler = new ProxyHandler(exampleService);

        ExampleService proxy = (ExampleService) Proxy.newProxyInstance(classLoader, interfaces, handler);
        proxy.testCall();
    }
}