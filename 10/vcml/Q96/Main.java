package Q96;

public class Main {

	public static void main(String[] args) {
		
		BathroomSynchronized b = new BathroomSynchronized();
		// BathroomLockCondition b = new BathroomLockCondition();
		
		Thread t1 = new Thread(){
			@Override
			public void run() {
				b.enterMale();				
				b.leaveMale();
			}
		};
		
		Thread t2 = new Thread(){
			@Override
			public void run() {
				b.enterFemale();				
				b.leaveFemale();
			}
		};
		
		Thread t3 = new Thread(){
			@Override
			public void run() {
				b.enterMale();				
				b.leaveMale();
			}
		};
		
		Thread t4 = new Thread(){
			@Override
			public void run() {
				b.enterFemale();				
				b.leaveFemale();
			}
		};
		
		Thread t5 = new Thread(){
			@Override
			public void run() {
				b.enterMale();				
				b.leaveMale();
			}
		};
		
		Thread t6 = new Thread(){
			@Override
			public void run() {
				b.enterFemale();				
				b.leaveFemale();
			}
		};
		
		t1.start();
		t3.start();
		t2.start();
		t4.start();
		t5.start();
		t6.start();

	}

}
