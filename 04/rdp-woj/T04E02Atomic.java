package T04;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class T04E02Atomic {
	static AtomicLong globalCountMax = new AtomicLong(Integer.MAX_VALUE);
	static AtomicLong globalCount = new AtomicLong();
	static ArrayList<ThreadContadora> listaThreads = new ArrayList<ThreadContadora>();  
	static boolean stop = false;
	static int tempoEsperaLeitura = 1000;
	static int numeroThreads = 8;
	static long MAX_COUNTERMAX = 100;
	static ThreadLeitora leitora;

	public T04E02Atomic() {
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
		new T04E02Atomic();
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
			long sum = globalCount.get();
			return sum;
		}
	}
	class ThreadContadora extends Thread{
		long delta = 1;

		@Override
		public void run() {
			while(!stop){
				add_count(delta);
			}
		}

		public ThreadContadora() {
			listaThreads.add(this);
		}

		public int add_count(long delta){
			if(globalCount.get() >= globalCountMax.get()){
				stop = true;
				return -1;
			}
			globalCount.addAndGet(delta);
			return 1;
		}

		public int sub_count(long delta){
			if(globalCount.get() >= globalCountMax.get()){
				stop = true;
				return -1;
			}
			globalCount.addAndGet(-delta);
			return 1;
		}
	}



}