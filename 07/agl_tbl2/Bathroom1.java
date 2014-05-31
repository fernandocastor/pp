import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Bathroom1 {
	enum Gender {
		NONE,
		MALE,
		FEMALE,
	}

	Gender gender;
	int count;
	ConcurrentLinkedQueue<Long> queue;

	Lock mutex;
	Condition cond;

	public Bathroom1() {
		gender = Gender.NONE;
		count = 0;
		queue = new ConcurrentLinkedQueue<Long>();
		mutex = new ReentrantLock();
		cond = mutex.newCondition();
	}

	private Gender opposite(Gender g) {
		if (g == Gender.MALE)
			return Gender.FEMALE;
		else
			return Gender.MALE;
	}

	public void enterMan() {
		enter(Gender.MALE);
	}

	public void enterWoman() {
		enter(Gender.FEMALE);
	}

	public void leaveMan() {
		leave();
	}

	public void leaveWoman() {
		leave();
	}

	public void enter(Gender g) {
		long tid = Thread.currentThread().getId();

		queue.add(tid);

		mutex.lock();

		// Wait until we are the head
		while (queue.peek() != tid)
			cond.awaitUninterruptibly();

		// Wait the last opposite gender leave
		while (gender == opposite(g))
			cond.awaitUninterruptibly();

		gender = g;
		count++;

		mutex.unlock();

		queue.remove();
	}

	public void leave() {
		mutex.lock();

		count--;

		if (count == 0) {
			gender = Gender.NONE;
			cond.signalAll();
		}

		mutex.unlock();
	}
}
