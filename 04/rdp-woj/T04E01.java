package T04;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class T04E01 {
	static long globalCountMax = Integer.MAX_VALUE;
	static long globalCount = 0;
	static long globalReserve = 0;
	static ArrayList<ThreadContadora> listaThreads = new ArrayList<ThreadContadora>();  
	static ReentrantLock lock = new ReentrantLock();
	static boolean stop = false;
	static int tempoEsperaLeitura = 1000;
	static int numeroThreads = 8;
	static long MAX_COUNTERMAX = 100;
	static ThreadLeitora leitora;

	public T04E01() {
		System.out.println("valor máximo esperado: " + globalCountMax);
		for (int i = 0; i < numeroThreads; i++) {
			new ThreadContadora();
		}
		leitora = new ThreadLeitora();
		try {
			start_threads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void start_threads() throws InterruptedException{
		for (ThreadContadora i : listaThreads) {
			i.start();
		}
		leitora.start();
		leitora.join();
	}

	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		new T04E01();
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	class ThreadLeitora extends Thread{
		@Override
		public void run() {
			synchronized (this) {
				while (!stop) {
//					System.out.println(read_count());
					try {
						wait(tempoEsperaLeitura);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//				System.out.println(read_count());
			}
		}
		public long read_count(){
			long sum;
			try{
				lock.lock();
				sum = globalCount;
				for (ThreadContadora  i : listaThreads) {
					sum += i.threadCount;
				}
			}finally{
				lock.unlock();
			}
			return sum;
		}
	}
	class ThreadContadora extends Thread{
		int threadCount = 0;
		int threadCountMax = 0;
		int delta = 1;

		@Override
		public void run() {
			while(!stop){
				add_count(delta);
			}
		}
		public ThreadContadora() {

			try{
				lock.lock();
				listaThreads.add(this);
			}finally{
				lock.unlock();
			}
		}
		public void delete(){
			try{
				lock.lock();
				globalize_count();
				listaThreads.remove(this);
			}finally{
				lock.unlock();
			}

		}
		public int add_count(long delta){
			if(globalCount >= globalCountMax){
				stop = true;
				delete();
				return -1;
			}
			if(threadCountMax - threadCount >= delta){
				threadCount += delta;
				return 1;
			}
			lock.lock();
			globalize_count();				
			if(globalCountMax - globalCount < delta){
				lock.unlock();
				return 0;
			}
			globalCount += delta;
			balance_count();		
			lock.unlock();
			return 1;
		}
		public int sub_count(long delta){
			if(globalCount >= globalCountMax){
				stop = true;
				delete();
				return -1;
			}
			if(threadCount >= delta){
				threadCount -= delta;
				return 1;
			}
			lock.lock();
			globalize_count();
			if(globalCount < delta){
				lock.unlock();
				return 0;
			}
			globalCount -= delta;
			balance_count();
			lock.unlock();
			return 1;
		}
		void globalize_count(){
			globalCount += threadCount;
			threadCount = 0;
			globalReserve -= threadCountMax;
			threadCountMax = 0;
		}
		void balance_count(){
			threadCountMax = (int) (globalCountMax - globalCount - globalReserve);
			threadCountMax /= listaThreads.size();
			if(threadCountMax > MAX_COUNTERMAX){
				threadCountMax = (int) MAX_COUNTERMAX;
			}
			globalReserve += threadCountMax;
			threadCount = threadCountMax / 2;
			if (threadCount > globalCount) {
				threadCount = (int) globalCount;
			}
			globalCount -= threadCount;
		}
	}




}