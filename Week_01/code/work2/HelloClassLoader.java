import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName HelloClassLoader
 * @Description 自定义类加载器，加载一个255-x的字节码文件
 * @Author frode
 * @Date 2020/10/17 上午10:56
 **/
public class HelloClassLoader extends ClassLoader {

    public static void main(String[] args) {
        try {

            // 获取要加载的类对象
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
        String filePath = this.getClass().getResource("/Hello.xlass").getPath();
        File file = new File(filePath);
        int length = (int) file.length();
        byte[] bytes = new byte[length];//一次性将文件中的值都放入缓冲中
        try {
            new FileInputStream(file).read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return super.findClass(name);
        }
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) (255 - bytes[i]);//将字节码文件的值转换过来
        }
        return defineClass(name, bytes, 0, length);
    }
}
