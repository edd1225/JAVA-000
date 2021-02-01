package cn.qj.week5.dynamicproxy2;

public class Cat implements Animate {
 
	@Override
	public void printName() {
		System.out.println("This is cat!");
	}
 
	@Override
	public String getName() {
		return "cat";
	}

}