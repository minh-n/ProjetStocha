
public abstract class SimulatedAnnealing
{
	//----------ATTRIBUTES-------------------
	
	protected LinearProblem pb;
	protected int initialTemperature;
	protected int currentTemperature;
	protected int nbIteration;
	protected int currentIteration;
	protected float acceptationRate;
	protected int nbPalierMax;
	protected int currentPalier;
	protected Solution currentSol;
	//il y aura ptetre des current a virer

	
	
	//----------CONST GET SET-------------------
	
	public SimulatedAnnealing(LinearProblem pb, int nbIteration, int nbPalierMax)
	{
		this.pb = pb;
		this.nbIteration = nbIteration;
		this.nbPalierMax = nbPalierMax;
	}
	
	//----------METHODS-------------------
	
	public void solve()
	{
		
	}
	
	public double processTier(/*TODO*/)
	{
		//TODO
		return 1;
	}
	
	
	public boolean refusBoltzman(float cost, float newCost)
	{	
		double delta = newCost-cost;
		
		if (!this.pb.maxMin)
		{
			if (delta > 0)
				return true;
			
			else
			{
				double rand = Math.random();
				if (Math.exp(delta/this.currentTemperature) > rand)
					return false;
				else return true;
				
			}
		}
		
		else
		{
			if (delta < 0)
				return true;
			
			else
			{
				double rand = Math.random();
				if (Math.exp(-delta/this.currentTemperature) > rand)
					return false;
				else return true;
				
			}
		}
		
	}
	
	public abstract void initTemp();
	
	public abstract void generateNeighbor();
	
}