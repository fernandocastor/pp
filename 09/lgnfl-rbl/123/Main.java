import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.ArrayList;

/*
1. The algorithm is the following:
* Each person sits in the round table and each one receives a unique number
* They have agreeded that the person with number 0 will start feeding the person next to him
* Every other person is hungry. A hungry person does nothing besides sleeping until someone comes to feed him.
* Once the person that is not hungry feeded his next colleague, he turns himself hungry and goes to sleep.
* Once a person is feeded, he is no longer hungry and wakes up.
* Each person repeatedly does this until the next person have been fed a constant amount of times
* In the end, everyone will be fed that constant amount of times

This algorithm is starvation-free:

The first person "full" will feed his successor that must be "hungry".
From that on, every hungry person next to it will be fed until it comes back to the first person that now is "hungry".
When the first person is finally fed, its feed count is equal to 1 but also all others must have been fed before that.
This cycles over again.


2. The advantage of this algorithm is that it is straight-forward to implement and the disadvantage is that in practice
only one thread is making progress over time, and all others must be hungry and sleeping.
This algorithm is also decentralized (each person acts independly but following the protocol),
It is low in contention because each person only looks for his successor status and no contention is needed.
It is also deterministic, we know exactly how one person will feed his successor and it will cycle over deterministically.

This algorithm also work if we alternate person to be either full or hungry so this could allow more threads to advance
at the same time.

*/

public class Main {
    public static Thread createUnitThread(final Unit unit) {
        Thread t = new Thread(new Runnable() {
                public void run() {
                	int count = 0;
                	unit.sleepIfHungry();
                    while (count < Constants.FEEDS) {
                    	if (unit.tryFeedNext()) {
                    		++count;
                    	}
                    }
                }
            }, unit.toString());
        unit.setOwner(t);
        return t;
    }

	public static void main(String args[]) {
		ArrayList<Unit> units = new ArrayList<Unit>();
		for (int i = 0; i < Constants.UNITS; ++i) {
			Unit u = new Unit();
			units.add(u);
			if (i != 0) {
				u.setNext(units.get(i-1));
			}
		}
		Unit first = units.get(0);
		Unit last = units.get(Constants.UNITS-1);
		first.setNext(last);

		ArrayList<Thread> unitThreads = new ArrayList<Thread>();
		for (Unit u : units) {
			Thread t = createUnitThread(u);
			unitThreads.add(t);
			t.start();
		}
		
		for (Thread t : unitThreads) {
			try { t.join(); } catch (Exception e) {}
		}

	}	
}

class Unit {
	static protected int overallUnitCount = 0;

	public enum State {
		FULL,
		HUNGRY
	}

	int unitNumber;
	Unit next;
	State state;
	volatile int feedCount;
	Thread ownerThread;

	public Unit() {
		unitNumber = overallUnitCount++;
		next = null;
		if (unitNumber % 2 == 0) {
			state = State.HUNGRY;
		} else {
			state = State.FULL;
		}
		feedCount = 0;
		ownerThread = null;
	}

	public void setNext(Unit other) {
		if (next == null) {
			next = other;
		}
	}

	public void setOwner(Thread t) {
		ownerThread = t;
	}

	public State getState() {
		return state;
	}

	public void setState(State s) {
		state = s;
	}

	public void feed() {
		feedCount += 1;
		setState(State.FULL);
		System.out.println(toString() + " was fed");
		awake();
	}

	public void sleepIfHungry() {
		if (state != State.HUNGRY)
			return;
		sleep();
	}

	private void sleep() {
		LockSupport.park(this);
	}

	protected void awake() {
		LockSupport.unpark(ownerThread);
	}

	private boolean isFinished() {
		return feedCount >= Constants.FEEDS;
	}

	public boolean tryFeedNext() {
		if (state != State.FULL ||
			next.getState() != State.HUNGRY) {
			return false;
		}
		next.feed();
		setState(State.HUNGRY);
		if (!isFinished()) {
			sleep();
		}
		return true;
	}

	public String toString() {
		String s = "unit-" + unitNumber;
		if (state == State.FULL) s += "(full)";
		else if (state == State.HUNGRY) s += "(hungry)";
		else s += "(feeding)";
		if (feedCount > 0) {
			s += "("+feedCount+")";
		}
		return s;
	}
}

class Constants {
	public static int UNITS = 5;
	public static int FEEDS = 50;
}
