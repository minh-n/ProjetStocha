

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
	
	public double objectiveFunction(Solution sol)
	{
		SolutionTSP solTSP = (SolutionTSP)sol;
		int track[] = ((SolutionTSP)sol).getTrack();
		double cost = 0;
		
		for (int i = 0 ; i < ((DataTSP)this.data).getNbCity() ; i++)
		{
			cost += ((DataTSP)this.data).getMatrixCost()[track[i]][solTSP.nextCity(track[i])];
		}
		
		return cost;
	}
	
	
	public void genVar()
	{
		this.setVar((int)Math.random()*15+5);
	}
	
	public void genVar(int val)
	{
		this.setVar(val);
	}
	
	public boolean getIsStocha()
	{
		return this.isStocha;
	}

	public int getVar() {
		return var;
	}

	public void setVar(int var) {
		this.var = var;
	}
	
}