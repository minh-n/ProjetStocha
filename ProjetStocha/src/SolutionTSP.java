import java.util.ArrayList;

public class SolutionTSP extends Solution {

	//There are two possible ways to represent the solutions
	//A n2 matrix, displaying the arcs that are used by the solution
	private int sol[][];
	//A tab displaying the cities in order.
	private int track[];
	
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
	
	public void setTrack(int track[])
	{
		this.track = track;
	}
	
	public int[] getTrack()
	{
		return this.track;
	}

	
	//------------METHODS--------------
	
	/**
	 * This function use the matrix form of the solution to build
	 * the "track" one
	 */
	public void trackFromSol(){
		int conversion[] = new int[sol.length];
		conversion[0] = 0;
		int idx = 0;
		
		for(int i = 0; i < sol.length; i++)
		{
			if (sol[conversion[idx]][i] == 1 & i != 0)
			{
				conversion[idx+1] = i;
				idx++;
			}
		}
			
		this.track = conversion;
	}
	
	
	/**
	 * This function uses the "track" form of the solution to build
	 * the matrix form.
	 */
	public void solFromTrack(){
		int conversion[][] = new int[track.length][track.length];
		
		for(int i = 0; i < track.length; i++)
		{
			for(int j = 0; j < track.length; j++)
				conversion[i][j] = 0;
		}
		
		for (int i = 0 ; i< track.length ; i++)
		{
			conversion[track[i]][nextCity(track[i])] = 1;
		}
		
		this.sol = conversion;
	}
	
	
	public void displayTrack()
	{
		if (this.track == null)
			trackFromSol();
		for(int i = 0; i < track.length; i++)
		{
			System.out.println(track[i]);
		}
		System.out.println();
	}

	
    protected void displaySolution() {
		if (this.sol == null)
			solFromTrack();
		
        for(int i = 0; i < sol.length; i++){
            for(int j = 0; j < sol[i].length; j++){
                System.out.print(sol[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

	
	public SolutionTSP cloneSol()
	{
		SolutionTSP solutionCopy = new SolutionTSP();
		
		int nbCity;
		if (this.sol != null)
		{
			nbCity = this.sol.length;
			
			int solCopy[][] = new int[nbCity][nbCity];
			for (int i=0 ; i<nbCity ; i++)
			{
				solCopy[i] = this.sol[i].clone();
			}
			solutionCopy.sol = solCopy;
		}
		
		if (this.track != null)
		{
			nbCity = this.track.length;
			
			int trackCopy[] = this.track.clone();
			solutionCopy.track = trackCopy;
		}
		
		solutionCopy.associatedValue = this.associatedValue;
		return solutionCopy;
	}
	
	
	/**
	 * @param city : the index of a city
	 * @return : the index of the city that comes next in the solution
	 */
	public int nextCity(int city)
	{
		int idx = 0;
		
		while (this.track[idx] != city)
			idx++;
		
		idx++;
		if (idx == this.track.length)
			idx = 0;
		
		return this.track[idx];
	}
	
	
	
	/**
	 * @param city1 : index of the first city
	 * @param city2 : index of the last city
	 * Takes a subpart of the solution (from city1 to city2),
	 * and reverses it
	 */
	public void reverseTrack(int city1, int city2)
	{
		ArrayList<Integer> subTrack = new ArrayList<Integer>();
		int idx = 0;
		int idxCity1 = -1;
		
		while (this.track[idx] != city1)
			idx++;
		
		idxCity1 = idx;
		
		do
		{
			subTrack.add(this.track[idx]);
			idx++;
			if (idx == this.track.length)
				idx = 0;
		}while (idx != city2);
		
		subTrack.add(this.track[idx]);
		idx = idxCity1;
		
		while (subTrack.size() != 0)
		{
			this.track[idx] = subTrack.get(subTrack.size()-1);
			subTrack.remove(subTrack.size()-1);
			idx++;
			if (idx == this.track.length)
				idx = 0;
		}	
	}
		
}
