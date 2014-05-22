import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HashedDeque {

    List<Deque> dequeList;
    int hashSize;
    int leftIndex = 0;  //Next empty index. Goes from 0 to -Infinity.
    int rightIndex = 1; //Next empty index. Goes from 1 to Infinity.
    Lock leftLock;
    Lock rightLock;
    volatile boolean stop = false;

    public HashedDeque(int hashSize) {
        this.hashSize = hashSize;
        dequeList = new ArrayList<Deque>();
        leftLock = new ReentrantLock();
        rightLock = new ReentrantLock();

        for (int i = 0; i < hashSize; i++) {
            dequeList.add(new Deque());
        }
    }

    private int getListIdHash(int index) {
        index = index%hashSize;
        boolean even = hashSize % 2 == 0;
        int middle = hashSize/2;
        int output = -1;
        if (index >= 0) {
            if (index > middle) {
                output = index - middle - 1;
            } else {
                output = index + middle;
                if (even) {
                    output--;
                }
            }
        } else {
            if (!even) {
                if (Math.abs(index) <= middle) {
                    output = hashSize/2 - Math.abs(index);
                } else {
                    output = hashSize - Math.abs(index) + hashSize/2;
                }
            } else {
                if (Math.abs(index) < middle) {
                    output = hashSize/2 - Math.abs(index) - 1;
                } else {
                    output = hashSize - Math.abs(index) + hashSize/2 - 1;
                }
            }
        }

        return output;
    }

    public void pushLeft(Node n) {
        leftLock.lock();
        int listId = getListIdHash(leftIndex);
        Deque deque = dequeList.get(listId);
        deque.pushLeft(n);
        leftIndex--;
        leftLock.unlock();
    }

    public void pushRight(Node n) {
        rightLock.lock();
        int listId = getListIdHash(rightIndex);
        Deque deque = dequeList.get(listId);
        deque.pushRight(n);
        rightIndex++;
        rightLock.unlock();
    }

    public Node popLeft() {
        leftLock.lock();
        int listId = getListIdHash(leftIndex);
        Deque deque = dequeList.get(listId);
        Node n = deque.popLeft();
        if (n != null) {
            leftIndex++;
        }
        leftLock.unlock();
        return n;
    }

    public Node popRight() {
        rightLock.lock();
        int listId = getListIdHash(rightIndex);
        Deque deque = dequeList.get(listId);
        Node n = deque.popRight();
        if (n != null) {
            rightIndex--;
        }
        rightLock.unlock();
        return n;
    }

    private class Node {
    }

    private class Deque {

        LinkedList<Node> list;
        Lock lock;

        public Deque() {
            list = new LinkedList<Node>();
            lock = new ReentrantLock();
        }

        int size() {
            return list.size();
        }

        void pushLeft(Node n) {
            lock.lock();
            list.addFirst(n);
            lock.unlock();
        }

        void pushRight(Node n) {
            lock.lock();
            list.addLast(n);
            lock.unlock();
        }

        Node popLeft() {
            lock.lock();
            Node poll = list.pollFirst();
            lock.unlock();
            return poll;
        }

        Node popRight() {
            lock.lock();
            Node poll = list.pollLast();
            lock.unlock();
            return poll;
        }

    }

    private class DequeFillerLeft extends Thread {
        @Override
        public void run() {
            long numberOfPushes = 0;
            while (!stop) {
                pushLeft(new Node());
//                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
                numberOfPushes++;
            }
            System.out.println("Thread id:" + Thread.currentThread().getId() +". Nodes Pushed by this thread: " + numberOfPushes);
        }
    }

    private class DequeFillerRight extends Thread {
        @Override
        public void run() {
            long numberOfPushes = 0;
            while (!stop) {
                pushRight(new Node());
                numberOfPushes++;
//                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
            }
            System.out.println("Thread id:" + Thread.currentThread().getId() +". Nodes Pushed by this thread: " + numberOfPushes);
        }
    }

    private class DequeEmptierLeft extends Thread {
        @Override
        public void run() {
            long numberOfPops = 0;
            while (!stop) {
                popLeft();
//                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
                 numberOfPops++;
            }
            System.out.println("Thread id:" + Thread.currentThread().getId() +". Nodes Popped by this thread: " + numberOfPops);
        }
    }

    private class DequeEmptierRight extends Thread {
        @Override
        public void run() {
            long numberOfPops = 0;
            while (!stop) {
                popRight();
//                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
                 numberOfPops++;
            }
            System.out.println("Thread id:" + Thread.currentThread().getId() +". Nodes Popped by this thread: " + numberOfPops);
        }
    }


    private class Stopper extends Thread {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            while (!stop) {
                if(System.currentTimeMillis() - currentTime > 10000) {
                    stop = true;
                }
            }
        }
    }




    public static void main(String[] args) {
        int hashSize = Integer.parseInt(args[0]);
        HashedDeque hashedDeque = new HashedDeque(hashSize);

        int deqFillLeft = 4;
        int deqFillRight = 4;
        int deqEmptyLeft = 1;
        int deqEmptyRight = 1;

        for (int i = 0; i < deqFillLeft; i++) {
            hashedDeque.new DequeFillerLeft().start();
        }

        for (int i = 0; i < deqFillRight; i++) {
            hashedDeque.new DequeFillerRight().start();
        }

        for (int i = 0; i < deqEmptyLeft; i++) {
            hashedDeque.new DequeEmptierLeft().start();
        }

        for (int i = 0; i < deqEmptyRight; i++) {
            hashedDeque.new DequeEmptierRight().start();
        }

        hashedDeque.new Stopper().start();
    }

}
