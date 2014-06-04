package T07.EX093;

import java.util.Random;


public class Executar {

	int readerTempo = 100;
	int writerTempo = 100;
	SimpleReadWriteLock simpleRWL = new SimpleReadWriteLock();
	int args = 0;
	Random random = new Random();

	public Executar() {
		new ThreadEscritora().start();
		new ThreadEscritora().start();
		new ThreadEscritora().start();
		new ThreadEscritora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
		new ThreadLeitora().start();
	
	}

	public static void main(String[] args) {
		new Executar();
	}

	class ThreadLeitora extends Thread {

		@Override
		public void run() {
			while(true){
				simpleRWL.readLock.lock();
				try{
					readerTempo = 1000;
					System.out.println("valor atual: " + args);
				}
				finally{
					simpleRWL.readLock.unlock();
				}
				synchronized (this) {
					try {
						wait(random.nextInt(readerTempo/2));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	class ThreadEscritora extends Thread {

		@Override
		public void run() {
			while(true){
				simpleRWL.writeLock.lock();
				try{
					writerTempo = 1000;
					System.out.println("escrevendo em args: " + args);
					args++;
				}
				finally{
					simpleRWL.writeLock.unlock();
				}
				synchronized (this) {
					try {
						wait(random.nextInt(writerTempo/2));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
