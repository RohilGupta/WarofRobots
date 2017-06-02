package RobotWarSummative;
import java.math.*;

/**
 * This is the template class used to store data and help the application class make calculations
 * @author Rohil
 * @date January 24, 2017
 */
public class GuptaOppData extends OppData{

	//Defines the global variables to be used
	private int id;
	private int a;
	private int s;
	private int health;
	private double points;
	private int numMoves;
	private int lastX, lastY;
	private int moshpit = 0;
	private boolean inMoshpit;

	/**
	 * The constructor used to connect this template class with all of the fighter robots
	 * @param a - Defines the x-coordinate of the robot
	 * @param s - Defines the y-coordinate of the robot
	 * @param id - Defines the id of the robot
	 * @param health - Defines the health of the robot
	 */
	public GuptaOppData(int id, int a, int s, int health){
		super(id, a, s, health);
		this.id = id;
		this.lastX = a;
		this.lastY = s;

	}

	/**
	 * Finds the number of steps each opponent robot has taken
	 * @param avenue - The updated avenue location of the opponent robot
	 * @param street - The updated street location of the opponent robot
	 * @param counter - A counter used to make sure that at least two rounds of data have been gathered
	 * @param numMoves - Passes in the old numMoves calculation
	 */
	public void setNumMoves(int avenue, int street, int counter, int numMoves){
		this.numMoves = numMoves;
		int tempMoves = 0;
		
		tempMoves += Math.abs(this.lastX - avenue);
		tempMoves += Math.abs(this.lastY - street);
		

		//Either assigns an initial value to numMoves or updates its value
		if (counter == 1){
			this.numMoves = tempMoves;
		}
		else{
			if (tempMoves > numMoves){
				this.numMoves = tempMoves;
			}
		}
	}
	
	/**
	 * Gets the number of moves of the robot
	 * @return - Returns the number of moves
	 */
	public int getNumMoves(){
		return this.numMoves;
	}

	/**
	 * Gets the distance from the opponent robot to this robot
	 * @param x - The x-coordinate of this robot
	 * @param y - The y-coordinate of this robot
	 * @return - Returns the distance from the opponent robot to this robot
	 */
	public int getDistance(int x, int y){
		return (Math.abs(y-this.getStreet()) + Math.abs(x-this.getAvenue()));
	}

	/**
	 * Changes the value of the points for an opponent robot
	 * @param points - Passes in the new value of points to update the old one
	 */
	public void setPoints(double points){
		this.points = points;
	}

	/**
	 * Gets the number of points of a certain robot
	 * @return - Returns the number of points of a certain robot
	 */
	public double getPoints(){
		return points;
	}

	/**
	 * Sets the mosh pit value of a certain robot
	 * @param num - Passes in the updated value of the mosh pit value of a certain robot
	 */
	public void setMoshpit(int num) {
		moshpit = num;
	}

	/**
	 * Gets the mosh pit value of a certain robot
	 * @return - Returns the mosh pit value of a certain robot
	 */
	public int getMoshpit() {
		return moshpit;
	}

	/**
	 * Determines whether a certain robot is in a mosh pit
	 * @param b - Updates to tell whether or not the robot is in a mosh pit
	 */
	public void setInMoshpit(boolean b) {
		
		//If the boolean b is true, inMoshpit is changed to be true
		if (b == true){
			inMoshpit = true;
		}
		else{
			inMoshpit = false;
		}
		
	}
	
	/**
	 * Gets the mosh pit value of a certain robot to see whether or not it is in a mosh pit
	 * @return - Returns a boolean variable to determine whether or not the robot is in a mosh pit
	 */
	public boolean getInMoshpit(){
		return inMoshpit;
	}
}