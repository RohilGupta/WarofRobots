package RobotWarSummative;
import becker.robots.City;

import java.awt.Color;
import java.lang.Math;
import java.util.*;
import becker.robots.*;

/**
 * This is the main fighter robot that utilizes GuptaOppData to make calculations
 * @author Rohil G.
 * @date January 24, 2017
 */
public class Rohbot extends FighterRobot{

	//Defines the private global variables to be used
	private int health;
	private GuptaOppData[] extendedData = null;
	private final static int ATTACK = 6;
	private final static int DEFENCE = 3;
	private final static int NUMMOVES = 1;
	private static int health_modifier = 50;
	private static int distance_modifier = 75;
	private static int moshpit_modifier = 75;
	private static int numMoves_modifier = 30;
	private int targetX, targetY, targetID, finalX, finalY;
	private int energy, counter = 0;
	private boolean allInMoshpit = true;

	/**
	 * The constructor method for the main fighter robot to be used
	 * @param c - Defines the city the robot is in
	 * @param a - Defines the x-coordinate of the robot
	 * @param s - Defines the y-coordinate of the robot
	 * @param d - Defines the direction the robot is facing
	 * @param id - Defines the id of the robot
	 * @param health - Defines the health of the robot
	 */
	public Rohbot(City c, int a, int s, Direction d,int id, int health){
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
			this.setColor(Color.RED);
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
		extendedData = extendOppData(data);
		GuptaOppData target;

		target = getTarget(); //Gets the target to attack
		this.goToTarget(target); //Sets the targetX and targetY to get closer to or attack the target
		
		//If everyone is involved in a moshpit, certain modifiers are changed
		if (allInMoshpit){
			distance_modifier = 150;
			health_modifier = 100;
			moshpit_modifier = 100;
			numMoves_modifier = 25;
		}

		//If the robot's health is lower than or equal to 5, robot moves to the closest corner
		if (this.health <= 5){
			this.closestCorner();
			this.goToTarget(finalX, finalY);
			request = new TurnRequest(targetX,targetY,targetID, 0);
		}
		else{

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
		}

		return request;
	}

	/**
	 * Calculates the closest corner for the robot to move into
	 */
	private void closestCorner() {
		int[] dist = new int[4];
		int minDist;

		//Determines the total distance that each opponent in the battlefield is away from each corner
		for(int i = 0; i<extendedData.length; i++){

			//Only collects the other robot's information if they are alive
			if (extendedData[i].getHealth() > 0){
				dist[0] += extendedData[i].getDistance(0, 0);
				dist[1] += extendedData[i].getDistance(19, 0);
				dist[2] += extendedData[i].getDistance(0, 11);
				dist[3] += extendedData[i].getDistance(19, 11);
			}
		}

		targetID = -1;

		//Finds the lowest distance to a corner
		minDist = Math.min(Math.min(dist[0], dist[1]), Math.min(dist[2], dist[3]));

		//Runs for the length of the dist array (4)
		for (int i = 0; i<dist.length; i++){

			//Determines which corner provides the minimum distance
			if (dist[i] == minDist){

				//If it is the first corner, this corner's location is saved
				if (i == 0){
					finalX = 0;
					finalY = 19;
					break;
				}

				//If it is the second corner, this corner's location is saved
				else if (i == 1){
					finalX = 19;
					finalY = 0;
					break;
				}

				//If it is the third corner, this corner's location is saved
				else if (i == 2){
					finalX = 0;
					finalY = 11;
					break;
				}

				//If it is the fourth corner, this corner's location is saved
				else{
					finalX = 19;
					finalY = 11;
					break;
				}
			}
		}


	}

	/**
	 * An overloaded method which moves the robot as close to the target location as possible
	 * @param x - The target x-coordinate
	 * @param y- The target y-coordinate
	 */
	private void goToTarget(int x, int y) {
		int limitingNum, difX, difY, position = 0;

		//Determines the limiting number of moves by comparing the energy of the robot with the number of moves it is allowed to take
		if (this.energy/5 > NUMMOVES){
			limitingNum = NUMMOVES;
		}
		else{
			limitingNum = energy/5;
		}

		//Runs for the duration of the extendedData array
		for (int i = 0; i<extendedData.length; i++){

			//Determines the position of this robot in the extendedData array by ID
			if (super.getID() == extendedData[i].getID()){
				position = i;
				break;
			}
		}

		//Determines if the distance from this robot to the target location is greater than the limiting number calculated above 
		if (extendedData[position].getDistance(x,y) > limitingNum){

			//Defines the variables to be used
			difX = Math.abs(extendedData[position].getAvenue() - x);
			difY = Math.abs(extendedData[position].getStreet() - y);
			int X = extendedData[position].getAvenue() - x;
			int Y = extendedData[position].getStreet() - y;
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

		//Occurs if the robot can reach its target position
		else{
			difX = this.extendedData[position].getAvenue() - x;
			difY = this.extendedData[position].getStreet() - y;
		}

		this.targetX = this.getAvenue() + difX;
		this.targetY = this.getStreet() + difY;
	}

	/**
	 * Overloaded method which moves the robot as close to the target location as possible
	 * @param target - The GuptaOppData object that is to be attacked
	 */
	private void goToTarget(GuptaOppData target) {

		//Defines the variables to be used
		int limitingNum, difX, difY;

		//Determines the limiting number of moves by comparing the energy of the robot with the number of moves it is allowed to take
		if (this.energy/5 > NUMMOVES){
			limitingNum = NUMMOVES;
		}
		else{
			limitingNum = this.energy/5;
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

		this.targetX = this.getAvenue() + difX;
		this.targetY = this.getStreet() + difY;
	}

	/**
	 * Determines the best target to attack based on four different algorithms
	 * @return - Returns the target that is to be attacked
	 */
	private GuptaOppData getTarget() {
		//Updates the mosh pit values of all of the opponents
		this.findMoshpit();

		//Defines the four algorithms to be used
		double[] scaledHealth = scale(extendedData, "H");
		double[] scaledDistance = scale(extendedData, "D");
		double[] scaledMoshpit = scale(extendedData, "M");
		double[] scaledNumMoves = scale(extendedData, "N");

		double[] points = new double[extendedData.length];
		GuptaOppData[] sortedPoints;

		//Runs for the duration of the extendedData array and calculates and sets the points for each player
		for (int i = 0; i<extendedData.length; i++){
			points[i] = health_modifier*scaledHealth[i] + distance_modifier*scaledDistance[i] + moshpit_modifier*scaledMoshpit[i] + numMoves_modifier*scaledNumMoves[i];			
			this.extendedData[i].setPoints(points[i]);
		}

		sortedPoints = this.sortPoints(); //Sorts these points from lowest to highest

		//Runs for the duration of the extendedData array
		for(int i = 0; i<extendedData.length; i++){
			
			//Returns the player with the lowest points, making sure that that player is not this robot and that it is alive
			if (sortedPoints[i].getHealth() > 0 && sortedPoints[i].getID() != this.getID()){
				return sortedPoints[i];
			}
		}

		return sortedPoints[8];
	}

	/**
	 * Finds the "mosh pit value" of every player
	 */
	private void findMoshpit() {
		
		//Defines the variables to be used
		int count, tempX = 0, tempY = 0, sum;

		//Resets the mosh pit values of the players
		for(int i = 0; i<this.extendedData.length; i++){
			this.extendedData[i].setInMoshpit(false);
			if (this.extendedData[i].getID() == this.getID()){
				this.extendedData[i].setInMoshpit(true);
			}
		}

		//Finds the coordinates of the "moshpits" in the arena
		for(int i = 0; i<this.extendedData.length-1; i++){

			count = 0;
			sum = 0;

			//Checks every player that has not yet been compared
			for(int j = i+1; j<this.extendedData.length; j++){
				
				//Determines if the two robots in the loop are at the same intersection. If so, the two players are in a moshpit
				if (this.extendedData[i].getStreet() == this.extendedData[j].getStreet() && this.extendedData[i].getAvenue() == this.extendedData[j].getAvenue()){
					
					//Determines if this is the first time entering the condition
					if (count == 0){
						
						//Adds the health of the discovered robot to the sum value
						sum += this.extendedData[i].getHealth();
						this.extendedData[i].setInMoshpit(true);
					}
					
					//Adds the health of the discovered robot to the sum value
					sum += this.extendedData[j].getHealth();
					this.extendedData[j].setInMoshpit(true);
					count ++;
				}
			}

			//Only enters this statement if the location of the mosh pit has not yet been explored before
			if (count > 0 && tempX != extendedData[i].getAvenue()){
				tempX = this.extendedData[i].getAvenue();
				tempY = this.extendedData[i].getStreet();

				//Runs for the duration of the extendedData array
				for(int j = 0; j<this.extendedData.length; j++){
					
					//Checks to see if the new robot discovered intersects the old robot discovered
					if (extendedData[j].getAvenue() == tempX && extendedData[j].getStreet() == tempY){
						
						//Checks to see if the intersection includes this robot, in which its health is subtracted from the sum of the moshpit
						if (this.extendedData[j].getAvenue() == this.getAvenue() && this.extendedData[j].getStreet() == this.getStreet()){
							this.extendedData[j].setMoshpit(sum - this.health);
						}
						
						//Sets the mosh pit value of every player on the intersection
						else{
							this.extendedData[j].setMoshpit(sum);
						}
					}
				}
			}
		}

		allInMoshpit = true;

		//Runs for the duration of the extendedData array
		for(int i = 0; i<extendedData.length; i++){
			
			//Determines if every alive player is in a moshpit
			if (extendedData[i].getInMoshpit() == false && extendedData[i].getHealth() > 0){
				allInMoshpit = false;
			}
		}

	}


	/**
	 * Scales the values of the entered data down to between 0 and 1
	 * @param unscaled - A GuptaOppData[] object that is scaled down in a specific aspect, specified in the "type" parameter
	 * @param type - A character used to determine the aspect, such as health or distance, being scaled down
	 * @return - Returns an integer array of the scaled down values
	 */
	private double[] scale(GuptaOppData[] unscaled, String type) {
		//Defines the variables to be used
		double[] scale = new double[unscaled.length];
		double max = 0;

		//Scales the values of the health down;
		if (type.compareTo("H") == 0){

			//Runs for the duration of the unscaled array
			for (int i = 0; i<unscaled.length; i++){

				//Finds the maximum health in the unscaled values
				if (unscaled[i].getHealth() > max){
					max = unscaled[i].getHealth();
				}
			}

			//Scales the values down to between 0 and 1
			for (int i = 0; i<scale.length; i++){
				scale[i] = unscaled[i].getHealth() / max;
			}

			return scale;
		}

		//Scales the values of the distance down
		else if (type.compareTo("D") == 0){

			//Runs for the duration of the unscaled array
			for (int i = 0; i<unscaled.length; i++){

				//Finds the maximum distance in the unscaled values
				if (unscaled[i].getDistance(this.getAvenue(), this.getStreet()) > max){
					max = unscaled[i].getDistance(this.getAvenue(), this.getStreet());
				}
			}

			//Scales the values down to between 0 and 1
			for (int i = 0; i<scale.length; i++){
				scale[i] = unscaled[i].getDistance(this.getAvenue(), this.getStreet()) / max;
			}

			return scale;
		}

		//Scales the values of the moshpit down;
		else if (type.compareTo("M") == 0){

			//Runs for the duration of the unscaled array
			for (int i = 0; i<unscaled.length; i++){

				//Finds the maximum mosh pit value in the unscaled values
				if (unscaled[i].getMoshpit() > max){
					max = unscaled[i].getMoshpit();
				}
			}

			//Ensures no division by 0
			if (max == 0){
				max = 1;
			}

			//Scales the values down to between 0 and 1
			for (int i = 0; i<scale.length; i++){
				scale[i] = unscaled[i].getMoshpit() / max;
			}

			return scale;
		}

		//Scales the values of the numMoves down;
		else if (type.compareTo("N") == 0){

			//Runs for the duration of the unscaled array
			for (int i = 0; i<unscaled.length; i++){

				//Finds the maximum numMoves in the unscaled values
				if (unscaled[i].getNumMoves() > max){
					max = unscaled[i].getNumMoves();
				}
			}

			//Ensures no division by 0
			if (max == 0){
				max = 1;
			}

			//Scales the values down to between 0 and 1
			for (int i = 0; i<scale.length; i++){
				scale[i] = unscaled[i].getNumMoves() / max;
			}

			return scale;
		}

		return scale;
	}

	/**
	 * Prepares the extended OppData[] named GuptaOppData[]
	 * @param data - The OppData[] object that is then converted into extendedData, a GuptaOppData[] object
	 * @return - Returns the extendedData, the updated data
	 */
	private GuptaOppData[] extendOppData(OppData[] data) {

		//Determines if this is at least the second time through the array
		if (counter > 0){
			
			//Runs for the length of the data array
			for (int i = 0; i<data.length; i++){
				
				//Runs for the length of the data array
				for(int j = 0; j<data.length; j++){
					
					//Matches the extendedData ID to the data ID
					if (extendedData[i].getID() == data[j].getID()){
						
						//Updates the values of the extendedData instead of creating a new extendedData
						extendedData[i].setNumMoves(data[j].getAvenue(), data[j].getStreet(), counter, extendedData[i].getNumMoves());
						extendedData[i].setHealth(data[j].getHealth());
						extendedData[i].setAvenue(data[j].getAvenue());
						extendedData[i].setStreet(data[j].getStreet());
						
						break;
					}
				}
			}
		}

		//Enters if this is the first time running through this loop
		else{
			extendedData = new GuptaOppData[data.length];

			//Runs for the duration of the old OppData[] object and loads the new extendedData
			for (int i = 0; i<data.length; i++){
				extendedData[i] = new GuptaOppData(data[i].getID(), data[i].getAvenue(), data[i].getStreet(), data[i].getHealth());
			}

			counter++;
		}

		return extendedData;
	}

	/**
	 * Sorts the points of each robot from lowest to highest using an insertion sort
	 * @return - Returns the sorted array from lowest to highest
	 */
	private GuptaOppData[] sortPoints(){
		//Defines the variables to be used
		GuptaOppData temp;
		int previousIndex;

		//Runs for the length of the array minus 1 and sorts the array
		for (int index= 1; index<extendedData.length; index++){
			temp = extendedData[index];
			previousIndex = index - 1;

			//Sorts the sorted array
			while ((extendedData[previousIndex].getPoints() > temp.getPoints()) && (previousIndex > 0)){
				extendedData[previousIndex + 1] = extendedData[previousIndex]; 
				previousIndex -= 1;
			}

			//Sorts the unsorted array
			if (extendedData[previousIndex].getPoints() > temp.getPoints()){

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