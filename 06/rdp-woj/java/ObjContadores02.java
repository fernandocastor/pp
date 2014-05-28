package pp.T06ex02;

public class ObjContadores02 {
	int contagem = 0;

	// CLHLock mutex = new CLHLock();
	MCSLock mutex = new MCSLock();

	public void incrementa() {
		mutex.lock();
		contagem++;
		mutex.unlock();
	}
}
