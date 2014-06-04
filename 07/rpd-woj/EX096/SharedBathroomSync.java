package T07.EX096;

import java.util.Random;

public class SharedBathroomSync implements ISharedBathroom{

	public static void main(String[] args) {
		new SharedBathroomSync();
	}

	int countM, countF;
	static Random random;
	volatile int femaleCount, maleCount;
	volatile boolean femaleTrying, maleTrying;


	public SharedBathroomSync() {
		random = new Random();
		femaleCount = maleCount = 0;
		Thread male = new Male(this);
		Thread female = new Female(this);

		male.start();
		female.start();

	}

	public void enterMale() throws InterruptedException{


		synchronized (this) {
			System.out.println("homem entrando");
			while(femaleTrying){
				System.out.println("já há mulheres tentando. Homem esperando");
				wait();
			}
			System.out.println("homem tentando");
			while(femaleCount != 0){
				maleTrying = true;
				System.out.println("já há mulheres no banheiro. Homem Esperando");
				wait();
			}
			System.out.println("homem conseguiu");
			maleCount++;
			System.out.println("homem não tenta mais");
			maleTrying = false;
			notifyAll();
		}
	}
	public void leaveMale(){

		synchronized (this) {
			System.out.println("homem saiu");
			maleCount--;
			if (maleCount == 0) {
				System.out.println("não tem mais homens no banheiro");
				notifyAll();
			}
		}
	}
	public void enterFemale() throws InterruptedException{

		synchronized (this) {
			countF++;
			System.out.println("mulher entrando");
			while(maleTrying){
				System.out.println("já há homens tentando. Mulher Esperando");
				wait();
			}
			while(maleCount != 0){
				System.out.println("mulher tentando");
				femaleTrying = true;
				System.out.println("já há homens no banheiro. Mulher Esperando");
				wait();
			}
			System.out.println("mulher conseguiu");
			femaleCount++;
			System.out.println("mulher não está mais tentando");
			femaleTrying = false;
			notifyAll();
		}
	}
	public void leaveFemale(){

		synchronized (this) {
			System.out.println("mulher saiu");
			femaleCount--;
			if (femaleCount == 0) {
				System.out.println("não há mais mulheres no banheiro");
				notifyAll();
			}
		}
	}

	@Override
	public synchronized Random getRandom() {
		return random;
	}

}