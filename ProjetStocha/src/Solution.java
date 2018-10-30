

public abstract class Solution {
	
	public double associatedValue;
	
	//--------- CONST GET SET ----------------
	
	public Solution(){}
	
	public void setAssociatedVlue(double val)
	{
		this.associatedValue = val;
	}
	
	public double getAssociatedValue()
	{
		return this.associatedValue;
	}

	
	protected abstract void displaySolution();
	
}
