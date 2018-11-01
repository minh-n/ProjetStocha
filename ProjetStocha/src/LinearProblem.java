import java.util.ArrayList;


public abstract class LinearProblem
{
	//----------ATTRIBUTES-------------------
	
	protected Data data;
	//max is true
	protected boolean maxMin;
	//TODO trouver un type qui a du sens
	protected double matConstraints[][];
	protected double secondMembre[];
	
	protected static Solution sol;
	protected double cost;
	
	//----------CONST GET SET------------------

	public LinearProblem(DataTSP data, boolean maxmin, double matConstraints[][], double secondMembre[])
	{
		this.data = data;
		this.maxMin = maxmin;
		this.matConstraints = matConstraints;
		this.secondMembre = secondMembre;
	}
	
	//in case you don't want to use matConstraints and secondMembre
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
	
	public static void setSol(Solution sol) {
		LinearProblem.sol = sol;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	//--------------METHODS------------------
	
	public abstract double objectiveFunction();
	
}