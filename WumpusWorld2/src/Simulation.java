/*
 * Class that defines the simulation environment.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 4/14/08 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.util.*;

class Simulation {
	
	private int currScore = 0;
	private static int actionCost = 1;
	private static int deathCost = 0;
	private static int shootCost = 2;
	private static int goldCost = 0;
	private int stepCounter = 0;
	private int lastAction = 0;
	
	private boolean simulationRunning;
	
	private Agent agent;
	private Environment environment;
	private TransferPercept transferPercept;
	
	private ArrayList<Integer> StateSeq=new ArrayList<Integer>();
	private ArrayList<Integer[]> path=new ArrayList<Integer[]>();
	

	
	public Simulation(Environment wumpusEnvironment, int maxSteps, boolean nonDeterministic, int heuristic) { 
	
		simulationRunning = true;

		transferPercept = new TransferPercept(wumpusEnvironment);
		environment = wumpusEnvironment;

		agent = new Agent(environment, transferPercept, nonDeterministic, heuristic, maxSteps);

		environment.placeAgent(agent);
//		environment.printEnvironment();
//
//		printCurrentPerceptSequence();

		StateSeq.add(lastAction);
		Integer[] a = new Integer[2];
		a[0] = environment.getAgentLocation()[0];
		a[1] = environment.getAgentLocation()[1];
		path.add(a);
		
		try {

			System.out.println("Current score: " + currScore);

			while (simulationRunning == true && stepCounter < maxSteps) {

				System.out.println("Last action: " + Action.printAction(lastAction));

				System.out.println("Time step: " + stepCounter);

				int action = agent.chooseAction();
				handleAction(action);
				StateSeq.add(action);
								
				wumpusEnvironment.placeAgent(agent);
//				environment.printEnvironment();
				
				a = new Integer[2];
				a[0] = agent.getLocation()[0];
				a[1] = agent.getLocation()[1];
				path.add(a);
				
//			printCurrentPerceptSequence();

				System.out.println("Current score: " + currScore);

				stepCounter += 1;

				if (stepCounter == maxSteps || simulationRunning == false) {
					System.out.println("Last action: " + Action.printAction(lastAction));

					System.out.println("Time step: " + stepCounter);

					lastAction = Action.END_TRIAL;
					StateSeq.add(lastAction);
					
				}

				if (agent.getHasGold() == true) {
					System.out.println("\n" + agent.getName() + " found the GOLD!!");

				}
				if (agent.getIsDead() == true) {
					System.out.println("\n" + agent.getName() + " is DEAD!!");

				}

			}

		} catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
			e.printStackTrace();
		}

		printEndWorld();
	}
	
	public String getStateSeq()
	{
		String seq=Action.printAction(StateSeq.get(0))+",";
		
		for(int i=1;i<StateSeq.size()-1;i++)
		{
			seq+=(i)+"_"+Action.printAction(StateSeq.get(i))+",";
		}
		
		seq+=Action.printAction(StateSeq.get(StateSeq.size()-1));
		
		return seq;
	}
	public String getpath()
	{
		String path="("+this.path.get(0)[0]+","+this.path.get(0)[1]+")";
		
		for(int i=1;i<this.path.size();i++)
			path+=",("+this.path.get(i)[0]+","+this.path.get(i)[1]+")";
		
		return path;
	}
	
	public int getScore()
	{	
		for(int i=0;i<StateSeq.size();i++)
			handleAction(StateSeq.get(i));
		
		return currScore;
	}
	public void printEndWorld() {		
		try {
			
			environment.printEnvironment();
			
			System.out.println("Final score: " + currScore);
			System.out.println("Last action: " + Action.printAction(lastAction));
			

		}
		catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}
	}
	
	public void printCurrentPerceptSequence() {
		
		try {
		
			System.out.print("Percept: <");	
			
			
			if (transferPercept.getBump() == true) {
				System.out.print("bump,");
				
			}
			else if (transferPercept.getBump() == false) {
				System.out.print("none,");
				
			}
			if (transferPercept.getGlitter() == true) {
				System.out.print("glitter,");
				
			}
			else if (transferPercept.getGlitter() == false) {
				System.out.print("none,");
				
			}
			if (transferPercept.getBreeze() == true) {
				System.out.print("breeze,");
				
			}
			else if (transferPercept.getBreeze() == false) {
				System.out.print("none,");
				
			}
			if (transferPercept.getStench() == true) {
				System.out.print("stench,");
				
			}
			else if (transferPercept.getStench() == false) {
				System.out.print("none,");
				
			}
			if (transferPercept.getScream() == true) {
				System.out.print("scream>\n");
				
			}
			else if (transferPercept.getScream() == false) {
				System.out.print("none>\n");
				
			}
		
		}
		catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}
		
	}
	
	public void handleAction(int action) {
		
		try {
		
			if (action == Action.GO_FORWARD) {
				
				if (environment.getBump() == true) environment.setBump(false);
				
				agent.goForward();
				environment.placeAgent(agent);
				
				if (environment.checkDeath() == true) {
					
					currScore += deathCost;
					simulationRunning = false;
					
					agent.setIsDead(true);
				}
				else {
					currScore += actionCost;
				}
				
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.GO_FORWARD;
			}
			else if (action == Action.TURN_RIGHT) {
				
				currScore += actionCost;
				agent.turnRight();		
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.TURN_RIGHT;
			}
			else if (action == Action.TURN_LEFT) {
				
				currScore += actionCost;
				agent.turnLeft();		
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.TURN_LEFT;
			}
			else if (action == Action.GRAB) {
				
				if (environment.grabGold() == true) {
					
					currScore += goldCost;
					simulationRunning = false;
					
					agent.setHasGold(true);
				}
				else currScore += actionCost;
				
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.GRAB;
			}
			else if (action == Action.SHOOT) {
				
				currScore += shootCost;
				
				
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				
				lastAction = Action.SHOOT;
			}
			else if (action == Action.NO_OP) {
				
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.NO_OP;
			}
			
		}
		catch (Exception e) {
			
			System.out.println("An exception was thrown: " + e);
		}
	}	
}