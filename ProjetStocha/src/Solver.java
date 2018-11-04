public abstract class Solver
{
	/**
	 * instance du probleme lineaire a resoudre
	 */
	protected LinearProblem problem;
	
	public Solver(LinearProblem problem) {
		this.problem = problem;
	}
	
	/**
	 * resout le probleme lineaire represente par la variable problem
	 */
	protected abstract void solve();
}