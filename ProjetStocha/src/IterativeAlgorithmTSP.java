import java.util.ArrayList;
import ilog.concert.*;
import ilog.cplex.*;

public class IterativeAlgorithmTSP extends IterativeAlgorithm
{
	private CPLEXTSP algo;
	
	public IterativeAlgorithmTSP(CPLEXTSP algo) {
		this.algo = algo; 
	}

	@Override
	protected SolutionTSP oracle() {
		SolutionTSP result = new SolutionTSP();
		try {
			algo.model.solve();
			result.setSol(algo.castInIntMatrix());
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected SolutionTSP loop() {
		SolutionTSP result = new SolutionTSP();
		try {
			result.setSol(algo.castInIntMatrix());
			while(addConstraint1c(algo.model, result)) {
				result = oracle();
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean addConstraint1c(IloCplex model, SolutionTSP solution) throws IloException {
		ArrayList<ArrayList<Integer>> subtour = createSubtour(createLap(solution));
		if(subtour.size() > 1) {
			for(int i = 0; i < subtour.size(); i++) {
				IloLinearNumExpr constraint1c = algo.model.linearNumExpr();
				for(int j = 0; j < subtour.get(i).size(); j++) {
					if(j+1 < subtour.get(i).size())
						constraint1c.addTerm(1.0, algo.getMatrixSolution()[subtour.get(i).get(j)][subtour.get(i).get(j+1)]);
					else
						constraint1c.addTerm(1.0, algo.getMatrixSolution()[subtour.get(i).get(j)][subtour.get(i).get(0)]);
				}
				algo.model.addLe(constraint1c, subtour.get(i).size()-1);
			}
			return true;
		}
		else 
			return false;
	}
	
	private ArrayList<int[]> createLap(SolutionTSP solution) {
		ArrayList<int[]> lap = new ArrayList<int[]>();
		int counter = 0;
		for(int i = 0; i < solution.getSol().length ; i++) {
			int[] tmp = new int[2];
			for(int j = 0; j < solution.getSol()[i].length; j++) {
				if(solution.getSol()[i][j] == 1) {
					tmp[counter] = j;
					counter++;
					if(counter > 1) {
						counter = 0;
						lap.add(tmp);
						break;
					}
				}
			}
		}
		return lap;
	}
	
	private ArrayList<ArrayList<Integer>> createSubtour(ArrayList<int[]> lap) {
		int actualCity = 0;
		ArrayList<ArrayList<Integer>> subtour = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tour = new ArrayList<Integer>();
		for(int i = 0; i < lap.size(); i++) {
			tour.add(actualCity);
			if(!tour.contains(lap.get(actualCity)[0]))
				actualCity = lap.get(actualCity)[0];
			else if(!tour.contains(lap.get(actualCity)[1]))
				actualCity = lap.get(actualCity)[1];
			else {
				//TODO check prob de reference
				subtour.add(tour);
				if(i != lap.size()-1) {
					for(int j = 0; j < lap.size(); j++) {
						if(!tour.contains(j)) {
							actualCity = j;
							break;
						}
					}
					tour = new ArrayList<Integer>(); 
				}
			}
		}
		return subtour;
	}
}