public class DantesHell {

	public static void main(String[] args) throws InterruptedException {
		Unfortunate[] u = new Unfortunate[5];
		for (int i = 0; i < u.length; ++i)
			u[i] = new Unfortunate(i, u);
		
		for (int i = 0; i < u.length; ++i)
			u[i].start();
		
		for (int i = 0; i < u.length; ++i)
			u[i].join();
	}
}
