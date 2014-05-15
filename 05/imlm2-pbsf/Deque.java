import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class Deque {

    private class Node {}

    LinkedList<Node> list1;
    LinkedList<Node> list2;
    Lock lockLeft;
    Lock lockRight;

    public Deque() {
        list1 = new LinkedList<Node>();
        list2 = new LinkedList<Node>();
        lockLeft = new ReentrantLock();
        lockRight = new ReentrantLock();
    }
    
    public int size() {
        return list1.size() + list2.size();
    }

    void pushLeft(Node n) {
        lockLeft.lock();
        list1.addFirst(n);
        lockLeft.unlock();
    }

    void pushRight(Node n) {
        lockRight.lock();
        list2.addLast(n);
        lockRight.unlock();
    }

    void rebalance() {
        // Acting as a swap of the lists.
        LinkedList<Node> temp = list2;
        list2 = list1;
        list1 = temp;
    }

    Node popLeft() {
        lockLeft.lock();
        Node poll = list1.pollFirst();
        if (poll == null) {
            lockRight.lock();
            rebalance();
            poll = list1.pollFirst();
            lockRight.unlock();
        }
        lockLeft.unlock();
        return poll;
    }

    Node popRight() {
        lockRight.lock();
        Node poll = list2.pollLast();
        if (poll == null) {
            lockRight.unlock();
            lockLeft.lock();
            lockRight.lock();
            poll = list2.pollLast();
            if(poll == null) {
                rebalance();
                poll = list2.pollLast();
            }
            lockLeft.unlock();
        }
        lockRight.unlock();
        return poll;
    }


    private class DequeFillerLeft extends Thread {
        @Override
        public void run() {
            while (true) {
                pushLeft(new Node());
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Size:" + size());
            }
        }
    }

    private class DequeFillerRight extends Thread {
        @Override
        public void run() {
            while (true) {
                pushRight(new Node());
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Size:" + size());
            }
        }
    }

    private class DequeEmptierLeft extends Thread {
        @Override
        public void run() {
            while (true) {
                popLeft();
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Size:" + size());
            }
        }
    }

    private class DequeEmptierRight extends Thread {
        @Override
        public void run() {
            while (true) {
                popRight();
                System.out.println("Thread id:" + Thread.currentThread().getId() +". Size:" + size());
            }
        }
    }

    public static void main(String[] args) {
        Deque deque = new Deque();

        int deqFillLeft = 1;
        int deqFillRight = 1;
        int deqEmptyLeft = 10;
        int deqEmptyRight = 10;

        for (int i = 0; i < deqFillLeft; i++) {
            deque.new DequeFillerLeft().start();
        }

        for (int i = 0; i < deqFillRight; i++) {
            deque.new DequeFillerRight().start();
        }

        for (int i = 0; i < deqEmptyLeft; i++) {
            deque.new DequeEmptierLeft().start();
        }

        for (int i = 0; i < deqEmptyRight; i++) {
            deque.new DequeEmptierRight().start();
        }

    }

}
