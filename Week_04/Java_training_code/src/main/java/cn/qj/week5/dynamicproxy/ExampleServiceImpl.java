package cn.qj.week5.dynamicproxy;

/**
 * @author lw
 */
public class ExampleServiceImpl implements ExampleService {
    @Override
    public void testCall() {
        System.out.println("Print example test info");
    }
}