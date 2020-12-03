package cn.qj.week5.auto;

import org.springframework.stereotype.Component;
/**
 * 自动注解方式，Bean装配
 *
 */
@Component
public class AutoWiringExample {
    public AutoWiringExample() {
        System.out.println("Construct Example");
    }

    public void example() {
        System.out.println("Auto wiring example");
    }
}