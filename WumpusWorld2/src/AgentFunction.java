
/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.util.Random;

class AgentFunction {

	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";

	private int[] actionTable;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private Random rand;

	// add
	char[][][] world;
	int size;
	int heuristic;
	private int nx[] = new int[4];
	private int ny[] = new int[4];
	int[] location;
	Environment environment;
	int prevX = -2, prevY = -2;
	int back = 2;
	int prevAction = -1;
	int[][] f;
	int step = 0;
	boolean isBack = false;

	public AgentFunction(Environment env, int worldSize, int heuristic, int ms) {

		// this integer array will store the agent actions
		actionTable = new int[8];

		actionTable[0] = Action.GO_FORWARD;
		actionTable[1] = Action.GO_FORWARD;
		actionTable[2] = Action.GO_FORWARD;
		actionTable[3] = Action.GO_FORWARD;
		actionTable[4] = Action.TURN_RIGHT;
		actionTable[5] = Action.TURN_LEFT;
		actionTable[6] = Action.GRAB;
		actionTable[7] = Action.SHOOT;

		// new random number generator, for
		// randomly picking actions to execute
		rand = new Random();

		// add
		environment = env;
		world = env.getWumpusWorld();
		size = worldSize;
		this.heuristic = heuristic;
		nx[0] = 1;
		nx[1] = 0;
		nx[2] = -1;
		nx[3] = 0; // N,E,S,W
		ny[0] = 0;
		ny[1] = 1;
		ny[2] = 0;
		ny[3] = -1;
		location = new int[2];
		location[0] = -1;
		location[1] = -1;
		f = new int[ms][4];
	}

	public int process(TransferPercept tp, char direction) {

		// read in the current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();
		

		if (prevAction == Action.GO_FORWARD) {
			prevY = location[0];
			prevX = location[1];
		}
		System.out.println("step = " + step);

		location = getAgentLocation();

		int i;
		int y = location[0];
		int x = location[1];

		int heuristic_cost = 10000;
		int[] gold_loc = getGoldLocation();
		int index = -1; // =i;
		int[] can = new int[4];

		if (heuristic == 1) {
			System.out.println("Direction and Location " + direction + " " + x + " " + y);
			// MANHATAN DISTANCE
			for (i = 0; i < 4; i++) {
				if (f[step][i] > 0 && isBack)
					break;
				if (f[step][i] == 9999998)
					continue;
				heuristic_cost = Math.abs(gold_loc[0] - (y + ny[i])) + Math.abs(gold_loc[1] - (x + nx[i]));
				f[step][i] = heuristic_cost;
				System.out.println(heuristic_cost);
				
				if (world[y][x][2] == 'G') {
					return Action.GRAB;
				}
			}
		}

		if (heuristic >= 2) {
			System.out.println("Direction and Location " + direction + " " + x + " " + y);
			// Euclidean Distance
			for (i = 0; i < 4; i++) {
				if (f[step][i] > 0 && isBack)
					break;
				heuristic_cost = (int) (Math.pow((gold_loc[0] - (y + ny[i])), 2)
						+ Math.pow(gold_loc[1] - (x + nx[i]), 2));
				f[step][i] = heuristic_cost;

				if (world[y][x][2] == 'G') {
					return Action.GRAB;

				}
			}
		}

		int min_cost = 9999999;

		for (i = 0; i < 4; i++) {
			if (!(0 <= y + ny[i] && y + ny[i] < size && 0 <= x + nx[i] && x + nx[i] < size)) {
				f[step][i] = 99999999;
				continue;
			}
			if (world[y + ny[i]][x + nx[i]][0] == 'P' || world[y + ny[i]][x + nx[i]][1] == 'W')
				f[step][i] = 99999999;
			if (min_cost >= f[step][i]) {
				min_cost = f[step][i];
				can[i] = min_cost;
				index = i;
			}
		}
		index = getOptimalIndex(can, direction, index);

		prevAction = getAgentActionFromIndex(direction, index);
		if (prevAction == Action.GO_FORWARD && y + ny[index] == prevY && x + nx[index] == prevX) {
			isBack = true;
			step--;
		} else if (prevAction == Action.GO_FORWARD) {
			isBack = false;
			step++;
		}
		if (index == 0)
			f[step][2] = 9999998;
		else if (index == 1)
			f[step][3] = 9999998;
		else if (index == 2)
			f[step][0] = 9999998;
		else if (index == 3)
			f[step][1] = 9999998;
		else
			System.out.println("step = " + step);
		return prevAction;

	}

	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}

	int[] getGoldLocation() {
		int[] gold_location = new int[2];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (world[i][j][2] == 'G') {
					gold_location[0] = i;
					gold_location[1] = j;
					System.out.println("Gold Location = " + i + ", " + j);
					return gold_location;
				}
			}
		}
		return gold_location;
	}

	int[] getAgentLocation() {
		int[] agent_location = new int[2];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (world[i][j][3] == '<' || world[i][j][3] == 'A' || world[i][j][3] == '>' || world[i][j][3] == 'V') {
					agent_location[0] = i;
					agent_location[1] = j;
					return agent_location;
				}
			}
		}
		return agent_location;
	}

	int getAgentActionFromIndex(char dir, int index) {
		System.out.println("direction = " + dir + " index = " + index);
		if (dir == 'E') {
			if (index == 0)
				return Action.GO_FORWARD;
			else if (index == 1)
				return Action.TURN_LEFT;
			else if (index == 2)
				return Action.TURN_LEFT;
			else if (index == 3)
				return Action.TURN_RIGHT;

		} else if (dir == 'W') {
			if (index == 0)
				return Action.TURN_LEFT;
			else if (index == 1)
				return Action.TURN_RIGHT;
			else if (index == 2)
				return Action.GO_FORWARD;
			else if (index == 3)
				return Action.TURN_LEFT;
		} else if (dir == 'S') {
			if (index == 0)
				return Action.TURN_LEFT;
			else if (index == 1)
				return Action.TURN_LEFT;
			else if (index == 2)
				return Action.TURN_RIGHT;
			else if (index == 3)
				return Action.GO_FORWARD;
		} else {
			if (index == 0)
				return Action.TURN_RIGHT;
			else if (index == 1)
				return Action.GO_FORWARD;
			else if (index == 2)
				return Action.TURN_LEFT;
			else if (index == 3)
				return Action.TURN_LEFT;
		}
		return -1;
	}

	int getOptimalIndex(int[] can, char dir, int cur_index) {

		int min_cost = 9999999;
		for (int i = 0; i < 4; i++) {
			if (can[i] > 0) {
				if (min_cost > can[i]) {
					min_cost = can[i];
				}
			}
		}
		for (int i = 0; i < 4; i++)
			if (can[i] == min_cost)
				can[i] = 1;

		if (dir == 'E') {
			if (can[0] == 1)
				return 0;
			else if (can[1] == 1)
				return 1;
			else if (can[3] == 1)
				return 3;
		} else if (dir == 'W') {
			if (can[2] == 1) {
				return 2;
			} else if (can[1] == 1) {
				return 1;
			} else if (can[3] == 1) {
				return 3;
			}
		} else if (dir == 'S') {
			if (can[3] == 1)
				return 3;
			else if (can[0] == 1)
				return 0;
			else if (can[2] == 1)
				return 2;
		} else {
			if (can[1] == 1)
				return 1;
			else if (can[0] == 1)
				return 0;
			else if (can[2] == 1)
				return 2;
		}
		return cur_index;
	}
}