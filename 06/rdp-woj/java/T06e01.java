package pp;

import java.util.ArrayList;

public class T06e01 {
	public static int qtdThreads = 10;
	public static int qtdContadores = 10;
	public static ArrayList<ObjContadores> listaContadores = new ArrayList<ObjContadores>();
	public static ArrayList<ThreadsContadoras> listaThreads = new ArrayList<ThreadsContadoras>();
	public static long tempoEspera = 120000;

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			long begin = 0;
			long end = 0;
			listaContadores = new ArrayList<ObjContadores>();
			listaThreads = new ArrayList<ThreadsContadoras>();
			synchronized (Thread.currentThread()) {
				try {
					startThreads();
					Thread.currentThread().wait(tempoEspera);
					// begin = System.currentTimeMillis();
					stopThreads();
					// sinal para as outras passarem a frente
					Thread.currentThread().sleep(2000);
					mostrarStatisticas();
					// end = System.currentTimeMillis();
					// System.out.println("Tempo excedido: " + (end - begin));
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("\n fim");
			}
		}
	}

	private static void mostrarStatisticas() {
		int i = 1;
		int qtdTotal = 0;
		for (ThreadsContadoras element : listaThreads) {
			System.out.print("Thread0" + i + ": " + element.qtdVezesContagem);
			qtdTotal += element.qtdVezesContagem;
			i++;
		}
		System.out.println("\nqtd Total:" + qtdTotal);
	}

	private static long somarContadores() {
		long i = 0;
		for (ObjContadores element : listaContadores) {
			i += element.contagem;
		}
		return i;
	}

	private static void startThreads() {
		// Criando contadores
		for (int i = 0; i < qtdContadores; i++) {
			listaContadores.add(new ObjContadores());
		}

		// Start threads
		for (int i = 0; i < qtdThreads; i++) {
			listaThreads.add(new ThreadsContadoras());
		}

		for (ThreadsContadoras element : listaThreads) {
			element.start();
		}
	}

	private static void stopThreads() {
		for (ThreadsContadoras element : listaThreads) {
			element.stopThread();
		}
	}
}
