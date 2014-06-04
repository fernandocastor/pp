package T07.EX096;

import java.util.Random;

public interface ISharedBathroom {

	public void enterMale() throws InterruptedException;
	public void enterFemale() throws InterruptedException;
	public void leaveMale();
	public void leaveFemale();
	public Random getRandom();

	
}
