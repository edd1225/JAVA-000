package cn.qj.week5.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/***
 *代理类
 */
public class ProxyHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        method.invoke(object, args);
        after();
        return null;
    }

    private Object object;

    public ProxyHandler(Object object) {
        this.object = object;
    }

    private void before() {
        System.out.println("proxy before method");
    }

    private void after() {
        System.out.println("proxy after method");
    }
}
