package trabalho9.ex123;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		Unfortunates[] threads = new Unfortunates[5];
		Soup soup;
		

		// create 5 Unfortunates
		for (int i = 0; i < 5; i++) {
			Unfortunates unfortunates = new Unfortunates(i);
			threads[i] = unfortunates;
		}

		soup = new Soup(threads);
		
		for (Unfortunates thread : threads)
		{
			thread.setSopa(soup);
		}
		
		for (Unfortunates thread : threads)
		{
			thread.start();
		}
		

	}
}
