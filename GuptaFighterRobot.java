package RobotWarSummative;
import becker.robots.City;

import java.awt.Color;
import java.lang.Math;
import java.util.*;
import becker.robots.*;

public class GuptaFighterRobot extends FighterRobot{

	int health;

	public GuptaFighterRobot(City c, int a, int s, Direction d,int id, int health){
		super(c,a,s,d,id,health, 4, 2, 10);
		this.health = health;
		this.setLabel();
	}

	//avenue (x-axis or s) street (y-axis or a)
	public void goToLocation(int s, int a){
		int y = this.getStreet() - a;
		int x = this.getAvenue() - s;

		if (x>0){
			super.turnLeft();
			for (int i = 0; i<x; i++){
				super.move();
			}
			super.turnRight();
		}
		else{
			super.turnRight();
			for (int i = x; i<0; i++){
				super.move();
			}
			super.turnLeft();
		}
		
		if (y>0){
			for (int i = 0; i<y; i++){
				super.move();
			}
		}
		else{
			super.turnAround();
			for (int i = y; i<0; i++){
				super.move();
			}
			super.turnAround();
		}
	}

	public TurnRequest takeTurn(int energy, OppData[] data){
		int[][] health = new int[BattleManager.NUM_PLAYERS][1];
		
		//FIlls the health array with the id number of a robot and its health
		/*for (int i = 0; i<health.length; i++){
			health[i][0] = i;
			health[i][1] = data[i].getHealth();
		}*/
		
		//health = this.sort(health);
		
		/*for (int i = 0; i<BattleManager.NUM_PLAYERS; i++){
			constructor from guptaoppData(oppData[i]);
			
			
			data[i] = new OppData(i, player[i].getAvenue(), player[i].getStreet(), health[i]);
		}*/
		
		/*for (int i = 0; i<9; i++){
			System.out.println("\n\n\n\n\n\n\n\n\n" + data[i] + "\n\n\n\n\n\n");
		}*/
		
				
		//avenue, street, id of player to fight(-1 = no fight), number of rounds
		TurnRequest request = new TurnRequest(5,4,-1,0);
		
		return request;
	}

	private int[] sort(int[] health) {
		
		return null;
	}

	public void battleResult(int healthLost, int oppID, int oppHealthLost, int numRoundsFought){
		this.health -= healthLost;
	}

	public void setLabel(){
		this.setLabel(super.getID() + " " + this.health);
		
		if (this.health == 0){
			this.setColor(Color.BLACK);
		}
		else{
			this.setColor(Color.RED);
		}

	}
}