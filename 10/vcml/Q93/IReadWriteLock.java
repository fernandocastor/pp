package Q93;

import java.util.concurrent.locks.Lock;


// Interface AMP Book
public interface IReadWriteLock {

	Lock readLock();

	Lock writeLock();
}
