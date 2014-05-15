//package java8_sandbox;
//
//
//public class SynchronizedVolatileConsistent {
//
//	public volatile int f1;
//	public volatile int f2;
//
//	public SynchronizedVolatileConsistent() {
//		f1 = 0;
//		f2 = 0;
//	}
//
//	volatile static boolean stop = false;
//	public static void main(String[] args) {
//		final SynchronizedVolatileConsistent lambdaTest1 = new SynchronizedVolatileConsistent();
//		final SynchronizedVolatileConsistent lambdaTest2 = lambdaTest1;
//		Thread t1 = new Thread(() -> {
//			while(!stop) {
//				synchronized (lambdaTest1) {
//					SynchronizedVolatileConsistent test1 = lambdaTest1;
//					int i1 = test1.f1;
//					SynchronizedVolatileConsistent test2 = lambdaTest2;
//					int i2 = test2.f2;
//					if(i2 > i1) {
//						System.out.println("i1=" + i1);
//						System.out.println("i2=" + i2);
//						stop = true;
//					}
//				}
//			}
//		});
//		Thread t2 = new Thread(() -> {
//			while(!stop) {
//				synchronized(lambdaTest1) {
//					SynchronizedVolatileConsistent t3 = lambdaTest1;
//					t3.f1++;
//					t3.f2++;
//				}
//			}
//		});
//		t1.start();
//		t2.start();
//	}
//}
