/**
 * Adaptado por Jose Fernando para fins academicos. Fonte:http://goo.gl/8jpSLX
 */
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class Cap01ExFilosofos {
  // Makes the code more readable.
  public static class ChopStick {
    // Make sure only one philosopher can have me at any time.
    Lock up = new ReentrantLock();
    // Who I am.
    private final int id;

    public ChopStick(int id) {
      this.id = id;
    }

    public boolean pickUp(Filosofo who, String where) throws InterruptedException {
      if (up.tryLock(10, TimeUnit.MILLISECONDS)) {
        System.out.println(who + " obteve o " + this +" "+ where);
        return true;
      }
      return false;
    }

    public void putDown(Filosofo who, String name) {
      up.unlock();
      System.out.println(who + " deitou o " + this +" "+ name  );
    }

    @Override
    public String toString() {
      return "Palitinho-" + id;
    }
  }

  // One philosoper.
  public static class Filosofo implements Runnable {
    // Which one I am.
    private final int id;
    // The chopsticks on either side of me.
    private final ChopStick palitinhoEsquerdo;
    private final ChopStick palitinhoDireito;
    // Am I full?
    volatile boolean isTummyFull = false;
    // To randomize eat/Think time
    private Random randomGenerator = new Random();
    // Number of times I was able to eat.
    private int noOfTurnsToEat = 0;

    /**
     * **
     *
     * @param id Philosopher number
     *
     * @param palitinhoEsquerdo
     * @param palitinhoDireito
     */
    public Filosofo(int id, ChopStick palitinhoEsquerdo, ChopStick palitinhoDireito) {
      this.id = id;
      this.palitinhoEsquerdo = palitinhoEsquerdo;
      this.palitinhoDireito = palitinhoDireito;
    }

    @Override
    public void run() {

      try {
        while (!isTummyFull) {
          // Think for a bit.
          think();
          // Make the mechanism obvious.
          if (palitinhoEsquerdo.pickUp(this, "esquerdo")) {
            if (palitinhoDireito.pickUp(this, "direito")) {
              // Eat some.
              eat();
              // Finished.
              palitinhoDireito.putDown(this, "direito");
            }
            // Finished.
            palitinhoEsquerdo.putDown(this, "esquerdo");
          }
        }
      } catch (Exception e) {
        // Catch the exception outside the loop.
        e.printStackTrace();
      }
    }

    private void think() throws InterruptedException {
      System.out.println(this + " está pensando");
      Thread.sleep(randomGenerator.nextInt(1000));
    }

    private void eat() throws InterruptedException {
      System.out.println(this + " está comendo");
      noOfTurnsToEat++;
      Thread.sleep(randomGenerator.nextInt(1000));
    }

    // Accessors at the end.
    public int getNoOfTurnsToEat() {
      return noOfTurnsToEat;
    }

    @Override
    public String toString() {
      return "Filosofo-" + id;
    }
  }
  private static final int NO_OF_PHILOSOPHER = 5;
  private static final int SIMULATION_MILLIS = 1000 * 10;

  public static void main(String args[]) throws InterruptedException {
    ExecutorService executorService = null;

    Filosofo[] filosofos = null;
    try {

    	filosofos = new Filosofo[NO_OF_PHILOSOPHER];

      ChopStick[] chopSticks = new ChopStick[NO_OF_PHILOSOPHER];
      for (int i = 0; i < NO_OF_PHILOSOPHER; i++) {
        chopSticks[i] = new ChopStick(i);
      }

      executorService = Executors.newFixedThreadPool(NO_OF_PHILOSOPHER);

      for (int i = 0; i < NO_OF_PHILOSOPHER; i++) {
    	filosofos[i] = new Filosofo(i, chopSticks[i], chopSticks[(i + 1) % NO_OF_PHILOSOPHER]);
        executorService.execute(filosofos[i]);
      }
      Thread.sleep(SIMULATION_MILLIS);
      for (Filosofo philosopher : filosofos) {
        philosopher.isTummyFull = true;
      }

    } finally {
      executorService.shutdown();

      while (!executorService.isTerminated()) {
        Thread.sleep(1000);
      }

      for (Filosofo philosopher : filosofos) {
        System.out.println(philosopher + " => Turnos para alimentar-se ="
                + philosopher.getNoOfTurnsToEat());
      }
    }
  }
}
