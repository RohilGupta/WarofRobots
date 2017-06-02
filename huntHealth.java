package RobotWarSummative;
import becker.robots.City;

import java.awt.Color;
import java.lang.Math;
import java.util.*;
import becker.robots.*;

/**
 * This is the health-hunter fighter robot that utilizes GuptaOppData to make calculations
 * @author Rohil G.
 * @date January 24, 2017
 */
public class huntHealth extends FighterRobot{
	
	//Defines the global variables to be used
	private int health;
	private GuptaOppData[] extendedData = null;
	private final static int ATTACK = 3;
	private final static int DEFENCE = 3;
	private static int NUMMOVES = 4;
	private int targetX, targetY, targetID;
	private int energy;
	
	/**
	 * The constructor method for the huntHealth fighter robot to be used
	 * @param c - Defines the city the robot is in
	 * @param a - Defines the x-coordinate of the robot
	 * @param s - Defines the y-coordinate of the robot
	 * @param d - Defines the direction the robot is facing
	 * @param id - Defines the id of the robot
	 * @param health - Defines the health of the robot
	 */
	public huntHealth(City c, int a, int s, Direction d,int id, int health){
		super(c,a,s,d,id,health, ATTACK, DEFENCE, NUMMOVES);
		this.health = health;
		this.setLabel();
	}
	
	/**
	 * This method is used to update the colour of the robot based on its health
	 */
	public void setLabel(){
		this.setLabel(super.getID() + " " + this.health);

		//Changes the colour of the robot to black if it is dead (health <= 0)
		if (this.health == 0){
			this.setColor(Color.BLACK);
		}
		else{
			this.setColor(Color.GREEN);
		}
	}

	/**
	 * This method is used to get the robot to go to a certain location
	 */
	public void goToLocation(int s, int a){
		int y = this.getStreet() - a;
		int x = this.getAvenue() - s;

		//Checks to see if the robot should move right or left
		if (x>0){
			super.turnLeft();

			//Moves the robot left the number of steps it has been "allowed" to take
			for (int i = 0; i<x; i++){
				super.move();
			}
			super.turnRight();
		}

		else{
			super.turnRight();

			//Moves the robot right the number of steps it has been "allowed" to take
			for (int i = x; i<0; i++){
				super.move();
			}
			super.turnLeft();
		}

		//Checks to see if the robot should move up or down
		if (y>0){

			//Moves the robot up the number of steps it has been "allowed" to take
			for (int i = 0; i<y; i++){
				super.move();
			}
		}
		else{
			super.turnAround();

			//Moves the robot down the number of steps it has been "allowed" to take
			for (int i = y; i<0; i++){
				super.move();
			}
			super.turnAround();
		}
	}

	/**
	 * This method returns a TurnRequest object that details where the robot would like to move to and who it would like to attack
	 */
	public TurnRequest takeTurn(int energy, OppData[] data){
		//Defines the variables to be used
		this.energy = energy;
		TurnRequest request;
		GuptaOppData target;
		GuptaOppData[] extendedData = extendOppData(data);
		
		target = getTarget(); //Gets the target to attack
		goToTarget(target); //Sets the targetX and targetY
		
		//If the robot is close enough to attack its target and has enough energy, it attacks its opponent
		if (targetX == target.getAvenue() && targetY == target.getStreet() && energy >= 20){
			targetID = target.getID();
			request = new TurnRequest(targetX,targetY,targetID, energy/(100/ATTACK));
		}
		
		//If either of the conditions above are not met, the robot simply moves to get closer to its target
		else{
			targetID = -1;
			request = new TurnRequest(targetX,targetY,targetID,0);
		}
		
		return request;
	}
	
	/**
	 * Determines the best target to attack
	 * @return - Returns the target that is to be attacked
	 */
	private GuptaOppData getTarget(){
		GuptaOppData[] sortedHealth;
		sortedHealth = sortHealth(); //sorts the health of each player

		//Checks to see if the lowest health belongs to this robot
		if (sortedHealth[0].getID() == this.getID()){
			return sortedHealth[1];
		}
		else{
			return sortedHealth[0];
		}
	}
	
	/**
	 * Moves the robot as close to the target location as possible
	 * @param target - The GuptaOppData object that is to be attacked
	 */
	private void goToTarget(GuptaOppData target) {
		
		//Defines the variables to be used
		int limitingNum, difX, difY;

		//Determines the limiting number of moves by comparing the energy of the robot with the number of moves it is allowed to take
		if (energy/5 > NUMMOVES){
			limitingNum = NUMMOVES;
		}
		else{
			limitingNum = energy/5;
		}

		//Determines if the distance from this robot to the target location is greater than the limiting number calculated above 
		if (target.getDistance(this.getAvenue(), this.getStreet()) > limitingNum){
			difX = Math.abs(target.getAvenue() - this.getAvenue());
			difY = Math.abs(target.getStreet() - this.getStreet());
			int X = target.getAvenue() - this.getAvenue();
			int Y = target.getStreet() - this.getStreet();
			int count = 0, tempX = 0, tempY = 0;

			//Runs as long the value of the limiting number is not exceeded
			while (count<limitingNum){

				//Adds to the movement in the x-direction of the robot if it needs to move more in this direction
				if (tempX < difX){
					tempX +=1;
					count += 1;
				}

				//Checks to see if the value of the limiting number has been exceeded
				if (count == limitingNum){
					break;
				}

				//Adds to the movement in the y-direction of the robot if it needs to move more in this direction
				if (tempY < difY){
					tempY+=1;
					count+=1;
				}
			}

			//Accounts for the direction of travel in the x-direction
			if (X<0){
				tempX *= -1;
			}

			//Accounts for the direction of travel in the y-direction
			if (Y<0){
				tempY *= -1;
			}

			difX = tempX;
			difY = tempY;

		}

		//If the target location can be reached, defines the total distance needed to move
		else{
			difX = target.getAvenue() - this.getAvenue();
			difY = target.getStreet() - this.getStreet();
		}

		targetX = this.getAvenue() + difX;
		targetY = this.getStreet() + difY;
	}

	/**
	 * Prepares the extended OppData[] named GuptaOppData[]
	 * @param data - The OppData[] object that is then converted into extendedData, a GuptaOppData[] object
	 * @return - Returns the extendedData, the updated data
	 */
	private GuptaOppData[] extendOppData(OppData[] data) {
		extendedData = new GuptaOppData[data.length];
		
		//Creates a new GuptaOppData object
		for (int i = 0; i<data.length; i++){
			extendedData[i] = new GuptaOppData(data[i].getID(), data[i].getAvenue(), data[i].getStreet(), data[i].getHealth());
		}
		return extendedData;
	}

	/**
	 * Sorts the health of each player from lowest to highest
	 * @return - Returns a GuptaOppData object with the sorted health from lowest to highest
	 */
	private GuptaOppData[] sortHealth() {
		GuptaOppData temp;
		//INSERTION SORT
		int previousIndex;
		
		//Moves dead participants to the end of the array
		for (int index = 0; index<extendedData.length; index++){
			
			//Moves dead players to the back of the array
			if (extendedData[index].getHealth() == 0){
				extendedData[index].setHealth(101);
			}
		}

		//Runs for the length of the array minus 1 and sorts the array
		for (int index= 1; index<extendedData.length; index++){
			temp = extendedData[index];
			previousIndex = index - 1;

			//Sorts the sorted array
			while ((extendedData[previousIndex].getHealth() > temp.getHealth()) && (previousIndex > 0)){
				extendedData[previousIndex + 1] = extendedData[previousIndex]; 
				previousIndex -= 1;
			}

			//Sorts the unsorted array
			if (extendedData[previousIndex].getHealth() > temp.getHealth()){

				// shift item in first element up into next element  
				extendedData[previousIndex + 1] = extendedData[previousIndex];   

				// place current item at index 0 (first element)
				extendedData[previousIndex] = temp;
			}
			else{

				// place current item at index ahead of previous item
				extendedData[previousIndex + 1] = temp;  }
		}

		return extendedData;
	}

	/**
	 * Subtracts the health from this robot that it lost while in the battlefield and updates its displayed health
	 */
	public void battleResult(int healthLost, int oppID, int oppHealthLost, int numRoundsFought){
		this.health -= healthLost;
		this.setLabel();
	}
}