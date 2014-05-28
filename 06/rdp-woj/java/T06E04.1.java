package T06;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

public class EX041 {

	DequeHash dequeHash;
	int numberChains = 10000  ;
	Random generator = new Random();
	boolean parouSoma = false;
	boolean parouRem = false;
	Thread t1, t2, t3, t4, t5, t6, t7, t8 ;
//	int waitPopLeft = 3;
//	int waitPopRight = 3;
//	int waitPushLeft = 3;
//	int waitPushRight = 3;
	int waitReader = 5000;
	int insRight = 0;
	int insLeft = 0;
	int remRight = 0;
	int remLeft = 0;

	public EX041() {
		dequeHash = new DequeHash();
		long init = System.currentTimeMillis();
		InitStartThreads();
		long fim = System.currentTimeMillis();
		System.out.println("tempo:" + (fim-init));
		System.out.println("Terminado execução");
	}
	private void InitStartThreads() {


		//		t5 = new Thread(new Runnable() {
		//			
		//			@Override
		//			public void run() {
		//				while(!parou){
		//					deque.lLock.lock();
		//					deque.rLock.lock();
		//					System.out.println("ESTADO ATUAL DO DEQUE");
		//					for (int j = deque.lList.size()-1; j >= 0; j--) {
		//						System.out.print(deque.lList.get(j) + ",");
		//					}
		//					System.out.print("~,");
		//					for (int i = 0; i < deque.rList.size(); i++) {
		//						System.out.print(deque.rList.get(i) + ",");
		//					}
		//					System.out.println();
		//					try {
		//						synchronized (this) {
		//							deque.rLock.unlock();
		//							deque.lLock.unlock();
		//							wait(generator.nextInt(10000));
		//						}
		//					} catch (InterruptedException e) {
		//						e.printStackTrace();
		//					}
		//				}
		//				
		//			}
		//		});

		t1 = new ThreadLeftPop();
		t2 = new ThreadLeftPush();
		t3 = new ThreadRightPop();
		t4 = new ThreadRightPush();
		t5 = new ThreadLeftPop();
		t6 = new ThreadLeftPush();
		t7 = new ThreadRightPop();
		t8 = new ThreadRightPush();
		

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
//		//		t5.start();
		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			t7.join();
			t8.join();
			//			t5.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	class ThreadLeftPop extends Thread{
		public void run(){
			while(!parouRem){
				getDeque().popLeft();
//				try {
////					synchronized (this) {
////						wait(waitPopLeft);
////					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				remRight++;
				if(remRight + remLeft >= 50000){
					parouRem = true;
				}
			}
		}
	}

	class ThreadRightPop extends Thread{
		public void run(){
			while(!parouRem){
				getDeque().popRight();
//				try {
//					synchronized (this) {
//						wait(waitPopRight);
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				remLeft++;
				if(remRight + remLeft >= 50000){
					parouRem = true;
				}
			}
		}
	}

	class ThreadRightPush extends Thread{
		public void run() {
			while(!parouSoma){
				getDeque().pushRight((generator.nextInt(100)));
//				try {
//					synchronized (this) {
//						wait(waitPushRight);
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				insRight++;
				if(insRight + insLeft >= 50000){
					parouSoma = true;
				}
			}
		}
	}

	class ThreadLeftPush extends Thread{
		public void run() {
			while(!parouSoma){
				getDeque().pushLeft((generator.nextInt(100)));
//				try {
//					synchronized (this) {
//						wait(waitPushLeft);
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				insLeft++;
				if(insRight + insLeft >= 50000){
					parouSoma = true;
				}
			}
		}
	}
	public static void main(String[] args) {
		new EX041();
	}

	public  DequeHash getDeque(){
		return dequeHash;
	}

	class DequeChain{
		BackoffLock lock = new BackoffLock();
		LinkedList<Integer> list = new LinkedList<>();
		int id;

		public DequeChain(int id) {
			this.id = id;
		}

		public int popLeft(){
			int retorno;
			try {
				lock.lock();		
				retorno = list.removeFirst();
//				System.out.println("tirando de: " + id + " pela esquerda, elemento "+ retorno );
			} 
			catch (NoSuchElementException e) {
				return 0;
			} catch (DeadlockException e) {
				retorno = 0;
				e.printStackTrace();
			} finally{			
				lock.unlock();
			}
			return retorno;
		}
		public int popRight(){
			int retorno;
			try {
				lock.lock();		
				retorno = list.removeLast();
//				System.out.println("tirando de: " + id + " pela direita, elemento "+ retorno );
			} 
			catch (NoSuchElementException e) {
				return 0;
			} catch (DeadlockException e) {
				retorno = 0;
				e.printStackTrace();
			} finally{			
				lock.unlock();
			}
			return retorno;
		}
		public void pushLeft(int arg){
			try {
				lock.lock();
			} catch (DeadlockException e) {
				e.printStackTrace();
			}	
			list.addFirst(arg);
//			System.out.println("colocando: " + id + " pela esquerda, elemento "+ arg );
			lock.unlock();
		}
		public void pushRight(int arg){
			try {
				lock.lock();
			} catch (DeadlockException e) {
				e.printStackTrace();
			}		
			list.addLast(arg);
//			System.out.println("colocando: " + id + " pela direita, elemento "+ arg );
			lock.unlock();
		}


	}

	class DequeHash {

	
		int lidx = 0, ridx = 1;
		ArrayList<DequeChain> chains = new ArrayList<DequeChain>();

		public DequeHash() {
			for (int i = 0; i < numberChains; i++) {
				chains.add(new DequeChain(i));
			}
		}

		BackoffLock lLock = new BackoffLock();
		BackoffLock rLock = new BackoffLock();

		public int moveRight(int args){
			int retorno = (args+1)%numberChains;
			return retorno;
		}

		public int moveLeft(int args){
			int retorno = 0;
			if(args == 0){
				retorno = numberChains-1;
			}
			else{
				retorno = (args-1)%numberChains;
			}
			return retorno;
		}

		public void pushLeft(int arg){
			try {
				lLock.lock();
			} catch (DeadlockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chains.get(lidx).pushLeft(arg);
			lidx = moveLeft(lidx);
			lLock.unlock();
		}
		public void pushRight(int arg){
			try {
				rLock.lock();
			} catch (DeadlockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chains.get(ridx).pushRight(arg);
			ridx = moveRight(ridx);
			rLock.unlock();
		}

		public int popLeft(){

			int retorno;
			try {
				lLock.lock();
			} catch (DeadlockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			int i = moveRight(lidx);
			retorno = chains.get(i).popLeft();
			if(retorno != 0){
				lidx = moveRight(lidx);
			}
			lLock.unlock();
			return retorno;
		}
		public int popRight(){

			int retorno;
			try {
				rLock.lock();
			} catch (DeadlockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			int i = moveLeft(ridx);
			retorno = chains.get(i).popRight();
			if(retorno != 0){
				ridx = moveLeft(ridx);
			}
			rLock.unlock();
			return retorno;
		}


	}
}
