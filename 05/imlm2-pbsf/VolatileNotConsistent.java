


public class VolatileNotConsistent {

	public volatile int f1;
	public volatile int f2;

	public VolatileNotConsistent() {
		f1 = 0;
		f2 = 0;
	}

	volatile static boolean stop = false;
	public static void main(String[] args) {
		final VolatileNotConsistent lambdaTest1 = new VolatileNotConsistent();
		Thread t1 = new Thread(new Runnable() {
		    

            @Override
            public void run() {
                while(!stop) {
                    int i1 = lambdaTest1.f1;
                    int i2 = lambdaTest1.f2;
                    if(i2 > i1) {
                        System.out.println("i1=" + i1);
                        System.out.println("i2=" + i2);
                        stop = true;
                    }
                }
            }
		});
			
		Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                while(!stop) {
                    VolatileNotConsistent t3 = lambdaTest1;
                    t3.f1++;
                    if(t3.f1 > t3.f2) {
                        System.out.println(t3.f1 + " " + t3.f2);
                        t3.f2++;
                    }
                    
                }
            }
		    
		    
		});
			
		t1.start();
		t2.start();
	}
}
