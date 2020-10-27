package cn.qj.week1.a2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName HelloClassLoader
 * @Description 自定义类加载器，加载一个255-x的字节码文件
 * 测试环境 mac
 * jdk (java version "1.8.0_77")
 * @Author edd1225(qianjiang)
 * @Date 2020/10/19 上午11:56
 **/
public class HelloClassLoader extends ClassLoader {

    public static void main(String[] args) {
        try {
            // 获取要加载的类对象，自定义类加载器
            Class<?> clz = new HelloClassLoader().findClass("Hello");
            // 获取要调用的方法
            Method hello = clz.getDeclaredMethod("hello");
            hello.setAccessible(true);
            // 调用指定实例的方法
            hello.invoke(clz.newInstance());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String filename="/Users/qianjiang/tmp/JAVA-000/Week_01/Java_training_code/src/main/java/cn/qj/week1/a2/Hello.xlass";
        String filePath = this.getClass().getResource(filename).getPath();


        System.out.println("加载xlass路径： "+filePath);

        File file = new File(filename);
        //
        int length = (int) file.length();
        //一次性将文件中的值都放入缓冲中
        byte[] bytes = new byte[length];
        try {
            new FileInputStream(file).read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return super.findClass(name);
        }
        for (int i = 0; i < length; i++) {
            //将字节码文件的值转换过来
            bytes[i] = (byte) (255 - bytes[i]);
        }
        return defineClass(name, bytes, 0, length);
    }
}
