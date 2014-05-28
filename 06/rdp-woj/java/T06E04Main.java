package T06;

public class EX04 {

	BackoffLock lock1 = new BackoffLock();
	BackoffLock lock2 = new BackoffLock();
	BackoffLock lock3 = new BackoffLock();
	Thread t1,t2,t3;
	
	
	public EX04() {
		
	t1 = new Thread(new Runnable() {
		
		@Override
		public void run() {
			
			try{
			lock1.lock();
			System.out.println("t1 adquiriu lock1");
			synchronized (Thread.currentThread()) {
				Thread.currentThread().wait(100);
			}
			
			lock2.lock();
			System.out.println("t1 adquiriu lock2");
						
			lock2.unlock();
			lock1.unlock();
			
			}catch( DeadlockException e){
				System.out.println("deadlock papai");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	});
	
	t2 = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try{
				lock2.lock();
				System.out.println("t2 adquiriu lock2");
				
				synchronized (Thread.currentThread()) {
					Thread.currentThread().wait(100);
				}
				
				lock1.lock();
				System.out.println("t2 adquiriu lock1");
				
				lock1.unlock();
				lock2.unlock();
				
				}catch( DeadlockException e){
					System.out.println("deadlock papai");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
	
	t1.start();
	t2.start();
	
	}
	
	public static void main(String[] args) {
		new EX04();
		
		//DIFERENÇA DE DESEMPENHO
		// SEM O DETECTOR DE DEADLOCK : 106ms
		// COM O DETECTOR DE DEADLOCK : 3341ms
		
	}
	
}
