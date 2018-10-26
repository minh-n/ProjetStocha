
public abstract class SimulatedAnnealing
{
	//----------ATTRIBUTES-------------------
	
	protected LinearProblem pb;
	protected int initialTemperature;
	protected int currentTemperature;
	protected int nbIteration;
	protected int currentIteration;
	protected float AcceptationRate;
	protected int nbPalierMax;
	protected int currentPalier;
	//il y aura ptetre des current à virer

	
	
	//----------CONST GET SET-------------------
	
	public SimulatedAnnealing()
	{
		
	}
	
	//----------METHODS-------------------
	
	public void solve()
	{
		
	}
	
	public abstract void initTemp();
	
	public boolean boltzman(float cost, float newCost)
	{
		if (newCost>cost)
			return true;
		else
			//TODO
			return false;
	}
	
	public void kirkpatrick ()
	{
		//TODO
		this.currentTemperature /= 2;
	}
	
	public abstract void generate_neighbor();
	
}