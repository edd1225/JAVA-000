#极客大学 Java 进阶训练营 JVM核心技术 week1 学习笔记 周四

# 一 JVM基础知识

###编程语言的特点 

- 面向过程、面向对象、面向函数
- 静态类型、动态类型
- 编译执行、解释执行
- 有虚拟机、无虚拟机
- 有GC、无GC

> Java是一种面向对象的，静态类型、编译运行，有JVM、GC和运行时的、跨平台的高级语言。



###编程语言跨平台

1.源代码跨平台 

2.二进制跨平台



###Java、C++、Rust的区别

> ==C/C++==完全相信而且惯着程序员，让大家自行管理内存，所以可以编写很自由的代码，
>
> 但一个不小心就会造成内存泄漏等问题导致程序崩溃。
>
> 
>
> ==Java/Golang==完全不相信程序员，但也惯着程序员。所有的内存生命周期都由JVM/运行
>
> 时统一管理。 在绝大部分场景下，你可以非常自由的写代码，而且不用关心内存到底是
>
> 什么情况。 内存使用有问题的时候，我们可以通过JVM来信息相关的分析诊断和调整。
>
> 这也是本课程的目标。
>
> 
>
> ==Rust==语言选择既不相信程序员，也不惯着程序员。 让你在写代码的时候，必须清楚明白
>
> 的用Rust的规则管理好你的变量，好让机器能明白高效地分析和管理内存。 但是这样会
>
> 导致代码不利于人的理解，写代码很不自由，学习成本也很高。

### 字节码、类加载器、虚拟机

![image-20201021221441897](./README.assets/image-20201021221441897.png)

# 二 Java字节码技术

## 什么是字节码？

Java bytecode 由单字节(byte)的指令组成，理论上最多支持 256 个操作码(opcode)。实际上Java只使用了200左右的操作码， 还有一些操作码则保留给调试操作。

## 字节码分类

==根据指令的性质，主要分为四个大类：==

1. 栈操作指令，包括与局部变量交互的指令

2. 程序流程控制指令

3. 对象操作指令，包括方法调用指令

4. 算术运算以及类型转换指令

## 如何编译和反编译？

```java
package demo.jvm0104;

/**
 * Copyright @ 2019 Citycloud Co. Ltd.
 * All right reserved.
 *
 * @author edd1225
 * @Description: java类作用描述
 * @create 2020/10/20 22:23
 **/
public class HelloByteCode {
    public static void main(String[] args) {
        HelloByteCode  obj=new HelloByteCode();
    }
}

```
1. 使用 javac -g Hello.java编译java源代码

2. 使用 javap -c -verbose Hello 反编译class文件成助记符文件（汇编语言形式）

   ###简单例子

```
  1 Compiled from "HelloByteCode.java" 
  2 public class demo.jvm0104.HelloByteCode { 
  3   public demo.jvm0104.HelloByteCode(); 
  4     Code: 
  5        0: aload_0 
  6        1: invokespecial #1                  // Method java/lang/Object."<init>":()V 
  7        4: return 
  8  
  9   public static void main(java.lang.String[]); 
 10     Code: 
 11        0: new           #2                  // class demo/jvm0104/HelloByteCode 
 12        3: dup 
 13        4: invokespecial #3                  // Method "<init>":()V 
 14        7: astore_1 
 15        8: return 
 16 }

```




###复杂点的例子
```
Classfile /Users/qianjiang/tmp/JAVA-000/Week_01/Java_training_code/src/main/java/demo/jvm0104/HelloByteCode.class
  Last modified 2020-10-21; size 301 bytes
  MD5 checksum dbfeb2d66f629f224cfdc18d5e24f4ad
  Compiled from "HelloByteCode.java"
public class demo.jvm0104.HelloByteCode
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #4.#13         // java/lang/Object."<init>":()V
   #2 = Class              #14            // demo/jvm0104/HelloByteCode
   #3 = Methodref          #2.#13         // demo/jvm0104/HelloByteCode."<init>":()V
   #4 = Class              #15            // java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = Utf8               Code
   #8 = Utf8               LineNumberTable
   #9 = Utf8               main
  #10 = Utf8               ([Ljava/lang/String;)V
  #11 = Utf8               SourceFile
  #12 = Utf8               HelloByteCode.java
  #13 = NameAndType        #5:#6          // "<init>":()V
  #14 = Utf8               demo/jvm0104/HelloByteCode
  #15 = Utf8               java/lang/Object
{
  public demo.jvm0104.HelloByteCode();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 12: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: new           #2                  // class demo/jvm0104/HelloByteCode
         3: dup
         4: invokespecial #3                  // Method "<init>":()V
         7: astore_1
         8: return
      LineNumberTable:
        line 14: 0
        line 15: 8
}
SourceFile: "HelloByteCode.java"
```

## 字节码运行时结构

每个线程都会创建自己的线程栈，用于存储栈帧，每一次方法调用，JVM都会自动创建一个栈帧。



栈帧组成：

- Class引用，指向常量池 在堆内存中或元数据空间
- 局部变量数组
- 操作数栈





###方法调用的指令：

> invokestatic，顾名思义，这个指令用于调用某个类的静态方法，这也是方法调用指令中最快的一个。
>
> invokespecial, 我们已经学过了, invokespecial 指令用来调用构造函数，但也可以用于调用同一个类中的 private 方法, 以及可见的超类方法。
>
> invokevirtual，如果是具体类型的目标对象，invokevirtual用于调用公共，受保护和打包私有方法。
>
> invokeinterface，当要调用的方法属于某个接口时，将使用 invokeinterface 指令。
>
> invokedynamic,，JDK7新增加的指令是实现“动态类型语言”（Dynamically Typed  Language）支持而进行的改进之一，同时也是JDK 8以后支持的lambda表达式的实现基础。

# 三  JVM类加载器

## 类的声明周期

![image-20201021224146249](./README.assets/image-20201021224146249.png)

1. 加载（Loading）：找class文件

2. 验证（Verification）：验证格式、依赖

3. 准备（Preparation）：静态字段、方法表

4. 解析（Resolution）：符号解析为引用

5. 初始化（Initialization）：构造器、静态变量赋值、静态代码块

6. 使用（Using）

7. 卸载（Unloading）



## 类的加载初始化时机

1. 当虚拟机启动时，初始化用户指定的主类，就是启动执行的 main方法所在的类；

2. 当遇到用以新建目标类实例的 new 指令时，初始化 new 指令的目标类，就是new一个类的时候要初始化；

3. 当遇到调用静态方法的指令时，初始化该静态方法所在的类；

4. 当遇到访问静态字段的指令时，初始化该静态字段所在的类；

5. 子类的初始化会触发父类的初始化；

6. 如果一个接口定义了 default 方法，那么直接实现或者间接实现该接口的类的初始化，会触发该接口的初始化；

7. 使用反射 API 对某个类进行反射调用时，初始化这个类，其实跟前面一样，反射调用要么是已经有实例了，要么是静态方法，都需要初始化；

8. 当初次调用 MethodHandle 实例时，初始化该 MethodHandle 指向的方法所在的类。

## 类不初始化（可能会加载）

1. 通过子类引用父类的静态字段，只会触发父类的初始化，而不会触发子类的初始化。

2. 定义对象数组，不会触发该类的初始化。

3. 常量在编译期间会存入调用类的常量池中，本质上并没有直接引用定义常量的类，不会触发定义常量所在的类。

4. 通过类名获取Class对象，不会触发类的初始化，Hello.class不会让Hello类初始化。

5. 通过Class.forName加载指定类时，如果指定参数initialize为false时，也不会触发类初始化，其实这个参数是告诉虚拟机，是否要对类进行初始化。Class.forName(“jvm.Hello”)默认会加载Hello类。

6. 通过ClassLoader默认的loadClass方法，也不会触发初始化动作（加载了，但是不初始化）。

## 加载器类型

![image-20201021224542817](./README.assets/image-20201021224542817.png)

###三类加载器：

1. 启动类加载器（BootstrapClassLoader）

2. 扩展类加载器（ExtClassLoader）

3. 应用类加载器（AppClassLoader）

## 加载器特点

![image-20201021224454427](./README.assets/image-20201021224454427.png)

1. 双亲委托
2. 加载依赖
3. 缓存

###添加引用类的几种方式

1、放到jdk的lib/ext下，或者-Djava.ext.dirs

2、 java –cp/classpath或者class文件放到当前路径

3、自定义Classloader加载

4、拿到当前执行类的Classloader，反射调用AddUrl方法添加jar或路径(JDK9无效)。

## 自定义加载器 Classloader

###1.加载的一个Hello类：

```java
package jvm; 
public class Hello { 
static { 
 	System.out.println("Hello Class Initialized!"); 
	} 
} 
```

###2. HelloClassLoader
```java
package jvm; 

import java.util.Base64; 

public class HelloClassLoader extends ClassLoader { 

	public static void main(String[] args) { 

	try { 
      new HelloClassLoader().findClass("jvm.Hello").newInstance(); // 加载并初始化Hello类 
	 } catch (ClassNotFoundException e) { 
 		e.printStackTrace(); 
 	} catch (IllegalAccessException e) { 
 		e.printStackTrace(); 
	} catch (InstantiationException e) { 
 		e.printStackTrace(); 
 	} 
} 

@Override 

 protected Class<?> findClass(String name) throws ClassNotFoundException{
String helloBase64="yv66vgAAADQAHwoABgARCQASABMIABQKABUAFgcAFwcAGAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTn" +
"VtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQALTGp2bS9IZWxsbzsBAAg8Y2xpbml0PgEAClNvdXJjZUZpbGUBAAp" +
"IZWxsby5qYXZhDAAHAAgHABkMABoAGwEAGEhlbGxvIENsYXNzIEluaXRpYWxpemVkIQcAHAwAHQAeAQAJanZtL0hlbGxvAQAQamF2YS9s" +
"YW5nL09iamVjdAEAEGphdmEvbGFuZy9TeXN0ZW0BAANvdXQBABVMamF2YS9pby9QcmludFN0cmVhbTsBABNqYXZhL2lvL1ByaW50U3RyZ" +
"WFtAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgAhAAUABgAAAAAAAgABAAcACAABAAkAAAAvAAEAAQAAAAUqtwABsQAAAA" +
"IACgABAAkAAAAvAAEAAQAAAAUqtwABsQAAAAIACgAAAAYAAQAAAAMACwAAAAwAAQAAAAUADAANAAAACAAOAAgAAQAJAAAAJQ"; 

		byte[] bytes = decode(helloBase64); 
		 return defineClass(name,bytes,0,bytes.length); 
	} 
		public byte[] decode(String base64){ 
		return Base64.getDecoder().decode(base64); 
	} 
}

```

###直接执行这个类： 
```shell
 java jvm.HelloClassLoader 

 Hello Class Initialized! 
```

> 可以看到达到了我们的目的，成功执行了Hello类的代码，但是完全不需要有Hello这个 类的class文件。此外，需要说明的是两个没有关系的自定义类加载器之间加载的类是不共享的（只共享父类加载器，兄弟之间不共享），这样就可以实现不同的类型沙箱的隔离性，我们可以用多个类加载器，各自加载同一个类的不同版本，大家可以相互 之间不影响彼此，从而在这个基础上可以实现类的动态加载卸载，热插拔的插件机制 等，具体信息大家可以参考OSGi等模块化技术。 

### 一些实用技巧

**1）如何排查找不到Jar包的问题？**

有时候我们会面临明明已经把某个jar加入到了环境里，可以运行的时候还是找不到。 那么我们有没有一种方法，可以直接看到各个类加载器加载了哪些jar，以及把哪些路径加到了classpath里？答案是肯定的，代码如下：

```java
package jvm; 

import java.lang.reflect.Field; 
import java.net.URL; 
import java.net.URLClassLoader; 
import java.util.ArrayList; 

public class JvmClassLoaderPrintPath { 


public static void main(String[] args) { 

 // 启动类加载器 
	URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs(); 
	System.out.println("启动类加载器"); 
	
  for(URL url : urls) { 
		System.out.println(" ==> " +url.toExternalForm()); 
  } 

// 扩展类加载器 
	printClassLoader("扩展类加载器", JvmClassLoaderPrintPath.class.getClassLoader 

// 应用类加载器 
 	printClassLoader("应用类加载器", JvmClassLoaderPrintPath.class.getClassLoader 
} 
                  
 public static void printClassLoader(String name, ClassLoader CL){ 
    if(CL != null) { 
    	System.out.println(name + " ClassLoader ‐> " + CL.toString()); 
    	printURLForClassLoader(CL); 
     }else{ 
		System.out.println(name + " ClassLoader ‐> null"); 
	 	} 
 } 

   public static void printURLForClassLoader(ClassLoader CL){ 
     Object ucp = insightField(CL,"ucp"); 
     Object path = insightField(ucp,"path");
     ArrayList ps = (ArrayList) path;41 for (Object p : ps){ 
      System.out.println(" ==> " + p.toString()); 
		} 
 } 



	private static Object insightField(Object obj, String fName) { 
     try { 
     Field f = null; 
     if(obj instanceof URLClassLoader){ 
   			 f = URLClassLoader.class.getDeclaredField(fName); 
    	}else{ 
    		f = obj.getClass().getDeclaredField(fName); 
     } 
     		f.setAccessible(true); 
     return f.get(obj); 
     } catch (Exception e) { 
     e.printStackTrace(); 
     return null; 
     } 
	} 
} 
``````
代码执行结果如下： 
```
启动类加载器 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/resources.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/rt.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/sunrsasign.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jsse.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jce.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/charsets.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jfr.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/classes 

扩展类加载器 ClassLoader ‐> sun.misc.Launcher$ExtClassLoader@15db9742 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/access‐bridge 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/cldrdata.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/dnsns.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/jaccess.jar16 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/jfxrt.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/localedata.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/nashorn.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunec.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunjce_provider 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunmscapi.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunpkcs11.jar 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/zipfs.jar 

应用类加载器 ClassLoader ‐> sun.misc.Launcher$AppClassLoader@73d16e93 
==> file:/D:/git/studyjava/build/classes/java/main/ 
==> file:/D:/git/studyjava/build/resources/main 
```

从打印结果，我们可以看到三种类加载器各自默认加载了哪些jar包和包含了哪些classpath的路径。 

**2）如何排查类的方法不一致的问题**

假如我们确定一个jar或者class已经在classpath里了，但是却总是提示 java.lang.NoSuchMethodError ，这是怎么回事呢？很可能是加载了错误的或 者重复加载了不同版本的jar包。这时候，用前面的方法就可以先排查一下，加载了具 体什么jar，然后是不是不同路径下有重复的class文件，但是版本不一样。 

**3）怎么看到加载了哪些类，以及加载顺序**

还是针对上一个问题，假如有两个地方有Hello.class，一个是新版本，一个是旧的， 怎么才能直观地看到他们的加载顺序呢？也没有问题，我们可以直接打印加载的类清 单和加载顺序。 

只需要在类的启动命令行参数加上 ‐XX:+TraceClassLoading 或者 ‐verbose 即可，注意需要加载java命令之后，要执行的类名之前，不然不起作用。例如： 

```

 java ‐XX:+TraceClassLoading jvm.HelloClassLoader 

[Opened D:\Program Files\Java\jre1.8.0_231\lib\rt.jar] 

[Loaded java.lang.Object from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar 

[Loaded java.io.Serializable from D:\Program Files\Java\jre1.8.0_231\lib\rt 

[Loaded java.lang.Comparable from D:\Program Files\Java\jre1.8.0_231\lib\rt 

[Loaded java.lang.CharSequence from D:\Program Files\Java\jre1.8.0_231\lib 

[Loaded java.lang.String from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar 

[Loaded java.lang.reflect.AnnotatedElement from D:\Program Files\Java\jre19 [Loaded java.lang.reflect.GenericDeclaration from D:\Program Files\Java\jre1 

[Loaded java.lang.reflect.Type from D:\Program Files\Java\jre1.8.0_231\lib 

[Loaded java.lang.Class from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar 

[Loaded java.lang.Cloneable from D:\Program Files\Java\jre1.8.0_231\lib\rt 

[Loaded java.lang.ClassLoader from D:\Program Files\Java\jre1.8.0_231\lib\ 

[Loaded java.lang.System from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar 

// ....... 此处省略了100多条类加载信息 

[Loaded jvm.Hello from __JVM_DefineClass__] 

[Loaded java.util.concurrent.ConcurrentHashMap$ForwardingNode from D:\Program 

Hello Class Initialized! 

[Loaded java.lang.Shutdown from D:\Program Files\Java\jre1.8.0_231\lib\rt. 

[Loaded java.lang.Shutdown$Lock from D:\Program Files\Java\jre1.8.0_231\lib 
```

上面的信息，可以很清楚的看到类的加载先后顺序，以及是从哪个jar里加载的，这样排查类加载的问题非常方便。 

**4）怎么调整或修改ext和本地加载路径？** 

从前面的例子我们可以看到，假如什么都不设置，直接执行java命令，默认也会加载非常多的jar包，怎么可以自定义加载哪些jar包呢？比如我的代码很简单，只加载rt.jar 行不行？答案是肯定的。 

```
1 $ java ‐Dsun.boot.class.path="D:\Program Files\Java\jre1.8.0_231\lib\rt.jar" 
启动类加载器 
==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/rt.jar 
扩展类加载器 ClassLoader ‐> sun.misc.Launcher$ExtClassLoader@15db9742 
应用类加载器 ClassLoader ‐> sun.misc.Launcher$AppClassLoader@73d16e93 
==> file:/D:/git/studyjava/build/classes/java/main/ 
==> file:/D:/git/studyjava/build/resources/main 
```
我们看到启动类加载器只加载了rt.jar，而扩展类加载器什么都没加载，这就达到了我们的目的。 

其中命令行参数 ‐Dsun.boot.class.path 表示我们要指定启动类加载器加载什么， 最基础的东西都在rt.jar这个包了里，所以一般配置它就够了。需要注意的是因为在 windows系统默认JDK安装路径有个空格，所以需要把整个路径用双引号括起来，如果路径没有空格，或是Linux/Mac系统，就不需要双引号了。 

参数 ‐Djava.ext.dirs 表示扩展类加载器要加载什么，一般情况下不需要的话可以直接配置为空即可。 

**5）怎么运行期加载额外的jar包或者class呢？** 

有时候我们在程序已经运行了以后，还是想要再额外的去加载一些jar或类，需要怎么做呢？ 

简单说就是不使用命令行参数的情况下，怎么用代码来运行时改变加载类的路径和方式。假如说，在 d:/app/jvm 路径下，有我们刚才使用过的Hello.class文件，怎么在 代码里能加载这个Hello类呢？ 

两个办法，一个是前面提到的自定义ClassLoader的方式，还有一个就是直接在当前的应用类加载器里，使用 

URLClassLoader类的方法addURL，不过这个方法是protected的，需要反射处理一下，然后又因为程序在启动时并没有显示加载Hello类，所以在添加完了classpath以后，没法直接显式初始化，需要使用Class.forName的方式来拿到已经加载的Hello类 （Class.forName("jvm.Hello")默认会初始化并执行静态代码块）。代码如下： 
```java
package jvm; 
import java.lang.reflect.InvocationTargetException; 
import java.lang.reflect.Method; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.net.URLClassLoader; 
public class JvmAppClassLoaderAddURL { 
	public static void main(String[] args) { 

     String appPath = "file:/d:/app/"; 
		 URLClassLoader urlClassLoader = (URLClassLoader) JvmAppClassLoaderAddURL ;
		try { 
			Method addURL = URLClassLoader.class.getDeclaredMethod("addURL");
      addURL.setAccessible(true); 
      
      URL url = new URL(appPath); 
      addURL.invoke(urlClassLoader, url); 
      Class.forName("jvm.Hello"); // 效果跟Class.forName("jvm.Hello").newInstance()一样 
    } catch (Exception e) { 
    e.printStackTrace(); 
  	}
  } 
} 
```
执行以下，结果如下： 
```shell
java JvmAppClassLoaderAddURL 

Hello Class Initialized! 
```
结果显示Hello类被加载，成功的初始化并执行了其中的代码逻辑。 

# 四 JVM内存模型


##JVM内存结构
> 每个线程都只能访问自己的线程栈。
>
> 每个线程都不能访问(看不见)其他线程的局部变量。
>
> 所有原生类型的局部变量都存储在线程栈中，因此对其他线程是不可见的。
>
> 线程可以将一个原生变量值的副本传给另一个线程，但不能共享原生局部变量本身。
>
> 堆内存中包含了Java代码中创建的所有对象，不管是哪个线程创建的。 其中也涵盖了包装类型（例如Byte，Integer，Long等）。
>
> 不管是创建一个对象并将其赋值给局部变量， 还是赋值给另一个对象的成员变量， 创建的对象都会被保存到堆内存中。

## 为什么要区分栈和堆？

- 程序执行的时候，需要小的数据空间并且执行完就释放，也需要存活时间很长的，并且大小不确定的。
- 栈是可以对操作数进行操作，堆具有生命周期。



# ---------------------------------------------------------------------------------------------------------------

# ---------------------------------------------------------------------------------------------------------------

# ---------------------------------------------------------------------------------------------------------------

# 

# 极客大学 Java 进阶训练营 JVM核心技术 week1 学习笔记 周六

#JVM 核心技术（二）：工具与 GC 策略

##1. JDK 内置命令行工具

###JVM 命令行工具 JVM 命令行工具 

| 工    具  | 简介                    |
| --------- | ----------------------- |
| **java**  | Java 应用的启动程序     |
| **javac** | JDK 内置的编译工具      |
| **javap** | 反编译 class 文件的工具 |
| **javadoc**   | 根据 Java 代码和标准注释,自动生成相关的API说明文档           |
| **javah**     | JNI 开发时, 根据 java 代码生成需要的 .h文件。                |
| **extcheck**  | 检查某个 jar 文件和运行时扩展 jar 有没有版本冲突，很少使用   |
| **jdb**       | Java Debugger ; 可以调试本地和远端程序, 属于 JPDA 中的一个 demo 实现, 供其他调试器参考。开发时很少使用 |
| **jdeps**     | 探测 class 或 jar 包需要的依赖                               |
| **jar**       | 打包工具，可以将文件和目录打包成为 .jar 文件；.jar 文件本质上就是 zip 文件, 只是后缀不同。使用时按顺序对应好选项和参数即可。 |
| **keytool**   | 安全证书和密钥的管理工具; （支持生成、导入、导出等操作）     |
| **jarsigner** | JAR 文件签名和验证工具                                       |
| **policytool**    | 实际上这是一款图形界面工具, 管理本机的 Java 安全策略         |

**JVM** **命令行工具**

| 工具               | 简介                              |
| ------------------ | --------------------------------- |
| **jps/jinfo**      | 查看 java 进程                    |
| **jstat**          | 查看 JVM 内部 gc 相关信息         |
| **jmap**           | 查看 heap 或类占用空间统计        |
| **jstack**         | 查看线程信息                      |
| **jcmd**           | 执行 JVM 相关分析命令（整合命令） |
| **jrunscript/jjs** | 执行 js 命令                      |



**JVM** **命令行工具 --jps/jinfo**
1

```
➜  Week_01 (main) ✗ jps
950
42798 Jps
23784 QuorumPeerMain
```
2
```
➜  Week_01 (main) ✗ jps -help
usage: jps [-help]
       jps [-q] [-mlvV] [<hostid>]
Definitions:
    <hostid>:      <hostname>[:<port>]
```
3
```
➜  Week_01 (main) ✗ jps -lvm
42658 sun.tools.jps.Jps -lvm -Dapplication.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_77.jdk/Contents/Home -Xms8m
950  -Xms256m -Xmx2048m -XX:ReservedCodeCacheSize=240m -XX:+UseCompressedOops -Dfile.encoding=UTF-8 -XX:+UseConcMarkSweepGC -XX:SoftRefLRUPolicyMSPerMB=50 -ea -Dsun.io.useCanonCaches=false -Djava.net.preferIPv4Stack=true -Djdk.http.auth.tunneling.disabledSchemes="" -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -XX:ErrorFile=/Users/qianjiang/java_error_in_idea_%p.log -XX:HeapDumpPath=/Users/qianjiang/java_error_in_idea.hprof -javaagent:/Users/qianjiang/.jetbrains/jetbrains-agent-v3.2.0.0f1f.69e=6e68f9eb,LFq51qqupnaiTNn39w6zATiOTxZI2JYuRJEBlzmUDv4zeeNlXhMgJZVb0q5QkLr+CIUrSuNB7ucifrGXawLB4qswPOXYG7+ItDNUR/9UkLTUWlnHLX07hnR1USOrWIjTmbytcIKEdaI6x0RskyotuItj84xxoSBP/iRBW2EHpOc -Djb.vmOptionsFile=/Users/qianjiang/Library/Application Support/JetBrains/IntelliJIdea2020.2/idea.vmoptions -Didea.paths.selector=IntelliJIdea2020.2 -Didea.executable=idea -Didea.home.path=/Applications/IntelliJ IDEA.app/Contents -Didea.vendor.name=JetBrains
```

**JVM** **命令行工具****--jstat\***

```

➜  Week_01 (main) ✗  jstat -options
-class
-compiler
-gc
-gccapacity
-gccause
-gcmetacapacity
-gcnew
-gcnewcapacity
-gcold
-gcoldcapacity
-gcutil
-printcompilation




#各个参数详解
➜  Week_01 (main) ✗  jstat -options
-class 类加载(Class loader)信息统计. 
-compiler JIT 即时编译器相关的统计信息。
-gc GC相关的堆内存信息. 用法: jstat -gc -h 10 -t 864 1s 20
-gccapacity 各个内存池分代空间的容量。
-gccause 看上次 GC, 本次 GC（如果正在 GC中）的原因, 其他输出和 -gcutil 选项一致。
-gcnew 年轻代的统计信息. （New = Young = Eden + S0 + S1） 
-gcnewcapacity 年轻代空间大小统计. 
-gcold 老年代和元数据区的行为统计。
-gcoldcapacity old 空间大小统计. 
-gcmetacapacity meta 区大小统计. 
-gcutil GC 相关区域的使用率（utilization）统计。
-printcompilation 打印 JVM 编译统计信息。
```

###jstat  help：

```

-<option> required
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      An option reported by the -options option
  <vmid>        Virtual Machine Identifier. A vmid takes the following form:
                     <lvmid>[@<hostname>[:<port>]]
                Where <lvmid> is the local vm identifier for the target
                Java virtual machine, typically a process id; <hostname> is
                the name of the host running the target Java virtual machine;
                and <port> is the port number for the rmiregistry on the
                target host. See the jvmstat documentation for a more complete
                description of the Virtual Machine Identifier.
  <lines>       Number of samples between header lines.
  <interval>    Sampling interval. The following forms are allowed:
                    <n>["ms"|"s"]
                Where <n> is an integer and the suffix specifies the units as
                milliseconds("ms") or seconds("s"). The default units are "ms".
  <count>       Number of samples to take before terminating.
  -J<flag>      Pass <flag> directly to the runtime system.
  
  
  ➜  Week_01 (main) ✗ jstat  help
-<option> required
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      可以用的选项，查看详情使用 -options
  <vmid>        虚拟机标识符，格式 <lvmid>[@<hostname>[:<port>]]
  <lines>       标题行间隔的频率。
  <interval>   采样周期  <n>["ms"|"s"] 默认单位是豪秒 “ms".
  <count>      采用总次数
  -J<flag>    传给jstat底层JVM的 <flag> 参数
```



**JVM** **命令行工具 **--jstat**

演示:

```
jstat -gcutil pid 1000 1000
```

<img src="./README.assets/image-20201027152313116.png" alt="image-20201027152313116" style="zoom:67%;" />

演示

```
jstat -gc pid 1000 1000
```

<img src="./README.assets/image-20201027152504541.png" alt="image-20201027152504541" style="zoom:67%;" />

**JVM** **命令行工具  --jmap**

> 常用选项就 3 个：
>
> -heap 打印堆内存（/内存池）的配置和使用信息。
>
> -histo 看哪些类占用的空间最多, 直方图
>
> -dump:format=b,file=xxxx.hprof  Dump 堆内存。

演示:

```
jmap -heap pid

jmap -histo pid

jmap -dump:format=b,file=3826.hprof 3826
```

**JVM** **命令行工具 --jstack**

> -F 强制执行 thread dump. 可在 Java 进程卡死 （hung 住）时使用, 此选项可能需要系统权限。
>
> -m 混合模式(mixed mode),将 Java 帧和 native 帧一起输出, 此选项可能需要系统权限。
>
> -l 长列表模式. 将线程相关的 locks 信息一起输出， 比如持有的锁，等待的锁。

演示：

```
jstack pid -l
```

**JVM** **命令行工具 --jcmd**

```
Jcmd 综合了前面的几个命令

示例：
jcmd pid VM.version
jcmd pid VM.flags
jcmd pid VM.command_line
jcmd pid VM.system_properties
jcmd pid Thread.print
jcmd pid GC.class_histogram
jcmd pid GC.heap_info
```

操作

```
Week_01 (main) ✗ jcmd 950 help
950:
The following commands are available:
Compiler.CodeHeap_Analytics
Compiler.codecache
Compiler.codelist
Compiler.directives_add
Compiler.directives_clear
Compiler.directives_print
Compiler.directives_remove
Compiler.queue
GC.class_histogram
GC.class_stats
GC.finalizer_info
GC.heap_dump
GC.heap_info
GC.run
GC.run_finalization
JFR.check
JFR.configure
JFR.dump
JFR.start
JFR.stop
JVMTI.agent_load
JVMTI.data_dump
ManagementAgent.start
ManagementAgent.start_local
ManagementAgent.status
ManagementAgent.stop
Thread.print
VM.class_hierarchy
VM.classloader_stats
VM.classloaders
VM.command_line
VM.dynlibs
VM.flags
VM.info
VM.log
VM.metaspace
VM.native_memory
VM.print_touched_methods
VM.set_flag
VM.stringtable
VM.symboltable
VM.system_properties
VM.systemdictionary
VM.uptime
VM.version
help

For more information about a specific command use 'help <command>'.
```

**JVM** **命令行工具**  --jrunscript/jjs**

```
当curl命令用：
jrunscript -e "cat('http://www.baidu.com')"
执行js脚本片段
jrunscript -e "print('hello,kk.jvm'+1)"
执行js文件
jrunscript -l js -f /XXX/XXX/test.js
```



 ##2.JDK 内置图形化工具\2. JDK 内置图形化工具

**JVM** **图形化工具****--jconsole**

在命令行输入 jconsole 即可打开 本地 JVM 可以直接选择 远程 JVM 可以通过 JMX 方式连接

![image-20201027153603793](./README.assets/image-20201027153603793.png)

共有 6 个面板

第一个为概览，四项指标具体为：

堆内存使用量：此处展示的就是前面 Java 内存模型课程中提到的堆内存使用情况，从图上可以看到，堆内存使用了 94MB 左右，并且一直在增长。

线程：展示了 JVM 中活动线程的数量，当前时刻共有 17 个活动线程。

类：JVM 一共加载了 5563 个类，没有卸载类。

CPU 占用率：目前 CPU 使用率为 0.2%，这个数值非常低，且最高的时候也不到 3%，初步判断系统当前并没有什么负载和压力。

有如下几个时间维度可供选择：

1分钟、5分钟、10分钟、30分钟、1小时、2小时、3小时、6小时、12小时、1天、7天、1个月、3个月、6个月、1年、全部，一共是16 档。

当我们想关注最近1小时或者1分钟的数据，就可以选择对应的档。旁边的3个标签页(内存、线程、类)，也都支持选择时间范围。

<img src="./README.assets/image-20201027153913132.png" alt="image-20201027153913132" style="zoom:150%;" />

内存图表包括：

• 堆内存使用量，主要包括老年代（内存池 “PS Old Gen”）、新生代（“PS Eden Space”）、存活区（“PS Survivor Space”）；

• 非堆内存使用量，主要包括内存池“Metaspace”、 “Code Cache”、“Compressed Class Space”等；

可以分别选择对应的 6 个内存池。

通过内存面板，我们可以看到各个区域的内存使用和变化情况，并且可以：

1.手动执行 gc，见图上的标号1，点击按钮即可执行JDK 中的 System.gc()

2.通过图中右下角标号 2 的界面，可以看到各个内存池的百分比使用率，以及堆/非堆空间的汇总使用情况

3.从左下角标号 3 的界面，可以看到 JVM 使用的垃圾收集器，以及执行垃圾收集的次数，以及相应的时间消耗。

![image-20201027154449106](./README.assets/image-20201027154449106.png)

1.线程面板展示了线程数变化信息，以及监测到的线程列表。我们可以常根据名称直接查看线程的状态（运行还是等待中）和调用栈（正在执行什么操作）

2.特别地，我们还可以直接点击“检测死锁”按钮来检测死锁，如果没有死锁则会提示“未检测到死锁”。

![image-20201027154617587](./README.assets/image-20201027154617587.png)

类监控面板，可以直接看到 JVM 加载和卸载的类数量汇总信息，以及随着时间的动态变化。

![image-20201027154727981](./README.assets/image-20201027154727981.png)



VM 概要的数据有五个部分：

第一部分是虚拟机的信息；

第二部分是线程数量，以及类加载的汇总信息；

第三部分是堆内存和 GC 统计。

第四部分是操作系统和宿主机的设备信息，比如 CPU 数量、物理内存、虚拟内存等等。

第五部分是 JVM 启动参数和几个关键路径，这些信息其实跟 jinfo 命令看到的差不多。





**JVM** **图形化工具****--jvisualvm** 

这里查看idea的信息

![image-20201027154957758](./README.assets/image-20201027154957758.png)



![image-20201027155111769](./README.assets/image-20201027155111769.png)





![image-20201027155139296](./README.assets/image-20201027155139296.png)

![image-20201027155209762](./README.assets/image-20201027155209762.png)

**JVM** **图形化工具****--VisualGC**

idea 插件 VisualGC

![image-20201027155630795](./README.assets/image-20201027155630795.png)





**JVM** **图形化工具****--jmc**

Oracle Java Mission Control 是什么？Oracle Java Mission Control 是一个用于对 Java 应用程序进行管理、监视、概要分析和故障排除的工具套件。首次安装时，Java Mission Control 包括 JMX 控制台和 Java 飞行记录器。从 Mission Control 中可以轻松安装更多插件。

![image-20201027155848288](./README.assets/image-20201027155848288.png)

**JVM** **工具总结**

> jsonsole
>
> jvisualvm
>
> visualGC
>
> jmc
>
> jps/jinfo
>
> jstat
>
> jmap
>
> jstack
>
> jcmd
>
> jrunscript/jjs

##3. GC 的背景与一般原理

**GC** **的背景与一般原理**

为什么会有 GC,本质上是内存资源的有限性,因此需要大家共享使用，手工申请，手动释放。

举个例子：

<img src="./README.assets/image-20201027160604084.png" alt="image-20201027160604084" style="zoom:40%;" />



仓库与引用计数：计数为 0  简单粗暴，一般有效,实际情况复杂一点

仓库与仓库之间也有关系导致形成一个环，大家的计数永远不为 0（跟线程、事务死锁一个原理）

这些仓库都没法再用：内存泄漏->内存溢出

怎么改进呢？引用计数->引用跟踪**GC** 



####<img src="./README.assets/image-20201027160838799.png" alt="image-20201027160838799" style="zoom:33%;" />

<img src="./README.assets/image-20201027160918427.png" alt="image-20201027160918427" style="zoom:33%;" />

####标记清除算法（Mark and Sweep） 

• Marking（标记）: 遍历所有的可达对象，并在本地内存(native)中分门别类记下。

• Sweeping（清除）: 这一步保证了，不可达对象所占用的内存，在之后进行内存分配时可以重用。并行 GC 和 CMS 的基本原理。

优势：可以处理循环依赖，只扫描部分对象

除了清除，还要做压缩。怎么才能标记和清除清楚上百万对象呢？**答案就是 STW，**让全世界停止下来。

<img src="./README.assets/image-20201027161002939.png" alt="image-20201027161002939" style="zoom:33%;" />

分代假设：大部分新生对象很快无用存活较长时间的对象，可能存活更长时间。

<img src="./README.assets/image-20201027161040831.png" alt="image-20201027161040831" style="zoom:33%;" />

内存池划分不同类型对象不同区域，不同策略处理。



<img src="./README.assets/image-20201027161243321.png" alt="image-20201027161243321" style="zoom:50%;" />

由如下参数控制提升阈值

```
-XX：+MaxTenuringThreshold=15
```

老年代默认都是存活对象，采用移动方式：

1. 标记所有通过 GC roots 可达的对象；

2. 删除所有不可达对象；
3. 整理老年代空间中的内容，方法是将所有的存活对象复制，从老年代空间开始的地方依次存放。

持久代/元数据区

1.8 之前 -XX:MaxPermSize=256m

1.8 之后 -XX:MaxMetaspaceSize=256m



对象分配在新生代的 Eden 区，标记阶段 Eden 区存活的对象就会复制到存活区；

注意：为什么是复制，不是移动？？？大家想想两个存活区 from 和 to，互换角色。对象存活到一定周期会提升到老年 代。



![image-20201027161445466](./README.assets/image-20201027161445466.png)



可以作为 GC Roots 的对象

1. 当前正在执行的方法里的局部变量和输入参数

2. 活动线程（Active threads）

3. 所有类的静态字段（static field）

4. JNI 引用

此阶段暂停的时间，与堆内存大小,对象的总数没有直接关系，而是由存活对象（alive objects）的数量来决定。所以增加堆内存的大小并不会直接影响标记阶段占用的时间。

**总结一下，三者有什么优缺点**

清除算法

整理算法

复制算法

![image-20201027161821717](./README.assets/image-20201027161821717.png)

![image-20201027161831092](./README.assets/image-20201027161831092.png)

##4. 串行 GC/并行 GC（Serial GC/Parallel GC）



###串行 GC （ Serial GC ）/ParNewGC 

-XX：+UseSerialGC 配置串行 GC

串行 GC 对年轻代使用 mark-copy（标记-复制） 算法，对老年代使用 mark-sweep-compact（标记-清除-整理）算法。

两者都是单线程的垃圾收集器，不能进行并行处理，所以都会触发全线暂停（STW），停止所有的应用线程。

因此这种 GC 算法不能充分利用多核 CPU。不管有多少 CPU 内核，JVM 在垃圾收集时都只能使用单个核心。

CPU 利用率高，暂停时间长。简单粗暴，就像老式的电脑，动不动就卡死。



该选项只适合几百 MB 堆内存的 JVM，而且是单核 CPU 时比较有用。 

-XX：+USeParNewGC 改进版本的 Serial GC，可以配合 CMS 使用。





###并行   GC （  Parallel GC ） 

-XX：+UseParallelGC

-XX：+UseParallelOldGC

-XX：+UseParallelGC -XX:+UseParallelOldGC



年轻代和老年代的垃圾回收都会触发 STW 事件。

在年轻代使用 标记-复制（mark-copy）算法，在老年代使用 标记-清除-整理（mark-sweep compact）算法。 

-XX：ParallelGCThreads=N 来指定 GC 线程数， 其默认值为 CPU 核心数。



并行垃圾收集器适用于多核服务器，主要目标是增加吞吐量。因为对系统资源的有效使用，能达到更高的吞吐量: 

• 在 GC 期间，所有 CPU 内核都在并行清理垃圾，所以总暂停时间更短；

• 在两次 GC 周期的间隔期，没有 GC 线程在运行，不会消耗任何系统资源。



##5.CMS GC/G1 GC

###CMS GC （ Mostly Concurrent Mark and Sweep  Garbage Collector  ） 



-XX：+UseConcMarkSweepGC

其对年轻代采用并行 STW 方式的 mark-copy (标记-复制)算法，对老年代主要使用并发 mark sweep (标记-清除)算法。

CMS GC 的设计目标是避免在老年代垃圾收集时出现长时间的卡顿，主要通过两种手段来达成此

目标：

1. 不对老年代进行整理，而是使用空闲列表（free-lists）来管理内存空间的回收。

2. 在 mark-and-sweep （标记-清除） 阶段的大部分工作和应用线程一起并发执行。

也就是说，在这些阶段并没有明显的应用线程暂停。但值得注意的是，它仍然和应用线程争抢CPU 时间。默认情况下，CMS 使用的并发线程数等于 CPU 核心数的 1/4。

如果服务器是多核 CPU，并且主要调优目标是降低 GC 停顿导致的系统延迟，那么使用 CMS 是个很明智的选择。进行老年代的并发回收时，可能会伴随着多次年轻代的 minor GC。

###CMS GC 六个阶段 1 （STW） 

<img src="./README.assets/image-20201027163248881.png" alt="image-20201027163248881" style="zoom:50%;" />

> **阶段 1: Initial Mark（初始标记）**
>
> 阶段 2: Concurrent Mark（并发标记）
>
> 阶段 3: Concurrent Preclean（并发预清理）
>
> 阶段 4: Final Remark（最终标记）
>
> 阶段 5: Concurrent Sweep（并发清除）
>
> 阶段 6: Concurrent Reset（并发重置）

**这个阶段伴随着** **STW** **暂停**。初始标记的目标是标记所有的根对象，包括根对象直接引用的对象，以及被年轻代中所有存活对象所引用的对象（老年代单独回收）。

###CMS GC--   六个阶段 2

![image-20201027163501217](./README.assets/image-20201027163501217.png)

> 阶段 1: Initial Mark（初始标记）
>
> **阶段 2: Concurrent Mark（并发标记）**
>
> 阶段 3: Concurrent Preclean（并发预清理）
>
> 阶段 4: Final Remark（最终标记）
>
> 阶段 5: Concurrent Sweep（并发清除）
>
> 阶段 6: Concurrent Reset（并发重置）

在此阶段， CMS GC 遍历老年代，标记所有的存活对象，从前一阶段 “Initial Mark”找到的根对象开始算起。“并发标记” 阶段，就是与应用程序同时运行，不用暂停的阶段。

![image-20201027170224941](./README.assets/image-20201027170224941.png)



###CMS GC--  六个阶段 **3**

> 阶段 1: Initial Mark（初始标记）
>
> 阶段 2: Concurrent Mark（并发标记）
>
> **阶段 3: Concurrent Preclean（并发预清理）**
>
> 阶段 4: Final Remark（最终标记）
>
> 阶段 5: Concurrent Sweep（并发清除）
>
> 阶段 6: Concurrent Reset（并发重置）

此阶段同样是与应用线程并发执行的，不需要停止应用线程。 因为前一阶段【并发标记】与程序并发运行，可能有一些引用关系已经发生了改变。如果在并发标记过程中引用关系发生了变化，JVM会通过“Card（卡片）”的方式将发生了改变的区域标记为“脏”区，这就是所谓的卡片标记（Card Marking）。

![image-20201027170314544](./README.assets/image-20201027170314544.png)

###CMS GC— 六个阶段  4 （ STW )

> 阶段 1: Initial Mark（初始标记）
>
> 阶段 2: Concurrent Mark（并发标记）
>
> 阶段 3: Concurrent Preclean（并发预清理）
>
> **阶段 4: Final Remark（最终标记）**
>
> 阶段 5: Concurrent Sweep（并发清除）
>
> 阶段 6: Concurrent Reset（并发重置）

最终标记阶段是此次 **GC** 事件中的第二次（也是最后一次）STW**停顿。本阶段的目标是完成老年代中所有存活对象的标记.*因为之前的预清理阶段是并发执行的，有可能GC 线程跟不上应用程序的修改速度。所以需要一次STW暂停来处理各种复杂的情况。

通常 CMS 会尝试在年轻代尽可能空的情况下执行Final Remark阶段，以免连续触发多次 **STW** **事件。**

![image-20201027170358654](/Users/qianjiang/tmp/JAVA-000/Week_01/README.assets/image-20201027170358654.png)

###CMS GC— 六个阶段  **5**

> 阶段 1: Initial Mark（初始标记）
>
> 阶段 2: Concurrent Mark（并发标记）
>
> 阶段 3: Concurrent Preclean（并发预清理）
>
> 阶段 4: Final Remark（最终标记）
>
> **阶段 5: Concurrent Sweep（并发清除）**
>
> 阶段 6: Concurrent Reset（并发重置）

**此阶段与应用程序并发执行，不需要** **STW** **停顿。****JVM** **在此阶段删除不再使用的对象，并回收他们占用的内存空间。**



###CMS GC-- 六个阶段  **6**



CMS  垃圾收集器在减少停顿时间上做了很多复杂而有用的工作，用于垃圾回收的并发线程执行的同时，并不需要暂停应用线程。 当然，CMS也有一些缺点，其中最大的问题就是老年代内存碎片问题（因为不压缩），在某些情况下GC 会造成不可预测的暂停时间，特别是堆内存较大的情况下。 

> 阶段 1: Initial Mark（初始标记）
>
> 阶段 2: Concurrent Mark（并发标记）
>
> 阶段 3: Concurrent Preclean（并发预清理）
>
> 阶段 4: Final Remark（最终标记）
>
> 阶段 5: Concurrent Sweep（并发清除）
>
> **阶段 6: Concurrent Reset（并发重置）**

**此阶段与应用程序并发执行，重置 CMS算法相关的内部数据，为下一次 GC 循环做准备。**

CMS垃圾收集器在减少停顿时间上做了很多复杂而有用的工作，用于垃圾回收的并发线程执行的同时，并不需要暂停应用线程。 当然，CMS也有一些缺点，其中最大的问题就是老年代内存碎片问题（因为不压缩），在某些情况下GC会造成不可预测的暂停时间，特别是堆内存较大的情况下。





### G1 GC

![image-20201027171043559](./README.assets/image-20201027171043559.png)

G1 的全称是 Garbage-First，意为垃圾优先，哪一块的垃圾最多就优先清理它。

G1 GC 最主要的设计目标是：将 STW 停顿的时间和分布，变成可预期且可配置的。事实上，G1 GC 是一款软实时垃圾收集器，可以为其设置某项特定的性能指标。为了达成可预期停顿时间的指标，G1 GC 有一些独特的实现。

首先，堆不再分成年轻代和老年代，而是划分为多个（通常是 2048 个）可以存放对象的 小块堆区域(smaller heap regions)。每个小块，可能一会被定义成 Eden 区，一会被指定为 Survivor区或者Old 区。在逻辑上，所有的 Eden 区和 Survivor 区合起来就是年轻代，所有的 Old 区拼在一起那就是老年代

-XX：+UseG1GC -XX:MaxGCPauseMillis=50

![image-20201027171127391](./README.assets/image-20201027171127391.png)



这样划分之后，使得 G1 不必每次都去收集整个堆空间，而是以增量的方式来进行处理: 每次只处理一部分内存块，称为此次 GC 的回收集(collection set)。每次 GC 暂停都会收集所有年轻代的内存块，但一般只包含部分老年代的内存块。

G1 的另一项创新是，在并发阶段估算每个小堆块存活对象的总数。构建回收集的原则是：垃圾最多的小块会被优先收集。这也是 G1 名称的由来。





###G1 GC— 配置参数 

> -XX：+UseG1GC：启用 G1 GC； 
>
> ==-XX：G1NewSizePercent：==初始年轻代占整个 Java Heap 的大小，默认值为 5%； 
>
> ==-XX：G1MaxNewSizePercent：==最大年轻代占整个 Java Heap 的大小，默认值为 60%； 
>
> ==-XX：G1HeapRegionSize：==设置每个 Region 的大小，单位 MB，需要为 1，2，4，8，16，32 中的某个值，默认是堆内存的 1/2000。如果这个值设置比较大，那么大对象就可以进入 Region 了。
>
> ==-XX：ConcGCThreads：==与 Java 应用一起执行的 GC 线程数量，默认是 Java 线程的 1/4，减少这个参数的数值可能会提升并行回收的效率，提高系统内部吞吐量。如果这个数值过低，参与回收垃圾的线程不足，也会导致并行回收机制耗时加长。
>
> ==-XX：+InitiatingHeapOccupancyPercent==（简称 IHOP）：G1 内部并行回收循环启动的阈值，默认为 Java Heap 的 45%。这个可以理解为老年代使用大于等于 45% 的时候，JVM 会启动垃圾回收。这个值非常重要，它决定了在什么时间启动老年代的并行回收。
>
> ==-XX：G1HeapWastePercent：==G1停止回收的最小内存大小，默认是堆大小的 5%。GC 会收集所有的 Region 中的对象，但是如果下降到了 5%，就会停下来不再收集了。就是说，不必每次回收就把所有的垃圾都处理完，可以遗留少量的下次处理，这样也降低了单次消耗的时间。
>
> -XX：G1MixedGCCountTarget：设置并行循环之后需要有多少个混合 GC 启动，默认值是 8 个。老年代 Regions 的回收时间通常比年轻代的收集时间要长一些。所以如果混合收集器比较多，可以允许 G1 延长老年代的收集时间。
>
> -XX：+G1PrintRegionLivenessInfo：这个参数需要和 -XX:+UnlockDiagnosticVMOptions 配合启动，打印 JVM 的调试信息，每个 Region 里的对象存活信息。
>
> -XX：G1ReservePercent：G1 为了保留一些空间用于年代之间的提升，默认值是堆空间的 10%。因为大量执行回收的地方在年轻代（存活时间较短），所以如果你的应用里面有比较大的堆内存空间、比较多的大对象存活，这里需要保留一些内存。
>
> -XX：+G1SummarizeRSetStats：这也是一个 VM 的调试信息。如果启用，会在 VM 退出的时候打印出 Rsets 的详细总结信息。如果启用 -XX:G1SummaryRSetStatsPeriod 参数，就会阶段性地打印 Rsets 信息。
>
> -XX：+G1TraceConcRefinement：这个也是一个 VM 的调试信息，如果启用，并行回收阶段的日志就会被详细打印出来。
>
> ==-XX：+GCTimeRatio：==这个参数就是计算花在 Java 应用线程上和花在 GC 线程上的时间比率，默认是 9，跟新生代内存的分配比例一致。这个参数主要的目的是让用户可以控制花在应用上的时间，G1 的计算公式是 100/（1+GCTimeRatio）。这样如果参数设置为 9，则最多 10% 的时间会花在 GC 工作上面。Parallel GC 的默认值是 99，表示 1% 的时间被用在 GC 上面，这是因为 Parallel GC 贯穿整个 GC，而 G1 则根据 Region 来进行划分，不需要全局性扫描整个内存堆。
>
> -XX：+UseStringDeduplication：手动开启 Java String 对象的去重工作，这个是 JDK8u20 版本之后新增的参数，主要用于相同 String 避免重复申请内存，节约 Region 的使用。
>
> ==-XX：MaxGCPauseMills：==预期 G1 每次执行 GC 操作的暂停时间，单位是毫秒，默认值是 200 毫秒，G1 会尽量保证控制在这个范围内。





###G1 GC 的处理步骤 1

1、年轻代模式转移暂停（Evacuation Pause）

G1 GC 会通过前面一段时间的运行情况来不断的调整自己的回收策略和行为，以此来比较稳定地控制暂停时间。在应用程序刚启动时，G1 还没有采集到什么足够的信息，这时候就处于初始的 fully young 模式。当年轻代空间用满后，应用线程会被暂停，年轻代内存块中的存活对象被拷贝到存活区。如果还没有存活区，则任意选择一部分空闲的内存块作为存活区。拷贝的过程称为转移（Evacuation)，这和前面介绍的其他年轻代收集器是一样的工作原理。

2、并发标记（Concurrent Marking）

同时我们也可以看到，G1 GC 的很多概念建立在 CMS 的基础上，所以下面的内容需要对 CMS 有一定的理解。G1 并发标记的过程与 CMS 基本上是一样的。G1 的并发标记通过 Snapshot-At-The-Beginning（起始快照）的方式，在标记阶段开始时记下所有的存活对象。即使在标记的同时又有一些变成了垃圾。通过对象的存活信息，可以构建出每个小堆块的存活状态，以便回收集能高效地进行选择。这些信息在接下来的阶段会用来执行老年代区域的垃圾集。

有两种情况是可以完全并发执行的：

一、如果在标记阶段确定某个小堆块中没有存活对象，只包含垃圾；

二、在 STW 转移暂停期间，同时包含垃圾和存活对象的老年代小堆块。

当堆内存的总体使用比例达到一定数值，就会触发并发标记。这个默认比例是 45%，但也可以通过 JVM 参数 InitiatingHeapOccupancyPercent 来设置。和 CMS 一样，G1 的并发标记也是由多个阶段组成，

其中一些阶段是完全并发的，还有一些阶段则会暂停应用线程。

###G1 GC **的处理步骤** **2**

**阶段** 1: Initial Mark（初始标记）

此阶段标记所有从 GC 根对象直接可达的对象。

**阶段** 2: Root Region Scan（Root区扫描）

此阶段标记所有从 "根区域" 可达的存活对象。根区域包括：非空的区域，以及在标记过程中不得不收集的区域。

**阶段** 3: Concurrent Mark（并发标记）

此阶段和 CMS 的并发标记阶段非常类似：只遍历对象图，并在一个特殊的位图中标记能访问到的对象。

**阶段** 4: Remark（再次标记）

和 CMS 类似，这是一次 STW 停顿(因为不是并发的阶段)，以完成标记过程。 G1 收集器会短暂地停止应用线程，停止并发更新信息的写入，处理其中的少量信息，并标记所有在并发标记开始时未被标记的存活对象。

**阶段** 5: Cleanup（清理）

最后这个清理阶段为即将到来的转移阶段做准备，统计小堆块中所有存活的对象，并将小堆块进行排序，以提升GC 的效率，维护并发标记的内部状态。 所有不包含存活对象的小堆块在此阶段都被回收了。有一部分任务是并发的：例如空堆区的回收，还有大部分的存活率计算。此阶段也需要一个短暂的 STW 暂停。

**G1 GC** **的处理步骤** **3** 



3、转移暂停: 混合模式（Evacuation Pause (mixed)）

并发标记完成之后，G1将执行一次混合收集（mixed collection），就是不只清理年轻代，还将一部分老年代区域也加入到 回收集 中。混合模式的转移暂停不一定紧跟并发标记阶段。有很多规则和历史数据会影响混合模式的启动时机。比如，假若在老年代中可以并发地腾出很多的小堆块，就没有必要启动混合模式。

因此，在并发标记与混合转移暂停之间，很可能会存在多次 young 模式的转移暂停。具体添加到回收集的老年代小堆块的大小及顺序，也是基于许多规则来判定的。其中包括指定的软实时性能指标，存活性，以及在并发标记期间收集的 GC 效率等数据，外加一些可配置的 JVM 选项。混合收集的过程，很大程度上和前面的 fully-young gc 是一样的。







###G1 GC  **的注意事项**

特别需要注意的是，某些情况下 G1 触发了 Full GC，这时 G1 会退化使用 Serial 收集器来完成垃圾的清理工作， 它仅仅使用单线程来完成 GC 工作，GC 暂停时间将达到秒级别的。

1.并发模式失败

G1 启动标记周期，但在 Mix GC 之前，老年代就被填满，这时候 G1 会放弃标记周期。解决办法：增加堆大小， 或者调整周期（例如增加线程数-XX：ConcGCThreads 等）。

2.晋升失败

没有足够的内存供存活对象或晋升对象使用，由此触发了 Full GC(to-space exhausted/to-space overflow）。

**解决办法：**

​	a) 增加 –XX：G1ReservePercent 选项的值（并相应增加总的堆大小）增加预留内存量。

​	b) 通过减少 –XX：InitiatingHeapOccupancyPercent 提前启动标记周期。

​	c) 也可以通过增加 –XX：ConcGCThreads 选项的值来增加并行标记线程的数目。



3.巨型对象分配失败

当巨型对象找不到合适的空间进行分配时，就会启动 Full GC，来释放空间。

解决办法：增加内存或者增大 -XX：G1HeapRegionSize

**各个** **GC** **对比**

![image-20201027172709308](./README.assets/image-20201027172709308.png)



###常用的  **GC** **组合（重点）**

<img src="./README.assets/image-20201027172812775.png" alt="image-20201027172812775" style="zoom:33%;" />

常用的组合为：

（1）Serial+Serial Old 实现单线程的低延迟垃圾回收机制；

（2）ParNew+CMS，实现多线程的低延迟垃圾回收机制；

（3）Parallel Scavenge和Parallel Scavenge Old，实现多线程的高吞吐量垃圾回收机制；



###GC  **如何选择**

选择正确的 GC 算法，唯一可行的方式就是去尝试，一般性的指导原则：

1. 如果系统考虑吞吐优先，CPU 资源都用来最大程度处理业务，用 Parallel GC；

2. 如果系统考虑低延迟有限，每次 GC 时间尽量短，用 CMS GC；

3. 如果系统内存堆较大，同时希望整体来看平均 GC 时间可控，使用 G1 GC。

对于内存大小的考量：

1. 一般 4G 以上，算是比较大，用 G1 的性价比较高。

2. 一般超过 8G，比如 16G-64G 内存，非常推荐使用 G1 GC。

##6. ZGC/Shenandoah GC

**ZGC** **介绍**

![image-20201027173236823](./README.assets/image-20201027173236823.png)

![image-20201027173327761](./README.assets/image-20201027173327761.png)



![image-20201027173307617](./README.assets/image-20201027173307617.png)

```
-XX：+UnlockExperimentalVMOptions -XX:+UseZGC -Xmx16g
```

ZGC 最主要的特点包括:

1. GC 最大停顿时间不超过 10ms

2. 堆内存支持范围广，小至几百 MB 的堆空间，大至 4TB 的超大堆内存（JDK13 升至 16TB）

3. 与 G1 相比，应用吞吐量下降不超过 15%

4. 当前只支持 Linux/x64 位平台，JDK15 后支持 MacOS 和Windows 系统



**ShennandoahGC** **介绍**



![image-20201027173426780](./README.assets/image-20201027173426780.png)

```
-XX：+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -Xmx16g
```

Shenandoah GC 立项比 ZGC 更早，设计为GC 线程与应用线程并发执行的方式，通过实现垃圾回收过程的并发处理，改善停顿时间，使得 GC 执行线程能够在业务处理线程运行过程中进行堆压缩、标记和整理，从而消除了绝大部分的暂停时间。Shenandoah 团队对外宣称 Shenandoah GC 的暂停时间与堆大小无关，无论是 200 MB 还是 200 GB的堆内存，都可以保障具有很低的暂停时间（注意:并不像 ZGC 那样保证暂停时间在 10ms 以内）。



**ShennandoahGC** **与其他** **GC** **的** **STW** **比较**

![image-20201027173601057](/Users/qianjiang/tmp/JAVA-000/Week_01/README.assets/image-20201027173601057.png)



**GC** **总结**

到目前为止，我们一共了解了 Java 目前支持的所有 GC 算法，一共有 7 类:

1. 串行 GC（Serial GC）: 单线程执行，应用需要暂停；

2. 并行 GC（ParNew、Parallel Scavenge、Parallel Old）: 多线程并行地执行垃圾回收，关注与高吞吐；

3. CMS（Concurrent Mark-Sweep）: 多线程并发标记和清除，关注与降低延迟；

4. G1（G First）: 通过划分多个内存区域做增量整理和回收，进一步降低延迟；

5. ZGC（Z Garbage Collector）: 通过着色指针和读屏障，实现几乎全部的并发执行，几毫秒级别的延迟，线性扩展；

6. Epsilon: 实验性的 GC，供性能分析使用；

7. Shenandoah: G1 的改进版本，跟 ZGC 类似。

**GC** **总结**

可以看出 GC 算法和实现的演进路线:

1. 串行 -> 并行: 重复利用多核 CPU 的优势，大幅降低 GC 暂停时间，提升吞吐量。

2. 并行 -> 并发： 不只开多个 GC 线程并行回收，还将GC操作拆分为多个步骤，让很多繁重的任务和应用线程起并发执行，减少了单次 GC 暂停持续的时间，这能有效降低业务系统的延迟。

3. CMS -> G1： G1 可以说是在 CMS 基础上进行迭代和优化开发出来的，划分为多个小堆块进行增量回收，这样就更 进一步地降低了单次 GC 暂停的时间

4. G1 -> ZGC:：ZGC 号称无停顿垃圾收集器，这又是一次极大的改进。ZGC 和 G1 有一些相似的地方，但是底层的算法 和思想又有了全新的突破。

   脱离场景谈性能都是耍流氓”。

目前绝大部分 Java 应用系统，堆内存并不大比如 2G-4G 以内，而且对 10ms 这种低延迟的 GC 暂停不敏感，也就是说处 理一个业务步骤，大概几百毫秒都是可以接受的，GC 暂停 100ms 还是 10ms 没多大区别。另一方面，系统的吞吐量反 而往往是我们追求的重点，这时候就需要考虑采用并行 GC。

如果堆内存再大一些，可以考虑 G1 GC。如果内存非常大（比如超过 16G，甚至是 64G、128G），或者是对延迟非常 敏感（比如高频量化交易系统），就需要考虑使用本节提到的新 GC（ZGC/Shenandoah）。



#资料整理



class文件结构与格式 ------- 思维导图

![Java class文件结构](./README.assets/Java class文件结构.png)

jvm文档整理

[整理 jvm资料](https://github.com/edd1225/JAVA-000/blob/main/Week_01/%E6%95%B4%E7%90%86%20jvm%E8%B5%84%E6%96%99.md) 包括 

```
Java class文件结构（规范篇）
class文件结构与格式 ------- 思维导图
JVM优化之 -Xss -Xms -Xmx -Xmn 参数设置
```





