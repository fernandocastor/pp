//Reimplementation follow AMP 8
public class SimpleReadWriteLock {

	private ReadLock readLock;
	private WriteLock writeLock;

	public SimpleReadWriteLock() {
		this.readLock = new ReadLock();
		this.writeLock = new WriteLock();
	}

	public ReadLock readLock() {
		return readLock;
	}

	public WriteLock writeLock() {
		return writeLock;
	}
}