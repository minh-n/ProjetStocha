
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
	
	@Override
    protected void displaySolution() {
        for(int i = 0; i < sol.length; i++){
            for(int j = 0; j < sol[i].length; j++){
                System.out.print(sol[i][j] + " ");
            }
            System.out.println();
        }
    }

	
}
