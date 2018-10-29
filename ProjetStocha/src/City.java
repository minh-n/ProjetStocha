
public class City {
	
	private double xCoord, yCoord;
	private int id;
	
	public City(){}
	
	public City(double a, double b, int i)
	{
		xCoord = a;
		yCoord = b;
		id = i;
	}
	
	public double getDistanceToAnotherCity(City city)
	{	
		return Math.sqrt(Math.pow((city.xCoord - this.xCoord), 2) 
				+ 	Math.pow((city.yCoord - this.yCoord), 2));
	}

	public double getX() {
		return xCoord;
	}

	public void setX(double xCoord) {
		this.xCoord = xCoord;
	}

	public double getY() {
		return yCoord;
	}

	public void setY(double yCoord) {
		this.yCoord = yCoord;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		return "City " + id + " coordinates : (" + this.xCoord + ", " + this.yCoord + ").";
	}

}
