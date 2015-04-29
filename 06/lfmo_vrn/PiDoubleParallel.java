import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class PiDoubleParallel implements Runnable{
	final static int NUMBER_OF_THREADS = 4;
	final static int N_POINTS = 10000000;
	final static int BOUND = 1000000;
	final static int RADIUS = BOUND; //Raio do circulo	
	final Point center = new Point(BOUND, BOUND);
	private AtomicInteger pointsInside;
	
		
	public static void main(String[] args) throws InterruptedException {		
		long initialTime = System.currentTimeMillis();
		PiDoubleParallel pp = new PiDoubleParallel();
		pp.pointsInside = new AtomicInteger(); 
		List<Thread> threads = new LinkedList<Thread>();
		
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {			
			threads.add(new Thread(pp));
			threads.get(i).start();
		}
		
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {			
			threads.get(i).join();
		}
				
		BigDecimal r = BigDecimal.valueOf((double)pp.pointsInside.intValue()/N_POINTS);		
		BigDecimal pi = r.multiply(new BigDecimal(4));		
		long finalTime = System.currentTimeMillis();
		System.out.println("Valor do PI: " + pi);
		System.out.println("Tempo de Execução: " + ((double)(finalTime - initialTime)/1000) + " segundos.");
		System.out.println("Para " + N_POINTS + " Pontos.");
		System.out.println("Pontos Dentro do círculo: " + pp.pointsInside + " Pontos.");
		System.out.println("Raio do círculo: " + RADIUS);
	}
	
	private List<Point> generate(int n) {
		List<Point> points = new ArrayList<Point>();
		for (int i = 0; i < (int)(N_POINTS/n); i++) {			
			int x =(int) (Math.random() * BOUND);
			int y = (int) (Math.random() * BOUND);	
			Point p = new Point(x, y);
			points.add(p);			
		}		
		return points;
	}
	
	private int pointsInsideTheCircle(List<Point> points){
		int pointsInCircle = 0;
		for (Point p : points) {
			if(isInsideTheCircle(p)){
				pointsInCircle++;
			}	
		}
		return pointsInCircle;		
	}

	/**
	 * Formula para Ponto externo à circunferência: (xA – a)^2 + (yA – b)^2 > R^2
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInsideTheCircle(Point p) {			
		BigDecimal t_x = new BigDecimal(Math.pow((p.getX() - center.getX()), 2));
		BigDecimal t_y = new BigDecimal(Math.pow((p.getY() - center.getY()), 2));
		BigDecimal t_r = new BigDecimal(Math.pow(RADIUS, 2));
		if(t_x.add(t_y).compareTo(t_r)>0){
			return false;
		}			
		return true;
	}
	
	@Override
	public void run() {
		List<Point> generatedPoints = generate(NUMBER_OF_THREADS);						
		pointsInside.addAndGet(pointsInsideTheCircle(generatedPoints)); //Verify...	
	}
	
	
	class Point{
		private int x;
		private int y;
		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}		
	}


}
