
/**
 * @author corentin
 *
 */
public abstract class SimulatedAnnealing
{
	//----------ATTRIBUTES-------------------
	
	public LinearProblem pb;
	public double initialTemperature;
	public double currentTemperature;
	public int nbIteration;
	public double acceptationRate;
	
	public double acceptationThreshold;
	
	public int failureThreshold;
	public int consecutiveFailure;
	public int currentTier;
	public Solution currentSol;

	public double initCost;
	
	
	//----------CONST GET SET-------------------
	
	/**
	 * @param pb : problem given to the Simulated Annealing
	 * @param nbIteration : the numbers of iteration processed at each tier
	 * @param failureThreshold : the number of times we allow the Simulated
	 * Annealing not to respect the acceptationThreshold
	 * @param acceptationThreshold : the minimum proportion of new solutions
	 * expected from each tier of the simulated annealing for it to keep being productive
	 */
	public SimulatedAnnealing(LinearProblem pb, int nbIteration, int failureThreshold, double acceptationThreshold)
	{
		this.pb = pb;
		this.nbIteration = nbIteration;
		this.failureThreshold = failureThreshold;
		this.acceptationThreshold = acceptationThreshold;
		
		this.consecutiveFailure= 0;
		this.currentTier = 0;
	}
	
	//----------METHODS-------------------
	
	/**
	 * This the main algorithm of the Simulated Annealing,
	 * which processes everything.
	 * At the end, it displays the solution, and the associated cost.
	 */
	public void solve()
	{
		//On initialise ce qui a besoin de l'etre
		this.currentSol = initSol();
		LinearProblem.setSol(this.currentSol);
		this.initTemp();
		System.out.println("initCost = " +this.currentSol.associatedValue);
		System.out.println("initTemp = " +this.initialTemperature);
		
		//On fait un palier tant que ils continuent d'ameliorer la solution
		//
		do
		{
			
			processTier();
			changeTemp();
			//System.out.println("CurrentTemp = "+this.currentTemperature);
			//System.out.println("failures = "+this.consecutiveFailure+"\n");
			
			if (this.acceptationRate < this.acceptationThreshold)
				this.consecutiveFailure++;
			else
				this.consecutiveFailure = 0;
		}while(this.consecutiveFailure < this.failureThreshold);
		
		//ne pas commenter ce displaySolution, il est capital
		LinearProblem.getSol().displaySolution();
		System.out.println(LinearProblem.getSol().getAssociatedValue());
		
	}
	
	
	
	/**
	 * This is a subFunction of the Simulated Annealing, that processes
	 * one tier.
	 */
	public void processTier()
	{
		double nbAccepted = 0;
		Solution neighbor;
		double currentCost = currentSol.getAssociatedValue();
		
		this.currentTier++;
		
		for (int i=0 ; i<this.nbIteration ; i++)
		{
			neighbor = generateNeighbor();

			
			if(refusBoltzman(currentCost, neighbor.getAssociatedValue()))
			{
				//System.out.println("Solution mise a jour "+currentCost+" "+neighbor.getAssociatedValue());
				this.currentSol = neighbor;
				currentCost = currentSol.getAssociatedValue();
				nbAccepted++;
				
				if (currentCost < LinearProblem.getSol().getAssociatedValue())
					LinearProblem.setSol(this.currentSol);
			}
				
		}
		
		this.acceptationRate = nbAccepted/this.nbIteration;
		
	}
	
	
	/**
	 * @param cost : cost of the last accepted solution
	 * @param newCost : cost of the neighbor solution
	 * @return : returns true if the neighbor solution is accepted
	 * It uses the Boltzman probability to chose wether or not to
	 * throw away a solution
	 */
	public boolean refusBoltzman(double cost, double newCost)
	{	
		double delta = newCost-cost;
		if (delta == 0)
			return false;
		
		if (!this.pb.maxMin)
		{
			if (delta < 0)
				return true;
			
			else
			{
				double rand = Math.random();
				//System.out.println(rand+" "+Math.exp(-delta/this.currentTemperature));
				//System.out.println((Math.exp(-delta/this.currentTemperature)) < rand);
				if (Math.exp(-delta/this.currentTemperature) < rand)
					return false;
				else
					return true;
				
			}
		}
		
		else
		{
			if (delta > 0)
				return true;
			
			else
			{
				double rand = Math.random();
				if (Math.exp(delta/this.currentTemperature) < rand)
					return false;
				else 
					return true;
				
			}
		}
		
	}
	
	
	/**
	 * Will set the initial temperature of the Simulated Annealing
	 */
	public abstract void initTemp();
	
	
	/**
	 * Will express how to change the temperature from one tier
	 * to another
	 */
	public abstract void changeTemp();
	
	
	/**
	 * @return : the new solution
	 * Will contain the algorithm that produces new solutions
	 */
	public abstract Solution generateNeighbor();
	
	
	/**
	 * @return : an initial solution of the problem
	 */
	public abstract Solution initSol();
	
}