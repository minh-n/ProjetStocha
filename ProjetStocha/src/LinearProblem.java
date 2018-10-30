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
	
	protected Solution sol;
	
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
	
	public Solution getSol()
	{
		return this.sol;
	}
	
	
	
	//--------------METHODS------------------
	
	public abstract double objectiveFunction();
	
}