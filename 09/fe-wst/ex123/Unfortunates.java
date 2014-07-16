package trabalho9.ex123;

public class Unfortunates extends Thread {

	private int index;
	private Soup sopa;
	private boolean alimentado = false;

	public Unfortunates(int i) {
		this.index = i;

	}

	public int posicao() {
		return index + 1 == this.getSopa().getInfelizes().length ? 0
				: index + 1;
	}

	public void descansar() {
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getSopa().getInfelizes()[this.posicao()].setAlimentado(false);
		this.setAlimentado(false);
	}

	public void serve() {

		while (true)
		{
			if (this.getSopa().getPorcoes().get() >= 0)
			{
				if (this.isAlimentado() && this.getSopa().getInfelizes()[this.posicao()].isAlimentado())
				{
					this.descansar();
				}
				else
				{
					if (this.getSopa().getInfelizes()[this.posicao()].isAlimentado() && !this.isAlimentado())
					{
						continue;
					}
					else
					{
						int porcao = this.getSopa().getPorcoes()
								.decrementAndGet();

						if (porcao >= 0)
						{
							this.getSopa().getInfelizes()[this.posicao()].setAlimentado(true);
							System.out.println("Alimentou infeliz numero: "	+ this.getSopa().getInfelizes()[this.posicao()]);
						}
						else
						{
							break;
						}
					}
				}
			}
			else{
				break;
			}
		}

	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Soup getSopa() {
		return sopa;
	}

	public void setSopa(Soup sopa) {
		this.sopa = sopa;
	}

	public boolean isAlimentado() {
		return alimentado;
	}

	public void setAlimentado(boolean alimentado) {
		this.alimentado = alimentado;
	}

	@Override
	public void run() {
		this.serve();
	}

	@Override
	public String toString() {
		return "Infeliz [posicao=" + index + "]";
	}

}
