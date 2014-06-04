package T07.EX096;


class Male extends Thread{

	ISharedBathroom bathroom;

	public Male(ISharedBathroom bathroom){
		this.bathroom = bathroom;
	}

	@Override
	public void run() {
		while(true){
			synchronized (this) {
				try {
					bathroom.enterMale();
					wait(bathroom.getRandom().nextInt(200));
					bathroom.leaveMale();
					wait(bathroom.getRandom().nextInt(1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
