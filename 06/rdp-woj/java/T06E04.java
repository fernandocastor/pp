package T06;

//DIFERENÇA DE DESEMPENHO
// SEM O DETECTOR DE DEADLOCK : 106ms
// COM O DETECTOR DE DEADLOCK : 3341ms

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

class ThreadLock{
	Thread myThread;
	BackoffLock lock;

	public ThreadLock(Thread myThread, BackoffLock lock) {
		this.myThread = myThread;
		this.lock = lock;
	}
}


public class BackoffLock {
	private final AtomicBoolean state = new AtomicBoolean(false);
	private static final int MIN_DELAY = 1;
	private static final int MAX_DELAY = 1000;
	private static ArrayList<ThreadLock> array = new ArrayList<ThreadLock>();
	private static HashMap<Thread,BackoffLock> map = new HashMap<Thread,BackoffLock>();


	public ThreadLock containsLock(BackoffLock lock){
		synchronized (array) {
			for (ThreadLock i : array) {
				if(i.lock.equals(lock)){
					return i;
				}
			}
			return null;
		}
	}

	public synchronized ThreadLock searchThreadLock(Thread thread, BackoffLock lock){
		synchronized (array) {
			for (ThreadLock i : array) {
				if(i.lock.equals(lock) && i.myThread.equals(thread)){
					return i;
				}
			}
			return null;
		}
	}


	public boolean isDeadLock(){
		ThreadLock lock = containsLock(this);
		if(lock != null){
			BackoffLock args = map.get(lock.myThread);
			if(args != null){
				if(searchThreadLock(Thread.currentThread(), args) != null){
					return true;
				}
			}
		}
		return false;
	}

	public void lock() throws DeadlockException{
		boolean first = true;
		Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
		while (true) {
			while (state.get()) {
				if(first){
					map.put(Thread.currentThread(), this);
					first = !first;
				}
				if(isDeadLock())
					throw new DeadlockException();
			}
			if (!state.getAndSet(true)) {
				map.remove(Thread.currentThread());
				synchronized (array) {
					array.add(new ThreadLock(Thread.currentThread(), this));			
				}
				return;
			} else {
				try {
					backoff.backoff();// sEM O BACK OFF COMENTE
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void unlock() {
		synchronized (array) {
			array.remove(containsLock(this));			
		}
		state.set(false);
	}
}
