public abstract class LinearProblem
{
	//----------ATTRIBUTES-------------------
	
	protected Data data;
	//max is true
	protected boolean maxMin;
	protected double matConstraints[][];
	protected double secondMembre[];
	
	public static Solution sol;
	protected double cost;

	//----------CONST GET SET------------------
	
	/**
	 * @param data : class containing the data read from the file
	 * @param maxmin : true if it's a maximisation problem
	 * @param matConstraints
	 * @param secondMembre
	 */
	public LinearProblem(DataTSP data, boolean maxmin, double matConstraints[][], double secondMembre[])
	{
		this.data = data;
		this.maxMin = maxmin;
		this.matConstraints = matConstraints;
		this.secondMembre = secondMembre;
	}
	

	/**
	 * @param data : class containing the data read from the file
	 * @param maxmin : true if it's a maximisation problem
	 */
	public LinearProblem(DataTSP data, boolean maxmin)
	{
		this.data = data;
		this.maxMin = maxmin;
	}
	
	
	public Data getData()
	{
		return this.data;
	}

	public double[][] getMatConstraints()
	{
		return this.matConstraints;
	}

	public double[] getSecondMembre()
	{
		return this.secondMembre;
	}
	
	public boolean getMaxMin()
	{
		return this.maxMin;
	}
	
	public static Solution getSol()
	{
		return sol;
	}
	
	public static void setSol(Solution ssol)
	{
		sol = ssol;
	}
	
	public Double getCost()
	{
		return this.cost;
	}
	
	public void setCost(double cost)
	{
		this.cost = cost;
	}
	
	//--------------METHODS------------------
	
	
	/**
	 * @param sol
	 * @return the cost of the given solution
	 */
	public abstract double objectiveFunction(Solution sol);
	
}