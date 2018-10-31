
public abstract class IterativeAlgorithm
{
	public IterativeAlgorithm() {}
	
	protected abstract Solution oracle();
	protected abstract Solution loop();
}