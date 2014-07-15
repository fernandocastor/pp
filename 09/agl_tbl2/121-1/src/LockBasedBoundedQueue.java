import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedBoundedQueue {
	ReentrantLock mHeadLock;
	ReentrantLock mTailLock;
	Condition mNotFull;
	Condition mNotEmpty;
	int mHead;
	int mTail;
	int[] mItems;

	public LockBasedBoundedQueue(int capacity) {
		mItems = new int[capacity];
		mHeadLock = new ReentrantLock();
		mTailLock = new ReentrantLock();
		mNotFull = mTailLock.newCondition();
		mNotEmpty = mHeadLock.newCondition();
	}

	public void enq(int v) throws InterruptedException {
		boolean mustWakeDequeuers = false;
		mTailLock.lock();
		try {
			while (mTail - mHead == mItems.length)
				mNotFull.await();

			mItems[mTail % mItems.length] = v;
			if (mTail == mHead)
				mustWakeDequeuers = true;
			++mTail;
		} finally {
			// TODO Auto-generated catch block
			mTailLock.unlock();
		}

		if (mustWakeDequeuers) {
			mHeadLock.lock();
			try {
				mNotEmpty.signalAll();
			} finally {
				mHeadLock.unlock();
			}
		}
	}

	public int deq(int v) throws InterruptedException {
		boolean mustWakeEnqueuers = false;
		mHeadLock.lock();
		int value = 0;
		try {
			while (mTail == mHead)
				mNotEmpty.await();
			
			value = mItems[mHead % mItems.length];
			if (mTail - mHead == mItems.length)
				mustWakeEnqueuers = true;
			--mHead;
		} finally {
			mHeadLock.unlock();
		}
		
		if (mustWakeEnqueuers) {
			mTailLock.lock();
			try {
				mNotFull.signalAll();
			} finally {
				mTailLock.unlock();
			}
		}
		return value;
	}
}
