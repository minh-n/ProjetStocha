import java.util.ArrayList;


public class TSPStochastic extends LinearProblem{
	
	//ptetre qu'on peut mettre des bool
	private ArrayList<ArrayList<Boolean>> bestSolution;
	private ArrayList<ArrayList<Boolean>> tempSolution;
	
	private ArrayList<Float> average;
	private ArrayList<ArrayList<Float>> varianceCovariance;
	
	
	public TSPStochastic(Data data)
	{
		super(data);
		//TODO stuff
	}
	
	public float objective_function()
	{
		//TODO calculer un coût à partir de sol
		float cost = 666;
		
		return cost;
	}
	
}