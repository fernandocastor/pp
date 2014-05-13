
public class NonVolatileInconsistent {

	public volatile int f1;

	public NonVolatileInconsistent() {
		f1 = 0;
	}

	volatile static boolean stop = false;
	public static void main(String[] args) {
		final NonVolatileInconsistent lambdaTest1 = new NonVolatileInconsistent();
		final NonVolatileInconsistent lambdaTest2 = lambdaTest1;
		Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                while(!stop) {
//                    NonVolatileInconsistent test1 = lambdaTest1;
                    int i1 = lambdaTest1.f1;
//                    NonVolatileInconsistent test2 = lambdaTest2;
                    int i2 = lambdaTest2.f1;
                    int i3 = lambdaTest1.f1;
                    if(i3 != i2 && i3 == i1) {
                        System.out.println("i1=" + i1);
                        System.out.println("i2=" + i2);
                        System.out.println("i3=" + i3);
                        stop = true;
                    }
                }
            }
		    
		    
		});
		Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                while(!stop) {
                    NonVolatileInconsistent t3 = lambdaTest1;
                    t3.f1++;
                }
            }
			
		});
		t1.start();
		t2.start();
	}
}
