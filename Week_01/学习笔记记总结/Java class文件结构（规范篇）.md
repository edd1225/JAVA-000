# Java class文件结构（规范篇）

每一个class文件都对应着唯一一个类或者接口的定义信息，但是相对地，类或者接口并不一定都必须定义在文件里（比如类或者接口也可以通过类加载器直接生成）。我们通俗地将任意一个有效的类或者接口所应当满足的格式称为“class文件格式”，即使它不一定以磁盘文件的形式存在。

 Class文件是有8个字节为基础的字节流构成的，这些字节流之间都严格按照规定的顺序排列，并且字节之间不存在任何空隙，对于超过8个字节的数据，将按 照Big-Endian的顺序存储，也就是说高位字节存储在低的地址上面，而低位字节存储到高地址上面，其实这也是class文件要跨平台的关键，因为 PowerPC架构的处理采用Big-Endian的存储顺序，而x86系列的处理器则采用Little-Endian的存储顺序，因此为了Class文 件在各中处理器架构下保持统一的存储顺序，虚拟机规范必须对起进行统一。在Java JDK中，可以使用java.io.DataInput、java.io.DataOutput等接口和java.io.DataInputStream和java.io.DataOutputStream等类来访问这种格式的数据。

 Class文件结构采用类似C语言的结构体来存储数据的，主要有两类数据项，无符号数和表，无符号数用来表述数字，索引引用以及字符串等，比如 u1,u2,u4,u8分别代表1个字节，2个字节，4个字节，8个字节的无符号数，而表是有多个无符号数以及其它的表组成的复合结构。

------

### 一、class的文件结构

| 类型           | 名称                | 数量                    |      |
| -------------- | ------------------- | ----------------------- | ---- |
| u4             | magic               | 1                       |      |
| u2             | minor_version       | 1                       |      |
| u2             | major_version       | 1                       |      |
| u2             | constant_pool_count | 1                       |      |
| cp_info        | constant_pool       | constant_pool_count - 1 |      |
| u2             | access_flags        | 1                       |      |
| u2             | this_class          | 1                       |      |
| u2             | super_class         | 1                       |      |
| u2             | interfaces_count    | 1                       |      |
| u2             | interfaces          | interfaces_count        |      |
| u2             | fields_count        | 1                       |      |
| field_info     | fields              | fields_count            |      |
| u2             | methods_count       | 1                       |      |
| method_info    | methods             | methods_count           |      |
| u2             | attributes_count    | 1                       |      |
| attribute_info | attributes          | attributes_count        |      |

#### 1.1 魔数(u4 magic)

 每个Class文件的头4个字节称为魔数（magic），它的唯一作用是判断该文件是否为一个能被虚拟机接受的Class文件。它的值固定为0xCAFEBABE。



#### 1.2 class文件版本

`u2 minor_version`：副版本号
 `u2 major_version`：主版本号
 主副版本号共同构成了 Class 文件的格式版本号。譬如某个 Class 文件的主版本号为 M，副版本号为 m，那么这个Class 文件的格式版本号就确定为 M.m。一个 Java 虚拟机实例只能支持特定范围内的主版本号。不同版本的Java编译器编译的Class文件对应的版本是不一样的。高版本的虚拟机支持低版本的编译器编译的 Class文件结构。比如Java SE 6.0对应的虚拟机支持Java SE 5.0的编译器编译的Class文件结构，反之则不行。

#### 1.3 常量池计数器(constant_pool_count )

 常量池计数器，constant_pool_count 的值等于 constant_pool 表中的成员数加 1。constant_pool 表的索引值只有在大于 0 且小于 constant_pool_count 时才会被认为是有效的。（0表示不引用常量池的任一项）

#### 1.4 常量池(constant_pool)

 major_version之后是常量池（constant_pool）的入口，它是Class文件中与其他项目关联最多的数据类型，也是占用Class文件空间最大的数据项目之一。
 `常量池中主要存放两大类常量：字面量(Literal)和符号引用(Symbolic References)。`字面量比较接近于Java层面的常量概念，如文本字符串、被声明为final的常量值等。而符号引用总结起来则包括了下面三类常量：

- 类和接口的全限定名（即带有包名的Class名，如：com.sunny.common.TestClass）
- 字段的名称和描述符（private、static等描述符）
- 方法的名称和描述符（private、static等描述符）

 虚拟机在加载Class文件时才会进行动态连接，也就是说，Class文件中不会保存各个方法和字段的最终内存布局信息，因此，这些字段和方法的符号引用不经过转换是无法直接被虚拟机使用的。当虚拟机运行时，需要从常量池中获得对应的符号引用，再在类加载过程中的解析阶段将其替换为直接引用，并翻译到具体的内存地址中。

 这里说明下符号引用和直接引用的区别与关联：

- `符号引用`：符号引用以一组符号来描述所引用的目标，符号可以是任何形式的字面量，只要使用时能无歧义地定位到目标即可。符号引用与虚拟机实现的内存布局无关，引用的目标并不一定已经加载到了内存中。
- `直接引用`：直接引用可以是直接指向目标的指针、相对偏移量或是一个能间接定位到目标的句柄。直接引用是与虚拟机实现的内存布局相关的，同一个符号引用在不同虚拟机实例上翻译出来的直接引用一般不会相同。如果有了直接引用，那说明引用的目标必定已经存在于内存之中了。

 常量池中的每一项常量都是一个表，共有14种（JDK1.8）结构各不相同的表结构数据；

###### 1.4.1 常量池通用格式

 所有的常量池项都具有如下通用格式：



```undefined
cp_info {
    u1 tag;
    u1 info[];
}
```

 在常量池表中，每个cp_info项都必须以一个表示cp_info类型的单字节"tag"项开头。后面info[]数组的内容由tag的值所决定。有效的tag和对应的值如下表：

| 常量类型                    | 值   |
| --------------------------- | ---- |
| CONSTANT_Class              | 7    |
| CONSTANT_Fieldref           | 9    |
| CONSTANT_Methodref          | 10   |
| CONSTANT_InterfaceMethodref | 11   |
| CONSTANT_String             | 8    |
| CONSTANT_Integer            | 3    |
| CONSTANT_Float              | 4    |
| CONSTANT_Long               | 5    |
| CONSTANT_Double             | 6    |
| CONSTANT_NameAndType        | 12   |
| CONSTANT_Utf8               | 1    |
| CONSTANT_MethodHandle       | 15   |
| CONSTANT_MethodType         | 16   |
| CONSTANT_InvokeDynamic      | 18   |

###### 1.4.2 CONSTANT_Class_info 结构

 CONSTANT_Class_info 结构用于表示类或接口，格式如下：



```undefined
CONSTANT_Class_info {
    u1 tag;
    u2 name_index;
}
```

- `tag`：tag项的值为CONSTANT_Class(7)
- `name_index` :name_index项的值必须是堆常量池表的一个有效索引。常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，此结构代表一个有效的类或者接口二进制名称的内部形式。

###### 1.4.3 CONSTANT_Fieldref_info、CONSTANT_Methodref_info和CONSTANT_InterfaceMethodref_info结构

 字段、方法和接口方法由类似的结构表示：
 `字段`：



```undefined
CONSTANT_Fieldref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
```

 `方法`：



```undefined
CONSTANT_Methodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
```

 `接口方法`：



```undefined
CONSTANT_InterfaceMethodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
```

 这些结构各项说明如下：

- `tag`：
   CONSTANT_Fieldref_info 结构的tag项的值为CONSTANT_Fieldref(9)；
   CONSTANT_Methodref_info结构的tag项的值为CONSTANT_Methodref(10)
   CONSTANT_InterfaceMethodref_info结构的tag项的值为CONSTANT_InterfaceMethodref(11)
- `class_index`：
   class_index项的值必须是对常量池表的有效索引，常量池表在该索引处的项必须是CONSTANT_Class_info结构，此结构表示一个类或者接口，当前字段或方法时这个类或接口的成员。
   CONSTANT_Methodref_info结构的class_index项，表示的必须是类（而不能是接口）。
   CONSTANT_InterfaceMethodref_info结构的class_index项，表示的必须是接口类型。
   CONSTANT_Fieldref_info结构的class_index项既可以表示类也可以表示接口。
- `name_and_type_index`：
   name_and_type_index项的值必须是对常量池表的有效索引，常量池表在该索引处的项必须是CONSTANT_NameAndType_info结构，它表示当前字段或方法的名字和描述符。
   如果一个CONSTANT_Methodref_info结构的方法名以“<”开头，那么，方法名必须是特殊的<init>，即这个方法时实例初始化方法，它的返回类型必须是void。

###### 1.4.4 CONSTANT_String_info结构

 CONSTANT_String_info结构用于表示String类型的常量对象，其格式如下：



```undefined
CONSTANT_String_info {
    u1 tag;
    u2 string_index;
}
```

- `tag`：CONSTANT_String_info结构的tag项的值为CONSTANT_String(8)。
- `string_index`：string_index项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，此结构表示Unicode码点序列，这个序列最终会被初始化为一个String对象。

###### 1.4.5 CONSTANT_Integer_info和CONSTANT_Float_info结构



```cpp
CONSTANT_Integer_info和CONSTANT_Float_info 表示4字节（int和float）的数值常量；
```



```undefined
CONSTANT_Integer_info {
    u1 tag;
    u4 bytes;
}
```



```undefined
CONSTANT_Float_info {
    u1 tag;
    u4 bytes;
}
```

 这些结构说明如下：

- `tag`：
   CONSTANT_Integer_info结构的tag项的值是CONSTANT_Integer(3)。
   CONSTANT_Float_info结构的tag项的值是CONSTANT_Float(4)。
- `bytes`：
   CONSTANT_Integer_info结构的bytes项表示int常量的值，该值按照big-endian的顺序存储(也就是先存储高位字节)。
   CONSTANT_Float_info结构的bytes项按照IEEE754单精度浮点格式来表示float常量的值，该值按照big-endian的顺序存储(也就是先存储高位字节)。

###### 1.4.6 CONSTANT_Long_info和CONSTANT_Double_info结构



```cpp
CONSTANT_Long_info和CONSTANT_Double_info结构表示8字节(long和double)的数值常量。
```



```undefined
CONSTANT_Long_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}
```



```undefined
CONSTANT_Double_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}
```



```kotlin
在class文件的常量池表中，所有的8字节常量均占两个表成员（项）的空间。如果一个CONSTANT_Long_info或CONSTANT_Double_info结构的项在常量池表中的索引位n，则常量池表中下一个可用项的索引位n+2，此时常量池表中索引为n+1的项仍然有效但必须视为不可用。
```

- `tag`：
   CONSTANT_Long_info结构的tag项是CONSTANT_Long(5)。
   CONSTANT_Double_info结构的tag项是CONSTANT_Double(6)。
- `high_bytes`和`low_bytes`：
   CONSTANT_Long_info结构中的无符号的high_bytes和low_bytes项，用于共同表示long类型的常量；

###### 1.4.7 CONSTANT_NameAndType_info结构



```undefined
CONSTANT_NameAndType_info结构用于表示字段或方法，但是和之前的3个结构不同，CONSTANT_NameAndType_info结构没有指明该字段或方法所属的类或接口；
```



```kotlin
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index; //name_index 项的值必须是对常量池的有效索引， 常量池在该索引处的项必须是
    CONSTANT_Utf8_info结构，这个结构要么表示特殊的方法名<init>，要么表示一个有效
    的字段或方法的非限定名（ Unqualified Name）。
    u2 descriptor_index;//descriptor_index 项的值必须是对常量池的有效索引， 常量池在该索引
    处的项必须是CONSTANT_Utf8_info结构。
}
```

- `tag`：
   CONSTANT_NameAndType_info结构的tag项的值为CONSTANT_NameAndType(12)。
- `name_index`：
   name_index项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，这个结构要么表示特殊的方法名<init>，要么表示一个有效的字段或方法的非限定名。
- `descriptor_index`：
   descriptor_index项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，这个结构表示一个有效的字段描述符或方法描述符。

###### 1.4.8 CONSTANT_Utf8_info结构



```undefined
CONSTANT_Utf8_info用于表示字符常量的值：
```



```undefined
CONSTANT_Utf8_info {
    u1 tag;
    u2 length;
    u1 bytes[length];
}
```

- `tag`：
   CONSTANT_Utf8_info结构的tag项的值为CONSTANT_Utf8(1)
- `length`：
   length项的值指明了bytes[]数组的长度(注意，不能等同于当前结构所表示的String对象的长度)。CONSTANT_Utf8_info结构中的内容以length属性来确定长度，而不以null作为字符串的终止符。
- `bytes[]`：
   bytes[]是表示字符串值的byte数组，bytes[]中每个成员的byte值都不会是0，也不在0xf0~0xff范围内。

###### 1.4.9 CONSTANT_MethodHandle_info结构



```undefined
CONSTANT_MethodHandle_info结构用于表示方法句柄；
```



```cpp
CONSTANT_MethodHandle_info {
    u1 tag;
    u1 reference_kind;//reference_kind 项的值必须在 1 至 9 之间（包括 1 和 9），它决定了方法句柄的类型。
    方法句柄类型的值表示方法句柄的字节码行为。
    u2 reference_index;//reference_index 项的值必须是对常量池的有效索引。
}
```

- `tag`：
   CONSTANT_MethodHandle_info结构的tag项的值为CONSTANT_MethodHandle(15)。
- `reference_kind`：
   reference_kind项的值必须在范围1~9(包括1和9)之内，它表示方法句柄的类型(king)。方法句柄类型决定句柄的字节码行为（bytecode behavior）。
- `reference_index`：
   reference_index项的值必须是对常量池表的有效索引；

###### 1.4.10 CONSTANT_MethodType_info结构



```undefined
CONSTANT_MethodType_info结构表示方法类型：
```



```undefined
CONSTANT_MethodType_info {
    u1 tag;
    u2 descriptor_index;
}
```

- `tag`：
   CONSTANT_MethodType_info结构的tag项的值为CONSTANT_MethodType(16)。
- `descriptor_index`：
   descriptor_index项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是CONSTANT_Utf8_info结构，这个结构表示一个有效方法描述符。

###### 1.4.11 CONSTANT_InvokeDynamic_info结构



```csharp
CONSTANT_InvokeDynamic_info结构用于表示invokedynamic指令所用到的引导方法(bootstrap method)、引导方法所用到的动态调用名称(dynamic invocation name)、参数和返回类型，并可以给引导方法传入一系列称为静态参数(static argument)的常量。
```



```undefined
CONSTANT_InvokeDynamic_info {
    u1 tag;
    u2 bootstrap_method_attr_index;
    u2 name_and_type_index;
}
```

- `tag`：
   CONSTANT_InvokeDynamic_info结构的tag项的值为CONSTANT_InvokeDynamic(18)。
- `bootstrap_method_attr_index`：
   bootstrap_method_attr_index项的值必须是对当前class文件中引导方法表的bootstrap_methods数组的有效索引。
- `name_and_type_index`：
   name_and_type_index项的值必须是对常量池表的有效索引，常量池表在该索引处的成员必须是CONSTANT_NameAndType_info结构，此结构表示方法名和方法描述符。

#### 1.5 访问标识(access_flag)

 在常量池结束之后，紧接着的两个字节代表访问标志(access_flags)，这个标志用于识别一些类或者接口层次的访问信息，包括：这个Class是类还是接口；是否定义为public类型；是否定义为abstract类型，如果是类的话，是否被声明为final等，具体的标志位以及标志的含义如下：

| 标记名         | 值     | 含义                                                  |
| -------------- | ------ | ----------------------------------------------------- |
| ACC_PUBLIC     | 0x0001 | 可以被包的类外访问。                                  |
| ACC_FINAL      | 0x0010 | 不允许有子类。                                        |
| ACC_SUPER      | 0x0020 | 当用到 invokespecial 指令时，需要特殊处理的父类方法。 |
| ACC_INTERFACE  | 0x0200 | 标识定义的是接口而不是类。                            |
| ACC_ABSTRACT   | 0x0400 | 不能被实例化。                                        |
| ACC_SYNTHETIC  | 0x1000 | 标识并非 Java 源码生成的代码。                        |
| ACC_ANNOTATION | 0x2000 | 标识注解类型                                          |
| ACC_ENUM       | 0x4000 | 标识枚举类型                                          |

#### 1.6 类索引、父类索引与接口索引集合

 类索引（this_class）和父类索引（super_class）都是一个u2类型的数据，而接口索引集合（interfaces）是一组u2类型的数据的集合，Class文件中由这三项数据来确定这个类的继承关系。类索引用于确定这个类的全限定名，父类索引用于确定这个类的父类的全限定名。Java不允许多重继承，所以父类索引只有一个，除了java.lang.Object外，所有Java类的父类索引都不为0。接口索引集合就用来描述这个类实现了哪些接口，所有被实现的接口按类定义中的implements（如果类是一个接口则是extends）后的接口顺序从左到右排列在接口的索引集合中。

#### 1.7 字段表集合(field_info)

 字段表（field_info）用于描述接口或类中声明的变量。字段（field）包括了类级变量和实例级变量，但不包括方法内部声明的变量。一个字段的信息包括：作用域（public、private、protected修饰符）、是实例变量还是类变量（static修饰符）、可变性（final）、并发可见性（volatile修饰符，是否强制从主内存读写）、可否序列化（transient修饰符）、字段数据类型（基本数据类型、对象、数组）、字段名称。这些信息中，各个修饰符都是布尔值，要么有，要么没有。

- 字段结构如下：



```undefined
field_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

- 字段 access_flags 标记列表及其含义:

| 标记名        | 值     | 说明                                |
| ------------- | ------ | ----------------------------------- |
| ACC_PUBLIC    | 0x0001 | public，表示字段可以从任何包访问。  |
| ACC_PRIVATE   | 0x0002 | private，表示字段仅能该类自身调用。 |
| ACC_PROTECTED | 0x0004 | protected，表示字段可以被子类调用。 |
| ACC_STATIC    | 0x0008 | static，表示静态字段。              |
| ACC_FINAL     | 0x0010 | final，表示字段定义后值无法修改     |
| ACC_VOLATILE  | 0x0040 | volatile，表示字段是易变的。        |
| ACC_TRANSIENT | 0x0080 | transient，表示字段不会被序列化     |
| ACC_SYNTHETIC | 0x1000 | 表示字段由编译器自动产生。          |
| ACC_ENUM      | 0x4000 | enum，表示字段为枚举类型            |

 全限定名称：如果TestClass类是定义在com.sunny.common.TestClass包中，那么这个类的全限定名为com/sunny/common/TestClass。
 简单名称：简单名称指没有类型和参数修饰的方法或字段名称，在上面的例子中，TestClass类中的inc()方法和num字段的简单名称分别为“inc”和“num”。
 描述符：描述符的作用是用来描述字段的数据类型、方法的参数列表（包括数量、类型以及顺序）和返回值。根据描述符规则，基本数据类型（byte,char,double,float,int,long,short,boolean）及代表无返回值的void类型都用一个大写字符来表示，而对象则用字符L加对象的全限定名来表示，如下所示：

| 字符         | 类型      | 含义                       |
| ------------ | --------- | -------------------------- |
| B            | byte      | 有符号字节型数             |
| C            | char      | Unicode 字符， UTF-16 编码 |
| D            | double    | 双精度浮点数               |
| F            | float     | 单精度浮点数               |
| I            | int       | 整型数                     |
| J            | long      | 长整数                     |
| S            | short     | 有符号短整数               |
| Z            | boolean   | 布尔值 true/false          |
| L Classname; | reference | 一个名为Classname的实例    |
| [            | reference | 一个一维数组               |

 对于数组类型，每一个维度用一个前置的“[”字符来描述，如定义个int[][]类型的二维数组，记录为："[[I"。
 用描述符来描述方法时，按照先参数列表后返回值的顺序描述。参数裂变按照参数顺序放在“（）”内，如方法void login()描述符为“()V”，方法java.lang.String toString()的描述符为“()Ljava.lang.String”。

#### 1.8 方法表集合(method_info)

 方法表（method_info）的结构与属性表的结构相同，不过多赘述。方法里的Java代码，经过编译器编译成字节码指令后，存放在方法属性表集合中一个名为“Code”的属性里，关于属性表的项目，同样会在后面详细介绍。

 与字段表集合相对应，如果父类方法在子类中没有被覆写，方法表集合中就不会出现来自父类的方法信息。但同样，有可能会出现由编译器自动添加的方法，最典型的便是类构造器“<clinit>”方法和实例构造器“<init>”方法。

  `在Java语言中，要重载一个方法，除了要与原方法具有相同的简单名称外，还要求必须拥有一个与原方法不同的特征签名，特征签名就是一个方法中各个参数在常量池中的字段符号引用的集合，也就是因为返回值不会包含在特征签名之中，因此Java语言里无法仅仅依靠返回值的不同来对一个已有方法进行重载。`

  method_info 结构格式如下：



```undefined
method_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

方法 access_flags 标记列表及其含义:

| 标记名           | 值     | 说明                                   |
| ---------------- | ------ | -------------------------------------- |
| ACC_PUBLIC       | 0x0001 | public，方法可以从包外访问             |
| ACC_PRIVATE      | 0x0002 | private，方法只能本类中访问            |
| ACC_PROTECTED    | 0x0004 | protected，方法在自身和子类可以访问    |
| ACC_STATIC       | 0x0008 | static，静态方法                       |
| ACC_FINAL        | 0x0010 | final，方法不能被重写（覆盖）          |
| ACC_SYNCHRONIZED | 0x0020 | synchronized，方法由管程同步           |
| ACC_BRIDGE       | 0x0040 | bridge，方法由编译器产生               |
| ACC_VARARGS      | 0x0080 | 表示方法带有变长参数                   |
| ACC_NATIVE       | 0x0100 | native，方法引用非 java 语言的本地方法 |
| ACC_ABSTRACT     | 0x0400 | abstract，方法没有具体实现             |
| ACC_STRICT       | 0x0800 | strictfp，方法使用 FP-strict 浮点格式  |
| ACC_SYNTHETIC    | 0x1000 | 方法在源文件中不出现，由编译器产生     |

#### 1.9   属性表（attribute_info）

 属性表（attribute_info）在前面已经出现过多系，在Class文件、字段表、方法表中都可以携带自己的属性表集合，以用于描述某些场景专有的信息。

 属性表集合的限制没有那么严格，不再要求各个属性表具有严格的顺序，并且只要不与已有的属性名重复，任何人实现的编译器都可以向属性表中写入自己定义的属性信息，但Java虚拟机运行时会忽略掉它不认识的属性。关于虚拟机规范中预定义的属性，这里不展开讲了，列举几个常用的。

##### 1.9.1 属性的通用格式



```cpp
attribute_info {
    u2 attribute_name_index;   //属性名索引
    u4 attribute_length;       //属性长度
    u1 info[attribute_length]; //属性的具体内容
}
```

##### 1.9.2 ConstantValue 属性

 ConstantValue 属性表示一个常量字段的值。位于 field_info结构的属性表中。



```cpp
ConstantValue_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 constantvalue_index;//字段值在常量池中的索引，常量池在该索引处的项给出该属性表示的常量值。（例如，值是long型的，在常量池中便是CONSTANT_Long）
}
```

##### 1.9.3 Deprecated 属性

 Deprecated 属性是在 JDK 1.1 为了支持注释中的关键词@deprecated 而引入的。



```undefined
Deprecated_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
}
```

##### 1.9.4Code 属性



```cpp
Code_attribute {
    u2 attribute_name_index; //常量池中的uft8类型的索引，值固定为”Code“
    u4 attribute_length; //属性值长度，为整个属性表长度-6
    u2 max_stack;   //操作数栈的最大深度值，jvm运行时根据该值分配栈帧
    u2 max_locals;  //局部变量表最大存储空间，单位是slot
    u4 code_length; // 字节码指令的个数
    u1 code[code_length]; // 具体的字节码指令
    u2 exception_table_length; //异常的个数
    {   u2 start_pc;
        u2 end_pc;
        u2 handler_pc; //当字节码在[start_pc, end_pc)区间出现catch_type或子类，则转到handler_pc行继续处理。
        u2 catch_type; //当catch_type=0，则任意异常都需转到handler_pc处理
    } exception_table[exception_table_length]; //具体的异常内容
    u2 attributes_count;     //属性的个数
    attribute_info attributes[attributes_count]; //具体的属性内容
}
```

- 其中slot为局部变量中的最小单位。boolean、 byte、 char、 short、 float、 reference和 returnAddress 等小于等于32位的用一个slot表示，double,long这些大于32位的用2个slot表示。

##### 1.9.5 InnerClasses 属性

 为了方便说明特别定义一个表示类或接口的 Class 格式为 C。如果 C 的常量池中包含某个CONSTANT_Class_info 成员，且这个成员所表示的类或接口不属于任何一个包，那么 C 的ClassFile 结构的属性表中就必须含有对应的 InnerClasses 属性。InnerClasses 属性是在 JDK 1.1 中为了支持内部类和内部接口而引入的,位于 ClassFile结构的属性表。



```undefined
InnerClasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {   u2 inner_class_info_index;
        u2 outer_class_info_index;
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes[number_of_classes];
}
```

##### 1.9.6 LineNumberTable 属性

 LineNumberTable 属性是可选变长属性，位于 Code结构的属性表。它被调试器用于确定源文件中行号表示的内容在 Java 虚拟机的 code[]数组中对应的部分。在 Code 属性的属性表中,LineNumberTable 属性可以按照任意顺序出现，此外，多个 LineNumberTable属性可以共同表示一个行号在源文件中表示的内容，即 LineNumberTable 属性不需要与源文件的行一一对应。



```cpp
LineNumberTable_attribute {
    u2 attribute_name_index;//属性名称在常量池的索引，指向一个 CONSTANT_Utf8_info结构。
    u4 attribute_length;//属性长度
    u2 line_number_table_length;//线性表长度
    {   u2 start_pc;
        u2 line_number;
      } line_number_table[line_number_table_length];
}
```

##### 1.9.7 LocalVariableTable 属性

 LocalVariableTable 是可选变长属性，位于 Code属性的属性表中。它被调试器用于确定方法在执行过程中局部变量的信息。在 Code 属性的属性表中，LocalVariableTable 属性可以按照任意顺序出现。 Code 属性中的每个局部变量最多只能有一
 个 LocalVariableTable 属性。



```undefined
LocalVariableTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_table_length
    { u2 start_pc;
      u2 length;
      u2 name_index;
      u2 descriptor_index;
      u2 index;
    } local_variable_table[local_variable_table_length];
}
```

##### 1.9.8 Signature 属性

 Signature 属性是可选的定长属性，位于 ClassFile， field_info
 或 method_info结构的属性表中。在 Java 语言中，任何类、 接口、 初始化方法或成员的泛型签名如果包含了类型变量（ Type Variables） 或参数化类型（ Parameterized Types），则 Signature 属性会为它记录泛型签名信息。



```cpp
Signature_attribute {
    u2 attribute_name_index;//属性名称在常量池中的索引，指向一个 CONSTANT_Utf8_info结构。
    u4 attribute_length;
    u2 signature_index;
}
```

- slot是虚拟机未局部变量分配内存使用的最小单位。对于byte/char/float/int/short/boolean/returnAddress等长度不超过32位的局部变量，每个占用1个Slot；对于long和double这两种64位的数据类型则需要2个Slot来存放。
- 实例方法中有隐藏参数this, 显式异常处理器的参数，方法体定义的局部变量都使用局部变量表来存放。
- max_locals，不是所有局部变量所占Slot之和，因为Slot可以重用，javac编译器会根据变量的作用域来分配Slot给各个变量使用，从而计算出max_locals大小。
- 虚拟机规范限制严格方法不允许超过65535个字节码，否则拒绝编译。







# class文件结构与格式 ------- 思维导图

## 前言

class文件作为操作系统无关的格式文件，是JVM直接识别的字节码文件。它可由java、scala、groovy等语言编译而来，校验后可在JVM中执行。下面我们一起看看class文件的结构与格式规范。

### 1. class文件基本概念

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200426191325778.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTAwODYxMjI=,size_16,color_FFFFFF,t_70)

### 2. class文件的结构

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200426191425971.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTAwODYxMjI=,size_16,color_FFFFFF,t_70)

##### PS：符号引用的概念

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200426191540726.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTAwODYxMjI=,size_16,color_FFFFFF,t_70)

### 3. 字节码指令

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200426192305482.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTAwODYxMjI=,size_16,color_FFFFFF,t_70#pic_center)

# JVM优化之 -Xss -Xms -Xmx -Xmn 参数设置

### XmnXmsXmxXss有什么区别

Xmn、Xms、Xmx、Xss都是JVM对内存的配置参数，我们可以根据不同需要区修改这些参数，以达到运行程序的最好效果。

-Xms 堆内存的最小大小，默认为物理内存的1/64

-Xmx 堆内存的最大大小，默认为物理内存的1/4

-Xmn 堆内新生代的大小。通过这个值也可以得到老生代的大小：-Xmx减去-Xmn

-Xss 设置每个线程可使用的内存大小，即栈的大小。在相同物理内存下，减小这个值能生成更多的线程，当然操作系统对一个进程内的线程数还是有限制的，不能无限生成。线程栈的大小是个双刃剑，如果设置过小，可能会出现**栈溢出**，特别是在该线程内有递归、大的循环时出现溢出的可能性更大，如果该值设置过大，就有影响到创建栈的数量，如果是多线程的应用，就会出现内存溢出的错误。

------

除了这些配置，JVM还有非常多的配置，常用的如下：

1. 堆设置
   - **-Xms**:初始堆大小
   - **-Xmx**:最大堆大小
   - **-Xmn**:新生代大小
   - **-XX:NewRatio**:设置新生代和老年代的比值。如：为3，表示年轻代与老年代比值为1：3
   - **-XX:SurvivorRatio**:新生代中Eden区与两个Survivor区的比值。注意Survivor区有两个。如：为3，表示Eden：Survivor=3：2，一个Survivor区占整个新生代的1/5 
   - **-XX:MaxTenuringThreshold**:设置转入老年代的存活次数。如果是0，则直接跳过新生代进入老年代
   - **-XX:PermSize**、**-XX:MaxPermSize**:分别设置永久代最小大小与最大大小（Java8以前）
   - **-XX:MetaspaceSize**、**-XX:MaxMetaspaceSize**:分别设置元空间最小大小与最大大小（Java8以后）
2. 收集器设置
   - **-XX:+UseSerialGC**:设置串行收集器
   - **-XX:+UseParallelGC**:设置并行收集器
   - **-XX:+UseParalledlOldGC**:设置并行老年代收集器
   - **-XX:+UseConcMarkSweepGC**:设置并发收集器
3. 垃圾回收统计信息
   - **-XX:+PrintGC**
   - **-XX:+PrintGCDetails**
   - **-XX:+PrintGCTimeStamps**
   - **-Xloggc:filename**
4. 并行收集器设置
   - **-XX:ParallelGCThreads=n**:设置并行收集器收集时使用的CPU数。并行收集线程数。
   - **-XX:MaxGCPauseMillis=n**:设置并行收集最大暂停时间
   - **-XX:GCTimeRatio=n**:设置垃圾回收时间占程序运行时间的百分比。公式为1/(1+n)
5. 并发收集器设置
   - **-XX:+CMSIncrementalMode**:设置为增量模式。适用于单CPU情况。
   - **-XX:ParallelGCThreads=n**:设置并发收集器新生代收集方式为并行收集时，使用的CPU数。并行收集线程数。