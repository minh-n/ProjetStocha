import java.util.ArrayList;


public abstract class LinearProblem
{
	//----------ATTRIBUTES-------------------
	
	protected DataTSP data;
	//max is true
	protected boolean maxMin;
	//TODO trouver un type qui a du sens
	protected String constraints;
	
	//----------CONST GET SET------------------
	
	public LinearProblem(DataTSP data)
	{
		this.data = data;
		//TODO stuff
	}
	
	//--------------METHODS------------------
	
	public abstract float objective_function();
	
}