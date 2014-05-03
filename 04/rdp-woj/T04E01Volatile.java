package T04;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class T04E01Volatile {
	static volatile long globalCountMax = Integer.MAX_VALUE;
	static volatile long globalCount = 0;
	static volatile long globalReserve = 0;
	static ArrayList<ThreadContadora> listaThreads = new ArrayList<ThreadContadora>();  
	static ReentrantLock lock = new ReentrantLock();
	static boolean stop = false;
	static int tempoEsperaLeitura = 100;
	static int numeroThreads = 3;
	static long MAX_COUNTERMAX = 100;
	static ThreadLeitora leitora;

	public T04E01Volatile() {
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
		new T04E01Volatile();
	}


	class ThreadLeitora extends Thread{

		@Override
		public void run() {
			synchronized (this) {
				while (!stop) {
					System.out.println(read_count());
					try {
						wait(tempoEsperaLeitura);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(read_count());
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
		volatile long threadCount = 0;
		volatile long threadCountMax = 0;
		volatile long delta = 1;


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
			globalize_count();
			try{
				lock.lock();
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
			globalize_count();				
			if(globalCountMax - globalCount < delta){
				return 0;
			}
			globalCount += delta;
			balance_count();		
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
			globalize_count();
			if(globalCount < delta){
				return 0;
			}
			globalCount -= delta;
			balance_count();
			return 1;
		}

		void globalize_count(){
			globalCount += threadCount;
			threadCount = 0;
			globalReserve -= threadCountMax;
			threadCountMax = 0;
		}

		void balance_count(){
			threadCountMax = globalCountMax - globalCount - globalReserve;
			threadCountMax /= listaThreads.size();
						if(threadCountMax > MAX_COUNTERMAX){
							threadCountMax = MAX_COUNTERMAX;
						}
			globalReserve += threadCountMax;
			threadCount = threadCountMax / 2;
			if (threadCount > globalCount) {
				threadCount = globalCount;
			}
			globalCount -= threadCount;
		}
	}



}