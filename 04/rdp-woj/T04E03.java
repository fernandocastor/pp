package T04;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class T04E03 {
	static volatile short THEFT_IDLE = 0;
	static volatile short THEFT_REQ = 1;
	static volatile short THEFT_ACK = 2;
	static volatile short THEFT_READY = 3;

	static long globalCountMax = Integer.MAX_VALUE;
	static long globalCount = 0;
	static long globalReserve = 0;
	static ArrayList<ThreadContadora> listaThreads = new ArrayList<ThreadContadora>();  
	static ReentrantLock lock = new ReentrantLock();
	static boolean stop = false;
	static int tempoEsperaLeitura = 100;
	static int numeroThreads = 3;
	static long MAX_COUNTERMAX = 100;
	static ThreadLeitora leitora;

	public T04E03() {
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
		new T04E03();
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
					sum += i.counter;
				}
			}finally{
				lock.unlock();
			}
			return sum;
		}
	}
	class ThreadContadora extends Thread{
		volatile short theft = THEFT_IDLE;
		volatile boolean counting = false;
		volatile boolean fastpath = false;

		long counter = 0;
		long counterMax = 0;
		long delta = 1;
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
			fastpath = false;
			counting = true;
			
			if(counterMax - counter >= delta && theft <= THEFT_REQ){
				counter += delta;
				fastpath = true;
			}

			counting = false;
			if(theft == THEFT_ACK){
				theft = THEFT_READY;
			}
			if(fastpath){
				return 1;
			}
			lock.lock();
			globalize_count();				
			if(globalCountMax - globalCount - globalReserve < delta){
				flush_local_count();
				if(globalCountMax - globalCount - globalReserve < delta){
					lock.unlock();
					return 0;
				}
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

			fastpath = false;
			counting = true;

			if(counter >= delta && theft <= THEFT_REQ){
				counter -= delta;
				fastpath = true;
			}
			counting = false;

			if(theft == THEFT_ACK){
				theft = THEFT_READY;
			}

			if(fastpath){
				return 1;
			}

			lock.lock();
			globalize_count();				
			if(globalCount < delta){
				flush_local_count();
				if(globalCount < delta){
					lock.unlock();
					return 0;
				}
			}
			globalCount -= delta;
			balance_count();		
			lock.unlock();
			return 1;
		}

		void globalize_count(){
			globalCount += counter;
			counter = 0;
			globalReserve -= counterMax;
			counterMax = 0;
		}

		void flush_local_count_sig(int unused){
			if(theft != THEFT_REQ){
				return;
			}
			theft = THEFT_ACK;
			if(!counting){
				theft = THEFT_READY;
			}
		}

		void flush_local_count(){

			for (ThreadContadora i : listaThreads) {
				if(i.counterMax == 0){
					i.theft = THEFT_READY;
					continue;
				}
				i.theft = THEFT_REQ;
				i.flush_local_count_sig(1);
			}
			for (ThreadContadora i : listaThreads) {
				while(i.theft != THEFT_READY){
					synchronized (this) {
						try {
							wait(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(i.theft == THEFT_REQ){
						i.flush_local_count_sig(1);
					}
				}
				globalCount += i.counter;
				i.counter = 0;
				globalReserve -= i.counterMax;
				i.counterMax = 0;
				i.theft = THEFT_IDLE;
			}
		}

		void balance_count(){
			counterMax = (globalCountMax - globalCount - globalReserve);
			counterMax /= listaThreads.size();
			if(counterMax > MAX_COUNTERMAX){
				counterMax = MAX_COUNTERMAX;
			}
			globalReserve += counterMax;
			counter = counterMax / 2;
			if (counter > globalCount) {
				counter = globalCount;
			}
			globalCount -= counter;
		}
	}



}