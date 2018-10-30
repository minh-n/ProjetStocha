
public class SolutionTSP extends Solution {

	private int sol[][];
	
	//------------CONST SET GET--------------
	
	public SolutionTSP() {
		super();
	}
	
	public SolutionTSP(int sol[][]) {
		super();
		this.sol = sol;
	}
	
	
	public void setSol(int sol[][])
	{
		this.sol = sol;
	}
	
	public int[][] getSol()
	{
		return this.sol;
	}

	
	//------------METHODS--------------
	
}
