
/*
 *@author  tmbs2
 *
 */
public class Execution {

	public static void main(String[] args) {

	/*	//execution Deque
		DequeWithLinkedList<Object> deque = new DequeWithLinkedList<>();

        for (int i = 0; i < 5; i++) {
    		new Thread(new DequeSimulation(deque)).start();

        }*/
        
        
        //execution Hashing Deque
        HashingDequeWithLinkedList<Object> hashingDeque = new HashingDequeWithLinkedList<>();

        for (int i = 0; i < 0; i++) {
    		new Thread(new HashingDequeSimulator(hashingDeque)).start();

		}
	}

}
