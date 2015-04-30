import java.util.Random;

public class Main {
    private class DequeTest extends Thread {
        private HashDeque<Integer> deque;

        public DequeTest(HashDeque<Integer> deque) {
            this.deque = deque;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            Random r = new Random();
            while (true) {
                try {
                    switch (r.nextInt(4)) {
                    case 0:
                        System.out.println(deque.popLeft());
                        break;
                    case 1:
                        System.out.println(deque.popRight());
                        break;
                    case 2:
                        deque.pushLeft(r.nextInt(100));
                        break;
                    case 3:
                        deque.pushRight(r.nextInt(100));
                        break;
                    }
                } catch (Exception e) {

                }
            }
        }           
    };

    public void test() {
        HashDeque<Integer> deque = new HashDeque<Integer>(4);
//      deque.pushLeft(new Integer(5));
//      deque.pushLeft(new Integer(4));
//      deque.pushLeft(new Integer(3));
//      deque.pushLeft(new Integer(2));
//      deque.pushLeft(new Integer(1));
//      deque.pushLeft(new Integer(0));

        for (int i = 0; i < 4; i++)
            new DequeTest(deque).run();
    }


    public static void main(String[] args) {
        new Main().test();
    }
}