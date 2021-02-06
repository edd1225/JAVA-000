package io.kimmking.rpcfx.api;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AopAnnotation {

    String name() default "";
}
