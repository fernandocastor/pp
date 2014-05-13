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

    public HashedDeque(int n) {
        hashSize = n;
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
            while (true) {
                pushLeft(new Node());
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
            }
        }
    }

    private class DequeFillerRight extends Thread {
        @Override
        public void run() {
            while (true) {
                pushRight(new Node());
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
            }
        }
    }

    private class DequeEmptierLeft extends Thread {
        @Override
        public void run() {
            while (true) {
                popLeft();
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
            }
        }
    }

    private class DequeEmptierRight extends Thread {
        @Override
        public void run() {
            while (true) {
                popRight();
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Left Index:" + leftIndex + ". Right Index: " + rightIndex);
            }
        }
    }
    
    
    
    

    public static void main(String[] args) {
        int index = -2;
        int hashSize = 4;
        System.out.println(hashSize/2+(index%(hashSize/2)));
        
        HashedDeque hashedDeque = new HashedDeque(4);
        
        int deqFillLeft = 2;
        int deqFillRight = 2;
        int deqEmptyLeft = 12;
        int deqEmptyRight = 12;

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

    }

}
