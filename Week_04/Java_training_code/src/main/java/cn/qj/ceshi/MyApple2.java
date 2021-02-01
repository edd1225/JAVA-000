package cn.qj.ceshi;

public class MyApple2 {
    private static int apple = 100;

    public static void main(String[] args) {
        Thread monkeyOne = new Thread(() -> {
           try {
               while(apple >= 1) {
                   synchronized (MyApple2.class){
                       if(apple >= 1){
                           apple -= 1;
                           System.out.println("A猴子拿了1个，还剩" + apple + "个");
                       }
                       MyApple2.class.notify();
                       if (apple < 1) break;
                       MyApple2.class.wait();
                   }
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        });

        Thread monkeyTwo = new Thread(() -> {
            try {
                while(apple >= 2) {
                    synchronized (MyApple2.class){
                        if(apple >= 2){
                            apple -= 2;
                            System.out.println("B猴子拿了2个，还剩" + apple + "个");
                        }
                        MyApple2.class.notify();
                        if(apple < 2) break;
                        MyApple2.class.wait();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        Thread monkeythree  = new Thread(() -> {
            try {
                while(apple >= 3) {
                    synchronized (MyApple2.class){
                        if(apple >=3){
                            apple -= 3;
                            System.out.println("C猴子拿了3个，还剩" + apple + "个");
                        }
                        MyApple2.class.notify();
                        if (apple < 3) break;
                        MyApple2.class.wait();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        monkeyOne.start();
        monkeyTwo.start();
        monkeythree.start();
    }
}
