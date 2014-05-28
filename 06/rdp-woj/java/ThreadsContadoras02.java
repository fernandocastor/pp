package pp.T06ex02;

import java.util.Random;

public class ThreadsContadoras02 extends Thread {

	boolean stop = true;
	int qtdVezesContagem = 0;
	int random = 0;
	Random rdm = new Random();

	@Override
	public void run() {
		while (stop) {
			// for (int i = 0; i < 1000; i++) {
			T06e02.listaContadores.get(random).incrementa();
			random = rdm.nextInt(T06e02.qtdContadores);
			qtdVezesContagem++;
		}
	}

	public void stopThread() {
		stop = false;
	}
}
