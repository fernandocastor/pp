import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Our Thread Safe Deque class.
 * @param <T> the type of boxed value
 */
public class Deque<T> {
    private final Lock lockLeft = new ReentrantLock();
    private final Lock lockRight = new ReentrantLock();
    private LinkedList<T> leftList = new LinkedList<T>();
    private LinkedList<T> rightList = new LinkedList<T>();

    /**
     * Insert element at the begin
     */
    public void pushLeft(T element) {
        lockLeft.lock();
        try {
            leftList.addFirst(element);
        } finally {
            lockLeft.unlock();
        }
    }

    public void pushRight(T element) {
        lockRight.lock();
        try {
            rightList.addLast(element);
        } finally {
            lockRight.unlock();
        }
    }

    private void swapLists() {
        LinkedList<T> swap = leftList;
        leftList = rightList;
        rightList = swap;
    }

    /**
     * Remove element at the begin
     */
    public T popLeft() {
        lockLeft.lock();
        try {
            return leftList.removeFirst();
        } catch (NoSuchElementException nse){
            lockRight.lock();
            swapLists();

            try {
                return leftList.removeFirst();
            } finally {
                lockRight.unlock();
            }
        } finally {
            lockLeft.unlock();
        }
    }

    /**
     * Remove element at the end
     */
    public T popRight() {
        lockRight.lock();
        try {
            return rightList.removeLast();
        } catch (NoSuchElementException nse){
            lockRight.unlock();
            lockLeft.lock();
            lockRight.lock();

            try {
                return rightList.removeLast();
            } catch (NoSuchElementException nse2) {
                swapLists();
                return rightList.removeLast();
            } finally {
                lockLeft.unlock();
            }
        } finally {
            lockRight.unlock();
        }
    }
}
