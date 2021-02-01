package cn.qj.ceshi;

/**
 * 一次走1级或2级或3级的总走法
 */
public class Main3 {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		fun(10);
		System.out.println("n=10时运行时间："+(System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		fun(30);
		System.out.println("n=30时运行时间："+(System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		fun(40);
		System.out.println("n=40时运行时间："+(System.currentTimeMillis() - start));
	}
	
	public static int fun(int stair){
		if(stair == 1){
			return 1;
		}else if(stair == 2){
			return 2;
		}else if(stair == 3){
			return 4;//3级台阶总共有4种走法
		}else{
			return fun(stair - 1)+fun(stair - 2)+fun(stair - 3);
		}
	}
}
