package cn.qj.week5.dynamicproxy2;

public class Dog implements Animate {
 
	@Override
	public void printName() {
		System.out.println("This is dog!");
	}
 
	@Override
	public String getName() {
		return "dog";
	}
 
}
