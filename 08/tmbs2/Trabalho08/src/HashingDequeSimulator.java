

import java.util.Random;

/*
 *@author  tmbs2
 *
 */
public class HashingDequeSimulator extends Thread {

	HashingDequeWithLinkedList<Object> hashDeque = new HashingDequeWithLinkedList<Object>();
	
	public HashingDequeSimulator(HashingDequeWithLinkedList<Object> hashDeque) {
		this.hashDeque = hashDeque;
		
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
						hashDeque.pushLeft(5, this.getName());
						break;
					case 1:
						hashDeque.pushRight(10, this.getName());
						break;
					case 2:
						hashDeque.popLeft(this.getName());
						break;
					case 3:
						hashDeque.popRight(this.getName());
						break;
				}
			} catch (Exception e) {
			}
		}
	}
	
	
}
