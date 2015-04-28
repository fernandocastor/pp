import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



/**
The value of PI can be calculated in a number of ways. Consider the following method of approximating PI
- Inscribe a circle in a square
- Randomly generate points in the square
- Determine the number of points in the square that are also in the circle
- Let r be the number of points in the circle divided by the number of points in the square
- PI ~ 4 r
Note that the more points generated, the better the approximation


 * @author Leonardo Fernandes
 * @since 04/21/2015
 *
 */
public class PiSerial {
	final static int N_POINTS = 10000000;
	final static int BOUND = 1000000;
	final static int RADIUS = BOUND; //Raio do circulo
	final Point center = new Point(BOUND, BOUND);
	//...	
	private List<Point> generatedPoints;
	private int pointsInside;
		
	public static void main(String[] args) throws InterruptedException {		
		long initialTime = System.currentTimeMillis();
		PiSerial pp = new PiSerial();
		pp.generatedPoints = pp.generate(); //Generate..
		pp.pointsInside = 0;
		pp.pointsInside = pp.pointsInsideTheCircle(pp.generatedPoints); //Verify...
		BigDecimal r = BigDecimal.valueOf((double)pp.pointsInside/N_POINTS);		
		BigDecimal pi = r.multiply(new BigDecimal(4));		
		long finalTime = System.currentTimeMillis();
		System.out.println("Valor do PI: " + pi);
		System.out.println("Tempo de Execu��o: " + ((double)(finalTime - initialTime)/1000) + " segundos.");
		System.out.println("Para " + N_POINTS + " Pontos.");
		System.out.println("Pontos Dentro do c�rculo: " + pp.pointsInside + " Pontos.");
		System.out.println("Raio do c�rculo: " + RADIUS);
	}
	
	private List<Point> generate() {
		List<Point> points = new ArrayList<Point>();
		for (int i = 0; i < N_POINTS; i++) {			
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
	 * Formula para Ponto externo � circunfer�ncia: (xA � a)^2 + (yA � b)^2 > R^2
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
