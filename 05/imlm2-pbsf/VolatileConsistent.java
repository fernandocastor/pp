//
//
//
//public class VolatileConsistent {
//
//	public volatile int f1;
//
//	public VolatileConsistent() {
//		f1 = 0;
//	}
//
//	volatile static boolean stop = false;
//	public static void main(String[] args) {
//		final VolatileConsistent lambdaTest1 = new VolatileConsistent();
//		final VolatileConsistent lambdaTest2 = lambdaTest1;
//		Thread t1 = new Thread(() -> {
//			while(!stop) {
//				VolatileConsistent test1 = lambdaTest1;
//				int i1 = test1.f1;
//				VolatileConsistent test2 = lambdaTest2;
//				int i2 = test2.f1;
//				int i3 = test1.f1;
//				if(i3 != i2 && i3 == i1) {
//					System.out.println("i1=" + i1);
//					System.out.println("i2=" + i2);
//					System.out.println("i3=" + i3);
//					stop = true;
//				}
//			}
//		});
//		Thread t2 = new Thread(() -> {
//			while(!stop) {
//				VolatileConsistent t3 = lambdaTest1;
//				t3.f1++;
//			}
//		});
//		t1.start();
//		t2.start();
//	}
//}
