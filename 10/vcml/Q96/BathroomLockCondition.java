package Q96;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BathroomLockCondition {

	private volatile StatusBathroom curStatus = StatusBathroom.Empty;
	private volatile int count;
	private volatile int countManWaiting;
	private volatile int countFemaleWaiting;
	private Lock lock;
	private Condition conditionMale;
	private Condition conditionFemale;

	public BathroomLockCondition() {
		lock = new ReentrantLock();
		conditionMale = lock.newCondition();
		conditionFemale = lock.newCondition();
		count = 0;
		countManWaiting = 0;
		countFemaleWaiting = 0;
	}

	public void enterMale() {
		lock.lock();
		try
		{
			countManWaiting++;
			if (curStatus == StatusBathroom.Female || countFemaleWaiting > 0)
			{
				conditionMale.await();
			}
			count++;
			System.out.println("Enter male");
			countManWaiting--;
			curStatus = StatusBathroom.Male;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
	}

	public void enterFemale() {
		lock.lock();
		try
		{
			countFemaleWaiting++;
			if (curStatus == StatusBathroom.Male || countManWaiting > 0)
			{
				conditionFemale.await();
			}
			count++;
			countFemaleWaiting--;
			System.out.println("Enter female");
			curStatus = StatusBathroom.Female;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
	}

	public void leaveMale() {
		lock.lock();
		try
		{
			count--;
			System.out.println("Leave male");
			if (count == 0)
			{
				System.out.println("empty");
				curStatus = StatusBathroom.Empty;
				conditionFemale.signalAll();
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	public void leaveFemale() {
		lock.lock();
		try
		{
			count--;
			System.out.println("Leave female");
			if (count == 0)
			{
				System.out.println("empty");
				curStatus = StatusBathroom.Empty;
				conditionMale.signalAll();
			}
		}
		finally
		{
			lock.unlock();
		}
	}
}
