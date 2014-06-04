import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
		int nReaders = Integer.parseInt(args[0]);
		int nWriters = Integer.parseInt(args[1]);
		Counter counter = new Counter();
		SimpleReadWriteLock lock = new SimpleReadWriteLock();

		List<ReaderThread> readers = new ArrayList<ReaderThread>();
		for (int i = 0; i < nReaders; ++i) {
			ReaderThread thread = new ReaderThread(counter, lock.readLock());
			readers.add(thread);
		}

		List<WriterThread> writers = new ArrayList<WriterThread>();
		for (int i = 0; i < nWriters; ++i) {
			WriterThread thread = new WriterThread(counter, lock.writeLock());
			writers.add(thread);
		}

		for (int i = 0; i < nReaders; ++i) {
			readers.get(i).start();
		}

		for (int i = 0; i < nWriters; ++i) {
			writers.get(i).start();
		}

	}
}