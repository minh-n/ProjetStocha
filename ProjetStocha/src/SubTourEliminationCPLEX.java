import java.util.ArrayList;
import ilog.concert.*;

public class SubTourEliminationCPLEX extends IterativeAlgorithm
{
	private CPLEXTSP algo;
	
	public SubTourEliminationCPLEX(CPLEXTSP algo) {
		this.algo = algo; 
	}

	@Override
	protected SolutionTSP oracle() {
		SolutionTSP result = new SolutionTSP();
		try {
			algo.find = algo.model.solve();
			result.setSol(algo.castMatrixInInt());
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected SolutionTSP loop() {
		SolutionTSP result = new SolutionTSP();
		try {
			result.setSol(algo.castMatrixInInt());
			if(result != null) {
				while(addConstraint1c(result)) {
					result = oracle();
					if(result == null)
						break;
				}
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
//	private boolean addConstraint1c(SolutionTSP solution) throws IloException {
//		ArrayList<ArrayList<Integer>> subtour = createSubtour(createLap(solution));
//		if(subtour.size() > 1) {
//			for(int i = 0; i < subtour.size(); i++) {
//				IloLinearNumExpr constraint1c = algo.model.linearNumExpr();
//				if(subtour.get(i).size() > 1) {
//					for(int j = 0; j < subtour.get(i).size(); j++) {
//						if(j+1 < subtour.get(i).size())
//							constraint1c.addTerm(1.0, algo.getMatrixSolution()[subtour.get(i).get(j)][subtour.get(i).get(j+1)]);
//						else
//							constraint1c.addTerm(1.0, algo.getMatrixSolution()[subtour.get(i).get(j)][subtour.get(i).get(0)]);
//					}
//					algo.model.addLe(constraint1c, subtour.get(i).size()-1);
//				}
//			}
//			return true;
//		}
//		else 
//			return false;
//	}
	
	private boolean addConstraint1c(SolutionTSP solution) throws IloException {
		ArrayList<ArrayList<Integer>> subtour = createSubtour(createLap(solution));
		if(subtour.size() > 1) {
			for(int i = 0; i < subtour.size(); i++) {
				IloLinearNumExpr constraint1c = algo.model.linearNumExpr();
				final ArrayList<Integer> tmp = subtour.get(i);
				for(int j = 0; j < tmp.size(); j++) {
					for(int k = 0; k < tmp.size(); k++) {
						constraint1c.addTerm(1.0, algo.getMatrixSolution()[tmp.get(j)][tmp.get(k)]);
					}
				}
				algo.model.addLe(constraint1c, tmp.size()-1);
			}
			return true;
		}
		else 
			return false;
	}
	
//	private ArrayList<int[]> createLap(SolutionTSP solution) {
//		ArrayList<int[]> lap = new ArrayList<int[]>();
//		final int[][] matrixSolution = solution.getSol();
//		int size = matrixSolution.length;
//		int counter = 0;
//		for(int i = 0; i < size; i++) {
//			int[] tmp = new int[2];
//			for(int j = 0; j < size; j++) {
//				if(matrixSolution[i][j] == 1) {
//					tmp[counter] = j;
//					counter++;
//					if(counter > 1) {
//						counter = 0;
//						lap.add(tmp);
//						break;
//					}
//				}
//			}
//			if(counter == 1) {
//				tmp[counter] = tmp[0];
//				lap.add(tmp);
//			}
//		}
//		
//		System.out.println("debut test");
//		System.out.println("taille lap : " + lap.size());
//		int ct = 0;
//		for(int[] i : lap) {
//			System.out.println("index : " + ct);
//			for(int j = 0; j < i.length; j++) {
//				System.out.println(" " + i[j]);
//			}
//			ct++;
//		}
//		
//		return lap;
//	}
	
//	private ArrayList<Integer> createLap(SolutionTSP solution) {
//		ArrayList<Integer> lap = new ArrayList<Integer>();
//		final int[][] matrixSolution = solution.getSol();
//		int size = matrixSolution.length;
//		for(int i = 0; i < size; i++) {
//			int tmp = -1;
//			for(int j = 0; j < size; j++) {
//				if(matrixSolution[i][j] == 1) {
//					tmp = j;
//					lap.add(tmp);
//					break;
//				}
//				if(j == size-1)
//					lap.add(tmp);
//			}
//		}
//		return lap;
//	}
	
	private ArrayList<Integer> createLap(SolutionTSP solution) {
		ArrayList<Integer> lap = new ArrayList<Integer>();
		final int[][] matrixSolution = solution.getSol();
		int size = matrixSolution.length;
		for(int i = 0; i < size; i++) {
			int tmp = -1;
			for(int j = 0; j < size; j++) {
				if(matrixSolution[i][j] == 1) {
					tmp = j;
					lap.add(tmp);
					break;
				}
			}
		}
		
//		System.out.println("debut test");
//		System.out.println("taille lap : " + lap.size());
//		int ct = 0;
//		for(Integer i : lap) {
//			System.out.println("index : " + ct);
//			System.out.println(" " + i);
//			ct++;
//		}
		
		return lap;
	}
	
//	private ArrayList<ArrayList<Integer>> createSubtour(ArrayList<int[]> lap) {
//		int actualCity = 0;
//		ArrayList<ArrayList<Integer>> subtour = new ArrayList<ArrayList<Integer>>();
//		ArrayList<Integer> tour = new ArrayList<Integer>();
//		for(int i = 0; i < lap.size(); i++) {
//			tour.add(actualCity);
//			if(!tour.contains(lap.get(actualCity)[0]))
//				actualCity = lap.get(actualCity)[0];
//			else if(!tour.contains(lap.get(actualCity)[1]))
//				actualCity = lap.get(actualCity)[1];
//			else {
//				subtour.add(tour);
//				if(i != lap.size()-1) {
//					for(int j = 0; j < lap.size(); j++) {
//						if(!tour.contains(j)) {
//							actualCity = j;
//							break;
//						}
//					}
//					tour = new ArrayList<Integer>(); 
//				}
//			}
//		}
//		return subtour;
//	}
	
//	private ArrayList<ArrayList<Integer>> createSubtour(ArrayList<Integer> lap) {
//		int actualCity = 0;
//		ArrayList<ArrayList<Integer>> subtour = new ArrayList<ArrayList<Integer>>();
//		ArrayList<Integer> tour = new ArrayList<Integer>();
//		ArrayList<Integer> views = new ArrayList<Integer>();
//		for(int i = 0; i < lap.size(); i++) {
//			if(actualCity == -1) {
//				subtour.add(tour);
//				if(i != lap.size()-1) {
//					for(int j = 0; j < lap.size(); j++) {
//						if(!views.contains(j)) {
//							actualCity = j;
//							break;
//						}
//					}
//					tour = new ArrayList<Integer>(); 
//				}
//			}
//			else {
//				tour.add(actualCity);
//				views.add(actualCity);
//				if(!tour.contains(lap.get(actualCity)))
//					actualCity = lap.get(actualCity);
//				else {
//					subtour.add(tour);
//					if(i != lap.size()-1) {
//						for(int j = 0; j < lap.size(); j++) {
//							if(!views.contains(j)) {
//								actualCity = j;
//								break;
//							}
//						}
//						tour = new ArrayList<Integer>(); 
//					}
//				}
//			}
//		}
//		return subtour;
//	}
	
	private ArrayList<ArrayList<Integer>> createSubtour(ArrayList<Integer> lap) {
		int actualCity = 0;
		ArrayList<ArrayList<Integer>> subtour = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tour = new ArrayList<Integer>();
		ArrayList<Integer> views = new ArrayList<Integer>();
		for(int i = 0; i < lap.size(); i++) {
			tour.add(actualCity);
			views.add(actualCity);
			if(!tour.contains(lap.get(actualCity)))
				actualCity = lap.get(actualCity);
			else {
				subtour.add(tour);
				if(i != lap.size()-1) {
					for(int j = 0; j < lap.size(); j++) {
						if(!views.contains(j)) {
							actualCity = j;
							break;
						}
					}
					tour = new ArrayList<Integer>(); 
				}
			}
		}
//		System.out.println("debut test");
//		System.out.println("taille sous tour : " + subtour.size());
//		int ct = 0;
//		for(ArrayList<Integer> i : subtour) {
//			System.out.println("###########\nsoustour : " + ct);
//			for(Integer j : i) {
//				System.out.println(" " + j);
//			}
//			ct++;
//		}
		return subtour;
	}
}