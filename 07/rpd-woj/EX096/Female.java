package T07.EX096;


class Female extends Thread{

	ISharedBathroom bathroom;

	public Female(ISharedBathroom bathroom){
		this.bathroom = bathroom;
	}


	@Override
	public void run() {
		while(true){
			synchronized (this) {
				try {
					bathroom.enterFemale();
					bathroom.enterFemale();
					bathroom.enterFemale();
					wait(bathroom.getRandom().nextInt(1000));
					bathroom.leaveFemale();
					bathroom.leaveFemale();
					bathroom.leaveFemale();
					wait(bathroom.getRandom().nextInt(200));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}