public class Main {
    public static void main(String[] args) {
        SafeDeque<Integer> deque = new SafeDeque<Integer>();
        deque.pushLeft(new Integer(5));
        deque.pushLeft(new Integer(4));
        deque.pushLeft(new Integer(3));
        deque.pushLeft(new Integer(2));
        deque.pushLeft(new Integer(1));
        deque.pushLeft(new Integer(0));

        for (int i = 0; i < 6; i++) {
            System.out.println("Pop " +
                ((i%2 == 0) ? deque.popLeft() : deque.popRight()));
        }
    }
}
