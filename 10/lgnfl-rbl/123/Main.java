import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.ArrayList;

public class Main {
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
		}

		for (Thread t : unitThreads) {
			t.start();
		}
		
		for (Thread t : unitThreads) {
			try { t.join(); } catch (Exception e) {}
		}
	}

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
		if (unitNumber != 0) {
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
