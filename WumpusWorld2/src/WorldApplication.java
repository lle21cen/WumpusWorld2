/*
 * Wumpus-Lite, version 0.21 alpha
 * A lightweight Java-based Wumpus World Simulator
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Thanks to everyone who provided feedback and
 * suggestions for improving this application,
 * especially the students from Professor
 * Gmytrasiewicz's Spring 2007 CS511 class.
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

class WorldApplication {

	private static String VERSION = "v0.21a";
	String gameboard = "";
	String out = "";
	int numTrials;
	int maxSteps;
	int worldSize;
	int hurestic;

	public static void main(String args[]) throws Exception {

		long startTime = System.currentTimeMillis();
		WorldApplication wa = new WorldApplication();
		boolean nonDeterministicMode = false;


		if (wa.readPara(args) == 6) {

			FileWriter fw = new FileWriter(wa.out);
			
			int trialScores[] = new int[wa.numTrials];
			String trialStateSeqs[] = new String[wa.numTrials];
			String path[] = new String[wa.numTrials];
			int totalScore = 0;

			for (int currTrial = 0; currTrial < wa.numTrials; currTrial++) {

				char[][][] wumpusWorld = readWumpusWorld(wa.worldSize, wa.gameboard);

				Environment wumpusEnvironment = new Environment(wa.worldSize, wumpusWorld);

				Simulation trial = new Simulation(wumpusEnvironment, wa.maxSteps, nonDeterministicMode, wa.hurestic); // ,
																														// outputWriter,
																														// nonDeterministicMode);
				trialScores[currTrial] = trial.getScore();
				trialStateSeqs[currTrial] = trial.getStateSeq();
				path[currTrial] = trial.getpath();

			}
			

			for (int i = 0; i < wa.numTrials; i++) {

				fw.write("\nTrial " + (i + 1) + " score: " + trialScores[i] + "\n");
				fw.write("Trial " + (i + 1) + " StateSeq: " + trialStateSeqs[i] + "\n");
				fw.write("Trial " + (i + 1) + " path: " + path[i] + "\n");

				totalScore += trialScores[i];

			}
			long endTime = System.currentTimeMillis();

			fw.write("\nNumber of trials: " + wa.numTrials + "\n");
			fw.write("Total Score: " + totalScore + "\n");
			fw.write("Average Score: " + ((double) totalScore / (double) wa.numTrials) + "\n");
			fw.write("Execution Time: " + (endTime - startTime)+"\n");

			fw.close();

		} else {
			wa.usage();
		}

	}

	private void usage() {

		System.out.println("Usage:\n\n-i gameboard.txt");
		System.out.println("-o output.txt");
		System.out.println("-n number of trails");
		System.out.println("-ms max steps");
		System.out.println("-ws world size");
		System.out.println("-h heuristic number 1,2,3,4,5");

		System.out.println("\njava WorldApplication -i gameborad.txt -o output.txt -n 1 -ms 50 -ws 4 -h 1");

	}

	private int readPara(String args[]) {

		int n = 0;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				this.gameboard = args[i + 1];
				n++;
			} else if (args[i].equals("-o")) {
				this.out = args[i + 1];
				n++;
			} else if (args[i].equals("-n")) {
				this.numTrials = Integer.parseInt(args[i + 1]);
				n++;
			} else if (args[i].equals("-ms")) {
				this.maxSteps = Integer.parseInt(args[i + 1]);
				n++;
			} else if (args[i].equals("-ws")) {
				this.worldSize = Integer.parseInt(args[i + 1]);
				n++;
			} else if (args[i].equals("-h")) {
				this.hurestic = Integer.parseInt(args[i + 1]);
				n++;
			}
		}

		return n;
	}

	public static char[][][] readWumpusWorld(int size, String gameboard) throws Exception {

		char[][][] newWorld = new char[size][size][4];
		// to do
		String s;
		int lineX = 0, lineY = size;

		BufferedReader br = new BufferedReader(new FileReader(gameboard));
		while ((s = br.readLine()) != null) {
			if (s.contains(" ----")) {
				lineY--;
				continue;
			}
			StringTokenizer parse = new StringTokenizer(s, "|");
			while (parse.hasMoreTokens()) {
				String token = parse.nextToken();
				if (token.contains("P")) {
					newWorld[lineY][lineX][0] = 'P';
				} else if (token.contains("W")) {
					newWorld[lineY][lineX][1] = 'W';
				} else if (token.contains("G")) {
					newWorld[lineY][lineX][2] = 'G';
				} else if (token.contains("A")) {
					newWorld[lineY][lineX][3] = 'A';
				} else if (token.contains(">")) {
					newWorld[lineY][lineX][3] = '>';
				} else if (token.contains("<")) {
					newWorld[lineY][lineX][3] = '<';
				} else if (token.contains("V")) {
					newWorld[lineY][lineX][3] = 'V';
				}
				lineX++;
			}
			lineX = 0;
		}
		br.close();
		return newWorld;
	}
}