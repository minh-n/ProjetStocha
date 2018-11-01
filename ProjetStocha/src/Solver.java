public abstract class Solver
{
	protected LinearProblem problem;
	
	public Solver(LinearProblem problem) {
		this.problem = problem;
	}
	
	protected abstract void solve();
}