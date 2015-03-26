import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private Philosopher[] philosophers;
    private Chopstick[] chopsticks;
    private boolean[] canEat;

    private int master;
    private int n;
    private final long TIME = 1000;
    private ReentrantLock lock;

    public Main(int n) {
        Philosopher.main = this;
        this.lock = new ReentrantLock();
        this.n = n;
        this.master = 0;
        this.philosophers = new Philosopher[n];
        this.chopsticks = new Chopstick[n];
        this.canEat = new boolean[n];

        for (int i = 0; i < n; ++i) {
            this.chopsticks[i] = new Chopstick(i);
            this.canEat[i] = false;
        }

        for (int i = 0; i < n; ++i) {
            this.philosophers[i] = new Philosopher(i, this.chopsticks[i], this.chopsticks[(i+1)%n]);
            this.philosophers[i].start();
        }

        while (true) {
            try {
                Thread.sleep(TIME);
                updateMaster();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMaster() throws InterruptedException {
        this.lock.lock();
        for (int i = 0; i < n; i += 2) {
            this.canEat[(i + this.master)%n] = false;
            this.canEat[(i + 1 + this.master)%n] = true;
        }
        this.canEat[this.master] = false;
        this.master = (this.master + 1) % this.n;
        for (int i = 0; i < n; ++i) {
            while(chopsticks[i].hasOwner()) {
                Thread.sleep(10);
            }
        }
        this.lock.unlock();
    }

    public boolean canEat(int philosopher) {
        if (this.lock.isLocked()) return false;
        return this.canEat[philosopher];
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        new Main(new Scanner(System.in).nextInt());
    }
}

class Philosopher extends Thread {
    public static Main main;

    private int id;
    private Chopstick c1;
    private Chopstick c2;

    public Philosopher(int id, Chopstick c1, Chopstick c2) {
        this.id = id;
        this.c1 = c1;
        this.c2 = c2;
    }

    public boolean eat() {
        if (!main.canEat(this.id)) return false;

        if (this.c1.hasOwner(this) && this.c2.hasOwner(this)) return true;
        if (this.c1.hasOwner() || this.c2.hasOwner()) return false;

        boolean take1 = this.c1.tryTake(this);
        boolean take2 = this.c2.tryTake(this);

        if (take1 && take2) {
            System.out.printf("The Philosopher %d is eating using the Chopsticks %s and %s\n", this.id, this.c1.toString(), this.c2.toString());
            return true;
        } else {
            System.out.printf("PROBLEM: The Philosopher %d cannot eat using the Chopsticks %s and %s\n", this.id, this.c1.toString(), this.c2.toString());
            return false;
        }
    }

    public void drop(Chopstick chopstick) {
        if (!chopstick.hasOwner(this)) return;

        System.out.printf("The Philosopher %d dropped the Chopstick %s\n", this.id, chopstick.toString());

        if (!chopstick.tryDrop(this)) {
            System.out.printf("PROBLEM: The Philosopher %d cannot drop the Chopstick %s\n", this.id, chopstick.toString());
        }
    }

    public void run() {
        while (true) {
            if (!this.eat()) {
                this.drop(this.c1);
                this.drop(this.c2);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof Philosopher) {
            return this.id == ((Philosopher) obj).id;
        }
        return false;
    }
}

class Chopstick {
    private int id;
    private ReentrantLock lock;
    private Philosopher owner;

    public Chopstick(int id){
        this.id = id;
        this.lock = new ReentrantLock();
        this.owner = null;
    }

    public boolean tryTake(Philosopher owner) {
        if (lock.tryLock()) {
            this.owner = owner;
            return true;
        }
        return false;
    }

    public boolean tryDrop(Philosopher owner) {
        if (this.owner == null) return true;
        else if (this.owner.equals(owner)) {
            this.owner = null;
            lock.unlock();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasOwner() {
        return this.owner != null;
    }

    public boolean hasOwner(Philosopher owner) {
        return (this.owner != null) && (this.owner.equals(owner));
    }

    public String toString() {
        return String.valueOf(id);
    }
}
