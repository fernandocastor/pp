package Q96;

public class BathroomSynchronized {

	private volatile StatusBathroom curStatus = StatusBathroom.Empty;
	// private volatile StatusBathroom nextStatus = StatusBathroom.Empty;
	private volatile int count;
	private volatile int countManWaiting;
	private volatile int countFemaleWaiting;

	public BathroomSynchronized() {
		count = 0;
		countManWaiting = 0;
		countFemaleWaiting = 0;
	}

	public void enterMale() {
		synchronized (this) {
			try {
				this.countManWaiting++;
				while (curStatus == StatusBathroom.Female
						|| (curStatus == StatusBathroom.Male && countFemaleWaiting > 0)
						// || (nextStatus == StatusBathroom.Female && countFemaleWaiting > 0)
						) {
					this.wait();
					System.out.println("1. Status: " + curStatus
							+ " - CountFemale: " + countFemaleWaiting
							+ " - count" + count);
				}

				curStatus = StatusBathroom.Male;
				countManWaiting--;
				count++;
				System.out.println("Enter male");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void enterFemale() {
		synchronized (this) {
			try {
				countFemaleWaiting++;
				while (curStatus == StatusBathroom.Male
						|| (curStatus == StatusBathroom.Female && countManWaiting > 0)
						// || (nextStatus == StatusBathroom.Male && countManWaiting > 0)
						) {
					this.wait();
					System.out.println("2. Status: " + curStatus
							+ " - CountMan: " + countManWaiting + " - count: "
							+ count);
				}
				curStatus = StatusBathroom.Female;
				countFemaleWaiting--;
				count++;
				System.out.println("Enter female");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void leaveMale() {
		synchronized (this) {
			count--;
			System.out.println("leave male");

			if (count == 0) {
				System.out.println("empty");
				curStatus = StatusBathroom.Empty;

				// if (countFemaleWaiting > 0)
				// nextStatus = StatusBathroom.Female;
				this.notifyAll();
			}
		}
	}

	public void leaveFemale() {
		synchronized (this) {
			count--;
			System.out.println("leave female");

			if (count == 0) {
				System.out.println("empty");
				curStatus = StatusBathroom.Empty;

				// if (countManWaiting > 0)
				//	nextStatus = StatusBathroom.Male;
				this.notifyAll();
			}
		}
	}
}
