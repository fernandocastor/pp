package T07.EX096;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedBathroomLock implements ISharedBathroom{

	public static void main(String[] args) {
		new SharedBathroomLock();
	}

	int countM, countF;
	static Random random;
	public ReentrantLock lock;
	volatile public Condition notFemale, notMale;
	volatile int femaleCount, maleCount;
	volatile boolean femaleTrying, maleTrying;


	public SharedBathroomLock() {
		random = new Random();
		femaleCount = maleCount = 0;
		lock = new ReentrantLock();
		notFemale = lock.newCondition();
		notMale = lock.newCondition();
		Thread male = new Male(this);
		Thread female = new Female(this);

		male.start();
		female.start();

	}

	public void enterMale() throws InterruptedException{


		lock.lock();
		try {
			System.out.println("homem entrando");
			while(femaleTrying){
				System.out.println("já há mulheres tentando. Homem esperando");
				notFemale.await();
			}
			while(femaleCount != 0){
				System.out.println("homem tentando");
				maleTrying = true;
				System.out.println("já há mulheres no banheiro. Homem Esperando");
				notFemale.await();
			}
			System.out.println("homem conseguiu");
			maleCount++;
			System.out.println("homem não tenta mais");
			maleTrying = false;
			notMale.signalAll();
		}finally{
			lock.unlock();
		}		
	}
	public void leaveMale(){

		lock.lock();
		try {
			System.out.println("homem saiu");
			maleCount--;
			if (maleCount == 0) {
				System.out.println("não tem mais homens no banheiro");
				notMale.signalAll();
			}
		}finally{
			lock.unlock();
		}
	}
	public void enterFemale() throws InterruptedException{

		lock.lock();
		countF++;
		try {
			System.out.println("mulher entrando");
			while(maleTrying){
				System.out.println("já há homens tentando. Mulher Esperando");
				notMale.await();
			}
			while(maleCount != 0){
				System.out.println("mulher tentando");
				femaleTrying = true;
				System.out.println("já há homens no banheiro. Mulher Esperando");
				notMale.await();
			}
			System.out.println("mulher conseguiu");
			femaleCount++;
			System.out.println("mulher não está mais tentando");
			femaleTrying = false;
			notFemale.signalAll();
		}finally{
			lock.unlock();
		}		
	}
	public void leaveFemale(){

		lock.lock();
		try {
			System.out.println("mulher saiu");
			femaleCount--;
			if (femaleCount == 0) {
				System.out.println("não há mais mulheres no banheiro");
				notFemale.signalAll();
			}
		}finally{
			lock.unlock();
		}	
	}

	@Override
	public synchronized Random getRandom() {
		return random;
	}
	
}
