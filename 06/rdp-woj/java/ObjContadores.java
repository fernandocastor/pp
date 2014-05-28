package pp;

public class ObjContadores {
	int contagem = 0;
	BackoffLock mutex = new BackoffLock();

	public void incrementa() {
		mutex.lock();
		contagem++;
		mutex.unlock();
	}
}
