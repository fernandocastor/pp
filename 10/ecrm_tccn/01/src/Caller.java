import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Caller {
	
	private static final int N_THREADS = 10;

	public static void main(String[] args) {

		// cria um novo pool de Threads com N threads
		ExecutorService application = Executors.newFixedThreadPool(N_THREADS);

		application.execute(new ReadThread());
		application.execute(new WriteThread());
		
		application.shutdown();
	}
}