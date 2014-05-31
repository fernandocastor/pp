import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Bathroom2 {
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

	public Bathroom2() {
		gender = Gender.NONE;
		count = 0;
		queue = new ConcurrentLinkedQueue<Long>();
	}

	private Gender opposite(Gender g) {
		if (g == Gender.MALE)
			return Gender.FEMALE;
		else
			return Gender.MALE;
	}

	public void enterMan() throws InterruptedException {
		enter(Gender.MALE);
	}

	public void enterWoman() throws InterruptedException {
		enter(Gender.FEMALE);
	}

	public void leaveMan() {
		leave();
	}

	public void leaveWoman() {
		leave();
	}

	public synchronized void enter(Gender g) throws InterruptedException {
		long tid = Thread.currentThread().getId();

		queue.add(tid);

		// Wait until we are the head
		while (queue.peek() != tid)
			wait();

		// Wait the last opposite gender leave
		while (gender == opposite(g))
			wait();

		gender = g;
		count++;

		queue.remove();
	}

	public synchronized void leave() {
		count--;

		if (count == 0) {
			gender = Gender.NONE;
			notifyAll();
		}
	}
}
