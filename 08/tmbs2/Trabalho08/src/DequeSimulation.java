

import java.util.Random;

/*
 *@author  tmbs2
 *
 */
public class DequeSimulation extends Thread {

	DequeWithLinkedList<Object> deque = new DequeWithLinkedList<>();

	public DequeSimulation(DequeWithLinkedList<Object> deque) {
		this.deque = deque;
	}

	@Override
	public void run() {
		super.run();
		boolean isRun = true;
		Random random = new Random();
		while (isRun) {
			try {
				switch (random.nextInt(3)) {
					case 0:
						deque.pushLeft(5, this.getName());
						break;
					case 1:
						deque.popLeft(this.getName());
						break;
					case 2:
						deque.pushRight(10, this.getName());
						break;
					case 3:
						deque.popRight(this.getName());
						break;
				}
			} catch (Exception e) {
			}
		}
	}
}
