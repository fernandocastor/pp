import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class EventMonitoring {

	private final AtomicInteger index;
	private final Event[] events;

	public EventMonitoring(int eventsSize) {
		this.index = new AtomicInteger();
		this.events = new Event[eventsSize];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event();
		}
	}

	public void startDorway() {
		Event e = this.events[index.getAndIncrement()];
		e.tid = Thread.currentThread().getId();
		e.type = EventType.START_DORWAY;
	}

	public void startWaiting() {
		Event e = this.events[index.getAndIncrement()];
		e.tid = Thread.currentThread().getId();
		e.type = EventType.START_WAITING;
	}

	public void leaveWaiting() {
		Event e = this.events[index.getAndIncrement()];
		e.tid = Thread.currentThread().getId();
		e.type = EventType.LEAVE_WAITING;
	}

	public void unlock() {
		Event e = this.events[index.getAndIncrement()];
		e.tid = Thread.currentThread().getId();
		e.type = EventType.UNLOCK;
	}
	
	public List<Event> getEvents() {
		List<Event> events = Arrays.asList(Arrays.copyOf(this.events, this.index.get()));
		return events;
	}

	public static enum EventType {
		START_DORWAY,
		START_WAITING,
		LEAVE_WAITING,
		UNLOCK;
	}

	public static class Event {
		volatile long tid;
		volatile EventType type;
		@Override
		public String toString() {
			return "Event [tid=" + tid + ", type=" + type + "]";
		}
	}
}
