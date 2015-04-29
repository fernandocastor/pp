import java.util.LinkedList;

/**
 * Our Thread Safe Deque class.
 * @param <T> the type of boxed value
 */
public class SafeDeque<T> {
    private LinkedList<T> list = new LinkedList<T>();

    /**
     * Insert element at the begin
     */
    public void pushLeft(T element) {
        list.addFirst(element);
    }

    /**
     * Insert element at the end
     */
    public void pushRight(T element) {
        list.addLast(element);
    }

    /**
     * Remove element at the begin
     */
    public T popLeft() {
        return list.removeFirst();
    }

    /**
     * Remove element at the end
     */
    public T popRight() {
        return list.removeLast();
    }
}
