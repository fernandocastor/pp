
import java.util.concurrent.atomic.AtomicReference;


public class AtomicReferenceNotConsistent {

	public AtomicReference<Integer> f1;
	public AtomicReference<Integer> f2;

	public AtomicReferenceNotConsistent() {
		f1 = new AtomicReference<Integer>(0);
		f2 = new AtomicReference<Integer>(0);
	}

	volatile static boolean stop = false;
	public static void main(String[] args) {
		final AtomicReferenceNotConsistent lambdaTest1 = new AtomicReferenceNotConsistent();
		final AtomicReferenceNotConsistent lambdaTest2 = lambdaTest1;
		Thread t1 = new Thread(new Runnable() {
            
            @Override
            public void run() {
                while(!stop) {
                    AtomicReferenceNotConsistent test1 = lambdaTest1;
                    int i1 = test1.f1.get();
                    AtomicReferenceNotConsistent test2 = lambdaTest2;
                    int i2 = test2.f2.get();
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
                    AtomicReferenceNotConsistent t3 = lambdaTest1;
                    t3.f1.set(t3.f1.get() + 1);
                    t3.f2.set(t3.f2.get() + 1);
                }
            }
        });
		t1.start();
		t2.start();
	}
}
