import java.util.ArrayList;


public class TSPDeterministic extends LinearProblem{
	
	private ArrayList<ArrayList<Boolean>> bestSolution;
	private ArrayList<ArrayList<Boolean>> tempSolution;
	
	
	public TSPDeterministic(Data data)
	{
		super(data);
		//TODO stuff
	}
	
	public float objective_function()
	{
		//TODO calculer un co a partir de sol
		float cost = 666;
		
		return cost;
	}
	
}