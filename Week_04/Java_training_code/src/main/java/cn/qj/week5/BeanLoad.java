package cn.qj.week5;
ackage com.example.code.class9;

import com.example.code.class9.auto.AutoWiringExample;
import org.springframework.beans.factory.annotation.Autowired;

public class BeanLoad {

    @Autowired
    public AutoWiringExample instanceExample;

    public static void main(String[] args) {
        BeanLoad beanLoad = new BeanLoad();
        beanLoad.instanceExample.info();
    }
}