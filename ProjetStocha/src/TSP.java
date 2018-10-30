

public class TSP extends LinearProblem{
	
	private boolean isStocha;
	private int var;
	
	
	public TSP(DataTSP data, boolean maxmin, boolean isStocha)
	{
		super(data, maxmin);
		this.sol = new SolutionTSP();
		this.isStocha = isStocha;
		
		if (this.isStocha)
		{
			genVar();
		}
		
	}
	
	public double objectiveFunction()
	{
		double cost = 0;
		for (int i = 0 ; i < ((DataTSP)this.data).getNbCity() ; i++)
		{
			for (int j = 0 ; j < ((DataTSP)this.data).getNbCity() ; j++)
			{
				cost += ((DataTSP)this.data).getMatrixCost()[i][j]*((SolutionTSP)this.sol).getSol()[i][j];
			}
		}
		
		return cost;
	}
	
	
	public void genVar()
	{
		this.var = (int)Math.random()*15+5;
	}
	
	public void genVar(int val)
	{
		this.var = val;
	}
	
	public boolean getIsStocha()
	{
		return this.isStocha;
	}
	
}